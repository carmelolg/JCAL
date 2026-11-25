package io.github.carmelolg.jcal.ui;

import io.github.carmelolg.jcal.core.AbstractCellularAutomataRule;
import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.core.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder;
import io.github.carmelolg.jcal.core.CellularAutomataParallelRule;
import io.github.carmelolg.jcal.core.CellularAutomataRule;
import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.grid.GridDimensions;
import io.github.carmelolg.jcal.grid.GridSnapshot;
import io.github.carmelolg.jcal.neighborhood.NeighborhoodType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntBinaryOperator;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GifRecorder")
class GifRecorderTest {

    private static final CellState DEAD  = new CellState("dead",  "0");
    private static final CellState ALIVE = new CellState("alive", "1");
    private static final CellRenderer TWO_COLORS = s -> s.equals(ALIVE) ? Color.GREEN : Color.BLACK;
    /** State value is the RGB colour itself. */
    private static final CellRenderer BY_VALUE = s -> new Color((Integer) s.getValue());

    @TempDir
    Path dir;

    // -- recording a run ------------------------------------------------------

    @Test
    @DisplayName("records initial state and every generation of a non-square grid, cell by cell")
    void recordsSequentialRun() throws IOException {
        assertRunRecorded(new LifeRule(), dir.resolve("seq.gif"));
    }

    @Test
    @DisplayName("records a run of the parallel rule")
    void recordsParallelRun() throws IOException {
        assertRunRecorded(new ParallelLifeRule(), dir.resolve("par.gif"));
    }

    @Test
    @DisplayName("every(n) samples generations and still ends on the last one")
    void samplingKeepsLastGeneration() throws IOException {
        Path file = dir.resolve("every.gif");
        try (GifRecorder gif = GifRecorder.create(file, BY_VALUE).cellSize(1).every(3)) {
            for (int g = 1; g <= 8; g++) gif.onGeneration(g, uniform(2, 2, g));
        }
        List<BufferedImage> frames = read(file);
        assertEquals(List.of(3, 6, 8), frames.stream().map(f -> rgb(f, 0, 0)).toList());
    }

    @Test
    @DisplayName("the last generation is not duplicated when it was already sampled")
    void noDuplicateLastFrame() throws IOException {
        Path file = dir.resolve("exact.gif");
        try (GifRecorder gif = GifRecorder.create(file, BY_VALUE).every(2)) {
            for (int g = 1; g <= 4; g++) gif.onGeneration(g, uniform(2, 2, g));
        }
        assertEquals(2, read(file).size());
    }

    @Test
    @DisplayName("a run shorter than the sampling interval still yields the final frame")
    void onlyPendingFrame() throws IOException {
        Path file = dir.resolve("pending.gif");
        try (GifRecorder gif = GifRecorder.create(file, BY_VALUE).every(100)) {
            gif.onGeneration(1, uniform(2, 2, 7));
        }
        assertEquals(1, read(file).size());
    }

    @Test
    @DisplayName("record() writes regardless of the sampling interval and clears the pending frame")
    void recordIgnoresSampling() throws IOException {
        Path file = dir.resolve("record.gif");
        try (GifRecorder gif = GifRecorder.create(file, BY_VALUE).every(10)) {
            gif.onGeneration(1, uniform(2, 2, 1));
            gif.record(uniform(2, 2, 2));
        }
        assertEquals(1, read(file).size());
    }

    @Test
    @DisplayName("an interrupted infinite run leaves a valid GIF")
    void interruptedInfiniteRun() throws Exception {
        Path file = dir.resolve("infinite.gif");
        CellularAutomata ca = new CellularAutomata(new CellularAutomataConfigurationBuilder()
                .setWidth(4).setHeight(4).setInfinite(true).setDefaultStatus(DEAD)
                .setNeighborhoodType(NeighborhoodType.MOORE).build());
        List<Throwable> errors = new ArrayList<>();
        Thread thread = new Thread(() -> {
            LifeRule rule = new LifeRule();
            try (GifRecorder gif = GifRecorder.create(file, TWO_COLORS)) {
                rule.addGenerationListener(gif);
                rule.addGenerationListener((gen, snap) -> {
                    if (gen == 5) Thread.currentThread().interrupt();
                });
                rule.run(ca);
            } catch (Throwable t) {
                errors.add(t);
            }
        });
        thread.start();
        thread.join(10_000);
        assertEquals(List.of(), errors);
        assertEquals(5, read(file).size());
    }

