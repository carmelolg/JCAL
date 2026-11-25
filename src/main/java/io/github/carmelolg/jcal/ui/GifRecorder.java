package io.github.carmelolg.jcal.ui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.carmelolg.jcal.core.GenerationListener;
import io.github.carmelolg.jcal.grid.GridDimensions;
import io.github.carmelolg.jcal.grid.GridSnapshot;

/**
 * A {@link GenerationListener} that records the evolution of a 2D Cellular Automata
 * into an animated, endlessly looping GIF file.
 *
 * <p>Frames are written to disk as the generations are produced, so memory usage does
 * not grow with the number of generations. The GIF is complete only after
 * {@link #close()}, therefore use the recorder in a try-with-resources block:
 *
 * <pre>{@code
 * try (GifRecorder gif = GifRecorder.create(Path.of("life.gif"), renderer)
 *         .cellSize(8)
 *         .delay(100)
 *         .every(1)) {
 *     gif.record(GridSnapshot.of(0, ca.getGrid())); // optional: initial state
 *     rule.addGenerationListener(gif);
 *     rule.run(ca);
 * }
 * }</pre>
 *
 * <p><b>Behaviour:</b>
 * <ul>
 *   <li>The output file is created, or overwritten, when the first frame is written.
 *       If no frame is ever recorded the file is not touched. Parent directories must
 *       already exist.</li>
 *   <li>With {@link #every(int)} greater than 1, {@link #close()} still appends the last
 *       generation received, so the animation ends on the final state.</li>
 *   <li>After {@link #close()} further generations are ignored. This matters because a
 *       listener stays registered on its rule: do not expect a closed recorder to record
 *       a second run.</li>
 *   <li>A GIF frame holds at most 256 colours. Frames within this limit reproduce the
 *       renderer colours exactly; frames above it are still written, with approximated
 *       colours, and a warning is logged once.</li>
 *   <li>Translucent colours are blended over black, as in {@link GridPanel}.</li>
 *   <li>A failure while writing throws {@link UncheckedIOException}, which stops the
 *       running rule. The recorder is then closed and the file may be incomplete.</li>
 *   <li>For an infinite automaton, interrupt the execution thread and then let the
 *       try-with-resources block close the recorder. If the JVM exits before
 *       {@link #close()} (for example by closing a window created by
 *       {@link CellularAutomataUIRunner}), the file is incomplete.</li>
 * </ul>
 *
 * @author Carmelo La Gamba
 * @see CellRenderer
 * @see GenerationListener
 */