    // -- GIF format -----------------------------------------------------------

    @Test
    @DisplayName("writes the endless loop extension and the frame delay")
    void loopAndDelay() throws IOException {
        Path file = dir.resolve("loop.gif");
        try (GifRecorder gif = GifRecorder.create(file, BY_VALUE).delay(255)) {
            gif.record(uniform(2, 2, 1));
            gif.record(uniform(2, 2, 2));
        }
        String bytes = new String(Files.readAllBytes(file), StandardCharsets.ISO_8859_1);
        int loop = bytes.indexOf("NETSCAPE2.0");
        assertTrue(loop > 0);
        assertEquals(loop, bytes.lastIndexOf("NETSCAPE2.0"));
        assertArrayEquals(new byte[]{3, 1, 0, 0},
                bytes.substring(loop + 11, loop + 15).getBytes(StandardCharsets.ISO_8859_1));
        // Graphic Control Extension: introducer, label, block size, flags, delay (little endian)
        String controlHeader = new String(new char[]{0x21, 0xF9, 0x04});
        int control = bytes.indexOf(controlHeader);
        assertEquals(26, bytes.charAt(control + 4)); // 255 ms rounds to 26 hundredths
    }

    @Test
    @DisplayName("keeps exact colours up to 256 per frame, with a palette changing between frames")
    void exactColorsAcrossFrames() throws IOException {
        Path file = dir.resolve("palette.gif");
        int[] colorCounts = {1, 2, 3, 200, 256, 5};
        List<GridSnapshot> snapshots = new ArrayList<>();
        for (int i = 0; i < colorCounts.length; i++) {
            int count = colorCounts[i];
            int frame = i;
            snapshots.add(snapshot(20, 20, (col, row) -> ((row * 20 + col) % count) * 7919 + frame * 100_003));
        }
        try (GifRecorder gif = GifRecorder.create(file, BY_VALUE).cellSize(2)) {
            snapshots.forEach(gif::record);
        }
        assertFrames(read(file), snapshots, BY_VALUE, 2);
    }

    @Test
    @DisplayName("frames with more than 256 colours are written with approximated colours")
    void moreThan256Colors() throws IOException {
        Path file = dir.resolve("many.gif");
        try (GifRecorder gif = GifRecorder.create(file, BY_VALUE).cellSize(2)) {
            gif.record(snapshot(30, 20, (col, row) -> (row * 30 + col) * 7919));
            gif.record(snapshot(30, 20, (col, row) -> (row * 30 + col) * 104_729));
        }
        List<BufferedImage> frames = read(file);
        assertEquals(2, frames.size());
        assertEquals(60, frames.get(0).getWidth());
        assertEquals(40, frames.get(0).getHeight());
    }

    @Test
    @DisplayName("translucent colours are blended over black")
    void translucentColors() throws IOException {
        Path file = dir.resolve("alpha.gif");
        try (GifRecorder gif = GifRecorder.create(file, s -> new Color(255, 255, 255, 51))) {
            gif.record(uniform(2, 2, 0));
        }
        assertEquals(0x333333, rgb(read(file).get(0), 0, 0));
    }

    // -- file handling --------------------------------------------------------

    @Test
    @DisplayName("overwriting a larger file leaves no trailing bytes")
    void truncatesExistingFile() throws IOException {
        Path file = dir.resolve("old.gif");
        Files.write(file, new byte[100_000]);
        try (GifRecorder gif = GifRecorder.create(file, BY_VALUE)) {
            gif.record(uniform(2, 2, 1));
        }
        assertTrue(Files.size(file) < 2_000);
        assertEquals(1, read(file).size());
    }

    @Test
    @DisplayName("closing without frames does not touch the file")
    void noFrames() throws IOException {
        Path missing = dir.resolve("missing.gif");
        GifRecorder.create(missing, BY_VALUE).close();
        assertFalse(Files.exists(missing));

        Path existing = dir.resolve("existing.gif");
        Files.writeString(existing, "keep");
        GifRecorder.create(existing, BY_VALUE).close();
        assertEquals("keep", Files.readString(existing));
    }

    @Test
    @DisplayName("after close() generations are ignored, record() fails and close() is a no-op")
    void closedRecorder() throws IOException {
        Path file = dir.resolve("closed.gif");
        GifRecorder gif = GifRecorder.create(file, BY_VALUE);
        gif.record(uniform(2, 2, 1));
        gif.close();
        long size = Files.size(file);

        gif.onGeneration(2, uniform(2, 2, 2));
        gif.close();

        assertEquals(size, Files.size(file));
        assertThrows(IllegalStateException.class, () -> gif.record(uniform(2, 2, 3)));
        assertThrows(IllegalStateException.class, () -> gif.cellSize(3));
    }

    @Test
    @DisplayName("an unwritable path throws UncheckedIOException and closes the recorder")
    void unwritablePath() {
        Path file = dir.resolve("no-such-dir").resolve("a.gif");
        GifRecorder gif = GifRecorder.create(file, BY_VALUE);
        assertThrows(UncheckedIOException.class, () -> gif.record(uniform(2, 2, 1)));
        assertThrows(IllegalStateException.class, () -> gif.record(uniform(2, 2, 1)));
        assertDoesNotThrow(gif::close);
    }

    @Test
    @DisplayName("a pending frame that cannot be written makes close() fail")
    void unwritablePathOnClose() {
        GifRecorder gif = GifRecorder.create(dir, BY_VALUE).every(2); // a directory, not a file
        gif.onGeneration(1, uniform(2, 2, 1));
        assertThrows(UncheckedIOException.class, gif::close);
        assertDoesNotThrow(gif::close);
    }

    // -- validation -----------------------------------------------------------

    @Test
    @DisplayName("rejects null and out-of-range settings")
    void rejectsInvalidSettings() {
        Path file = dir.resolve("v.gif");
        assertThrows(NullPointerException.class, () -> GifRecorder.create(null, BY_VALUE));
        assertThrows(NullPointerException.class, () -> GifRecorder.create(file, null));
        GifRecorder gif = GifRecorder.create(file, BY_VALUE);
        assertThrows(IllegalArgumentException.class, () -> gif.cellSize(0));
        assertThrows(IllegalArgumentException.class, () -> gif.delay(-1));
        assertThrows(IllegalArgumentException.class, () -> gif.delay(655_351));
        assertThrows(IllegalArgumentException.class, () -> gif.every(0));
        assertThrows(NullPointerException.class, () -> gif.record(null));
        assertThrows(NullPointerException.class, () -> gif.onGeneration(1, null));
    }

    @Test
    @DisplayName("settings are frozen once recording has started")
    void frozenAfterStart() {
        try (GifRecorder gif = GifRecorder.create(dir.resolve("f.gif"), BY_VALUE)) {
            gif.record(uniform(2, 2, 1));
            assertThrows(IllegalStateException.class, () -> gif.cellSize(2));
            assertThrows(IllegalStateException.class, () -> gif.delay(2));
            assertThrows(IllegalStateException.class, () -> gif.every(2));
        }
    }

    @Test
    @DisplayName("rejects non-2D snapshots, oversize images and size changes")
    void rejectsInvalidSnapshots() throws IOException {
        CellGrid cube = new CellGrid(new GridDimensions(2, 2, 2));
        for (int[] c : cube.allCoordinates()) cube.set(c, new Cell(DEAD, c));
        Path file = dir.resolve("s.gif");
        try (GifRecorder gif = GifRecorder.create(file, TWO_COLORS)) {
            assertThrows(IllegalArgumentException.class, () -> gif.record(GridSnapshot.of(0, cube)));
            gif.record(uniform(2, 2, 0));
            assertThrows(IllegalArgumentException.class, () -> gif.record(uniform(3, 2, 0)));
            assertThrows(IllegalArgumentException.class, () -> gif.record(uniform(2, 3, 0)));
        }
        assertEquals(1, read(file).size());

        assertTooLarge(70_000, uniform(1, 2, 0));  // too wide
        assertTooLarge(1, uniform(1, 70_000, 0));  // too tall
        assertTooLarge(60_000, uniform(1, 1, 0));  // too many pixels
    }

    private void assertTooLarge(int cellSize, GridSnapshot snapshot) {
        try (GifRecorder gif = GifRecorder.create(dir.resolve("large.gif"), BY_VALUE).cellSize(cellSize)) {
            assertThrows(IllegalArgumentException.class, () -> gif.record(snapshot));
        }
    }