public class GifRecorder implements GenerationListener, AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(GifRecorder.class);

    private static final String METADATA_FORMAT = "javax_imageio_gif_image_1.0";
    private static final int MAX_COLORS = 256;
    private static final int MAX_SIDE = 65_535;
    private static final int MAX_DELAY_MS = 655_350;

    private final Path output;
    private final CellRenderer renderer;

    private int cellSize = 10;
    private int delayMs  = 100;
    private int every    = 1;

    private ImageWriter writer;
    private ImageOutputStream stream;
    private GridSnapshot pending;
    private int width;
    private int height;
    private boolean started;
    private boolean closed;
    private boolean colorWarningLogged;

    private GifRecorder(Path output, CellRenderer renderer) {
        this.output = output;
        this.renderer = renderer;
    }

    /**
     * Creates a recorder writing to the given file.
     *
     * @param output   the GIF file to create or overwrite, on the default file system; must not be {@code null}
     * @param renderer the {@link CellRenderer} mapping states to colours; must not be {@code null}
     * @return a new {@code GifRecorder}
     */
    public static GifRecorder create(Path output, CellRenderer renderer) {
        Objects.requireNonNull(output, "output must not be null");
        Objects.requireNonNull(renderer, "renderer must not be null");
        return new GifRecorder(output, renderer);
    }

    /**
     * Sets the pixel size of each cell (default: {@code 10}).
     *
     * @param cellSize pixels per cell; must be &gt; 0
     * @return this recorder
     * @throws IllegalArgumentException if {@code cellSize} is not positive
     * @throws IllegalStateException    if recording has already started or the recorder is closed
     */
    public synchronized GifRecorder cellSize(int cellSize) {
        checkConfigurable();
        if (cellSize <= 0) throw new IllegalArgumentException("cellSize must be > 0, got: " + cellSize);
        this.cellSize = cellSize;
        return this;
    }

    /**
     * Sets how long each frame is shown (default: {@code 100} ms).
     *
     * <p>GIF stores delays in hundredths of a second, so the value is rounded to the
     * nearest 10 ms. Many viewers slow down animations with delays below 20 ms.
     *
     * @param delayMs milliseconds per frame, between 0 and 655350
     * @return this recorder
     * @throws IllegalArgumentException if {@code delayMs} is out of range
     * @throws IllegalStateException    if recording has already started or the recorder is closed
     */
    public synchronized GifRecorder delay(int delayMs) {
        checkConfigurable();
        if (delayMs < 0 || delayMs > MAX_DELAY_MS) {
            throw new IllegalArgumentException(
                    "delayMs must be between 0 and " + MAX_DELAY_MS + ", got: " + delayMs);
        }
        this.delayMs = delayMs;
        return this;
    }

    /**
     * Records only the generations whose index is a multiple of {@code every}
     * (default: {@code 1}, all generations).
     *
     * @param every the sampling interval; must be &gt; 0
     * @return this recorder
     * @throws IllegalArgumentException if {@code every} is not positive
     * @throws IllegalStateException    if recording has already started or the recorder is closed
     */
    public synchronized GifRecorder every(int every) {
        checkConfigurable();
        if (every <= 0) throw new IllegalArgumentException("every must be > 0, got: " + every);
        this.every = every;
        return this;
    }

    /**
     * Writes the snapshot as a frame immediately, regardless of {@link #every(int)}.
     *
     * <p>Typically used for the initial state, which the engine does not notify.
     *
     * @param snapshot the 2D grid state to record; must not be {@code null}
     * @throws IllegalArgumentException if the snapshot is not 2D, its size differs from the
     *                                  previous frames, or the image would exceed GIF limits
     * @throws IllegalStateException    if the recorder is closed
     * @throws UncheckedIOException     if the file cannot be written
     */
    public synchronized void record(GridSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot must not be null");
        if (closed) throw new IllegalStateException("GifRecorder is closed");
        write(snapshot);
    }

    /**
     * Called by the engine after each completed generation. Writes a frame if the
     * generation matches the sampling interval. Ignored once the recorder is closed.
     *
     * @param generation the 1-based generation index
     * @param snapshot   the immutable grid state after the transition
     * @throws IllegalArgumentException if the snapshot cannot be recorded, see {@link #record(GridSnapshot)}
     * @throws UncheckedIOException     if the file cannot be written
     */
    @Override
    public synchronized void onGeneration(int generation, GridSnapshot snapshot) {
        if (closed) return;
        Objects.requireNonNull(snapshot, "snapshot must not be null");
        if (generation % every == 0) {
            write(snapshot);
        } else {
            pending = snapshot;
        }
    }

    /**
     * Appends the last skipped generation, if any, and completes the GIF file.
     * Calling this method more than once has no effect.
     *
     * @throws UncheckedIOException if the file cannot be completed
     */
    @Override
    public synchronized void close() {
        if (closed) return;
        try {
            if (pending != null) write(pending);
            if (started) {
                writer.endWriteSequence();
                stream.close();
                stream = null;
            } else {
                logger.warn("No frame recorded, {} was not written", output);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot complete GIF " + output, e);
        } finally {
            release();
        }
    }

    private void checkConfigurable() {
        if (started || closed) {
            throw new IllegalStateException("GifRecorder cannot be reconfigured after recording started");
        }
    }

    /** Marks the recorder closed and frees the writer and the stream, if still open. */
    private void release() {
        closed = true;
        pending = null;
        if (writer != null) {
            writer.dispose();
            writer = null;
        }
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                logger.warn("Cannot close GIF stream for {}", output, e);
            }
            stream = null;
        }
    }

    private void write(GridSnapshot snapshot) {
        GridDimensions dimensions = snapshot.getDimensions();
        checkSize(dimensions);
        BufferedImage image = render(snapshot);
        boolean written = false;
        try {
            if (!started) open();
            writer.writeToSequence(new IIOImage(image, null, metadata(image)), null);
            written = true;
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write GIF " + output, e);
        } finally {
            if (!written) release();
        }
        started = true;
        pending = null;
    }

    private void checkSize(GridDimensions dimensions) {
        if (dimensions.getDimensionCount() != 2) {
            throw new IllegalArgumentException(
                    "GifRecorder only supports 2D snapshots, got " + dimensions.getDimensionCount() + "D");
        }
        long frameWidth = (long) dimensions.getSize(0) * cellSize;
        long frameHeight = (long) dimensions.getSize(1) * cellSize;
        if (frameWidth > MAX_SIDE || frameHeight > MAX_SIDE || frameWidth * frameHeight > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "Image too large for a GIF: " + frameWidth + "x" + frameHeight + " pixels");
        }
        if (started && (frameWidth != width || frameHeight != height)) {
            throw new IllegalArgumentException("All frames must have the same size: expected "
                    + width + "x" + height + ", got " + frameWidth + "x" + frameHeight);
        }
        width = (int) frameWidth;
        height = (int) frameHeight;
    }

    private void open() throws IOException {
        // Creates or truncates the file: FileImageOutputStream alone would keep old trailing bytes.
        Files.newOutputStream(output).close();
        stream = new FileImageOutputStream(output.toFile());
        writer = ImageIO.getImageWritersByFormatName("gif").next();
        writer.setOutput(stream);
        writer.prepareWriteSequence(null);
    }

    /** Draws the snapshot, using an exact palette whenever the frame fits in 256 colours. */
    private BufferedImage render(GridSnapshot snapshot) {
        int cols = snapshot.getDimensions().getSize(0);
        int rows = snapshot.getDimensions().getSize(1);
        int[] colors = new int[cols * rows];
        Map<Integer, Integer> palette = new LinkedHashMap<>();
        boolean fitsPalette = true;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int rgb = toRgb(renderer.getColor(snapshot.getState(col, row)));
                colors[row * cols + col] = rgb;
                if (fitsPalette && !palette.containsKey(rgb)) {
                    if (palette.size() == MAX_COLORS) {
                        fitsPalette = false;
                    } else {
                        palette.put(rgb, palette.size());
                    }
                }
            }
        }
        if (fitsPalette) return renderIndexed(colors, cols, rows, palette);

        if (!colorWarningLogged) {
            colorWarningLogged = true;
            logger.warn("Frame uses more than {} colours: colours in {} are approximated", MAX_COLORS, output);
        }
        return renderRgb(colors, cols, rows);
    }

    private static int toRgb(Color color) {
        Objects.requireNonNull(color, "CellRenderer must not return null");
        int alpha = color.getAlpha();
        int red = color.getRed() * alpha / 255;
        int green = color.getGreen() * alpha / 255;
        int blue = color.getBlue() * alpha / 255;
        return red << 16 | green << 8 | blue;
    }

    private BufferedImage renderIndexed(int[] colors, int cols, int rows, Map<Integer, Integer> palette) {
        byte[] reds = new byte[MAX_COLORS];
        byte[] greens = new byte[MAX_COLORS];
        byte[] blues = new byte[MAX_COLORS];
        palette.forEach((rgb, index) -> {
            reds[index] = (byte) (rgb >> 16);
            greens[index] = (byte) (rgb >> 8);
            blues[index] = (byte) (int) rgb;
        });
        // GIF colour tables have a power-of-two size, with at least two entries.
        int bits = 32 - Integer.numberOfLeadingZeros(Math.max(2, palette.size()) - 1);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED,
                new IndexColorModel(8, 1 << bits, reds, greens, blues));
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        for (int row = 0; row < rows; row++) {
            int lineStart = row * cellSize * width;
            for (int col = 0; col < cols; col++) {
                byte index = (byte) (int) palette.get(colors[row * cols + col]);
                Arrays.fill(pixels, lineStart + col * cellSize, lineStart + (col + 1) * cellSize, index);
            }
            for (int line = 1; line < cellSize; line++) {
                System.arraycopy(pixels, lineStart, pixels, lineStart + line * width, width);
            }
        }
        return image;
    }

    private BufferedImage renderRgb(int[] colors, int cols, int rows) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] line = new int[width];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Arrays.fill(line, col * cellSize, (col + 1) * cellSize, colors[row * cols + col]);
            }
            for (int y = row * cellSize; y < (row + 1) * cellSize; y++) {
                image.setRGB(0, y, width, 1, line, 0, width);
            }
        }
        return image;
    }

    /** Builds the per-frame metadata: the delay, plus the loop extension on the first frame. */
    private IIOMetadata metadata(BufferedImage image) throws IOException {
        // Do not use ImageTypeSpecifier.createFromRenderedImage: on some JDKs it replaces
        // the palette of an indexed image with the default one, corrupting the colours.
        IIOMetadata metadata = writer.getDefaultImageMetadata(new ImageTypeSpecifier(image), null);
        IIOMetadataNode root = new IIOMetadataNode(METADATA_FORMAT);

        IIOMetadataNode control = new IIOMetadataNode("GraphicControlExtension");
        control.setAttribute("disposalMethod", "none");
        control.setAttribute("userInputFlag", "FALSE");
        control.setAttribute("transparentColorFlag", "FALSE");
        control.setAttribute("delayTime", Integer.toString((delayMs + 5) / 10));
        control.setAttribute("transparentColorIndex", "0");
        root.appendChild(control);

        if (!started) {
            IIOMetadataNode loop = new IIOMetadataNode("ApplicationExtension");
            loop.setAttribute("applicationID", "NETSCAPE");
            loop.setAttribute("authenticationCode", "2.0");
            loop.setUserObject(new byte[]{1, 0, 0}); // sub-block 1, repeat count 0 = forever
            IIOMetadataNode extensions = new IIOMetadataNode("ApplicationExtensions");
            extensions.appendChild(loop);
            root.appendChild(extensions);
        }
        metadata.mergeTree(METADATA_FORMAT, root);
        return metadata;
    }
}