    @Test
    @DisplayName("rejects a renderer returning null")
    void nullColor() {
        try (GifRecorder gif = GifRecorder.create(dir.resolve("n.gif"), s -> null)) {
            assertThrows(NullPointerException.class, () -> gif.record(uniform(2, 2, 0)));
        }
    }

    // -- helpers --------------------------------------------------------------

    private void assertRunRecorded(AbstractCellularAutomataRule rule, Path file) throws IOException {
        CellularAutomata ca = new CellularAutomata(new CellularAutomataConfigurationBuilder()
                .setWidth(7).setHeight(5).setTotalIterations(4).setDefaultStatus(DEAD)
                .setInitialState(List.of(new Cell(ALIVE, 1, 0), new Cell(ALIVE, 2, 1), new Cell(ALIVE, 0, 2),
                        new Cell(ALIVE, 1, 2), new Cell(ALIVE, 2, 2)))
                .setNeighborhoodType(NeighborhoodType.MOORE).build());
        List<GridSnapshot> expected = new ArrayList<>();
        expected.add(GridSnapshot.of(0, ca.getGrid()));
        try (GifRecorder gif = GifRecorder.create(file, TWO_COLORS).cellSize(3)) {
            gif.record(expected.get(0));
            rule.addGenerationListener(gif);
            rule.addGenerationListener((gen, snap) -> expected.add(snap));
            rule.run(ca);
        }
        assertFrames(read(file), expected, TWO_COLORS, 3);
    }

    private static void assertFrames(List<BufferedImage> frames, List<GridSnapshot> expected,
                                     CellRenderer renderer, int cellSize) {
        assertEquals(expected.size(), frames.size());
        for (int i = 0; i < frames.size(); i++) {
            GridSnapshot snap = expected.get(i);
            BufferedImage frame = frames.get(i);
            assertEquals(snap.getDimensions().getSize(0) * cellSize, frame.getWidth());
            assertEquals(snap.getDimensions().getSize(1) * cellSize, frame.getHeight());
            for (int x = 0; x < frame.getWidth(); x++) {
                for (int y = 0; y < frame.getHeight(); y++) {
                    int want = renderer.getColor(snap.getState(x / cellSize, y / cellSize)).getRGB() & 0xffffff;
                    assertEquals(want, rgb(frame, x, y), "frame " + i + " pixel " + x + "," + y);
                }
            }
        }
    }

    private static int rgb(BufferedImage image, int x, int y) {
        return image.getRGB(x, y) & 0xffffff;
    }

    private static List<BufferedImage> read(Path file) throws IOException {
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        try (ImageInputStream in = ImageIO.createImageInputStream(file.toFile())) {
            reader.setInput(in);
            List<BufferedImage> frames = new ArrayList<>();
            int count = reader.getNumImages(true);
            for (int i = 0; i < count; i++) frames.add(reader.read(i));
            return frames;
        } finally {
            reader.dispose();
        }
    }

    private static GridSnapshot uniform(int cols, int rows, int value) {
        return snapshot(cols, rows, (col, row) -> value);
    }

    private static GridSnapshot snapshot(int cols, int rows, IntBinaryOperator value) {
        CellGrid grid = new CellGrid(new GridDimensions(cols, rows));
        for (int[] c : grid.allCoordinates()) {
            grid.set(c, new Cell(new CellState("s", value.applyAsInt(c[0], c[1])), c));
        }
        return GridSnapshot.of(0, grid);
    }

    private static class LifeRule extends CellularAutomataRule {
        @Override
        public Cell transition(Cell cell, List<Cell> neighbors) {
            return next(cell, neighbors);
        }
    }

    private static class ParallelLifeRule extends CellularAutomataParallelRule {
        @Override
        public Cell transition(Cell cell, List<Cell> neighbors) {
            return next(cell, neighbors);
        }
    }

    private static Cell next(Cell cell, List<Cell> neighbors) {
        long count = neighbors.stream().filter(n -> n.getCurrentStatus().equals(ALIVE)).count();
        boolean alive = count == 3 || (count == 2 && cell.getCurrentStatus().equals(ALIVE));
        return new Cell(alive ? ALIVE : DEAD, cell.getCol(), cell.getRow());
    }
}
