package io.github.carmelolg.jcal.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Objects;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import io.github.carmelolg.jcal.grid.GridSnapshot;

/**
 * A Swing {@link JPanel} that renders a 2D {@link GridSnapshot}.
 *
 * <p>Each cell is drawn as a filled rectangle whose colour is determined by the
 * supplied {@link CellRenderer}.  The panel automatically resizes itself to fit
 * the grid: {@code width = cols × cellSize}, {@code height = rows × cellSize}.
 *
 * <p>{@link #update(GridSnapshot)} is thread-safe: it can be called from any thread
 * (including the execution thread used by {@link io.github.carmelolg.jcal.core.CellularAutomataRule})
 * and will always dispatch the repaint to the Swing Event Dispatch Thread (EDT).
 *
 * <p><b>Typical embedding in a frame:</b>
 * <pre>{@code
 * GridPanel panel = new GridPanel(renderer, 12);
 * JFrame frame = new JFrame();
 * frame.add(panel);
 * frame.pack();
 * frame.setVisible(true);
 *
 * // later, from any thread:
 * panel.update(snapshot);
 * }</pre>
 *
 * <p><b>Note:</b> only 2D snapshots are supported.  Passing a snapshot whose
 * {@link io.github.carmelolg.jcal.grid.GridDimensions} has more than 2 dimensions
 * will throw an {@link IllegalArgumentException}.
 *
 * @author Carmelo La Gamba
 * @see CellRenderer
 * @see CellularAutomataDisplay
 */
public class GridPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final CellRenderer renderer;
    private final int cellSize;

    private volatile GridSnapshot snapshot;

    /**
     * Creates a {@code GridPanel} with the given renderer and cell size.
     *
     * @param renderer the {@link CellRenderer} that maps states to colours; must not be {@code null}
     * @param cellSize the pixel size of each cell (width = height); must be &gt; 0
     * @throws IllegalArgumentException if {@code cellSize} is not positive
     */
    public GridPanel(CellRenderer renderer, int cellSize) {
        Objects.requireNonNull(renderer, "renderer must not be null");
        if (cellSize <= 0) throw new IllegalArgumentException("cellSize must be > 0, got: " + cellSize);
        this.renderer = renderer;
        this.cellSize = cellSize;
        setBackground(java.awt.Color.BLACK);
    }

    /**
     * Updates the panel with a new snapshot and schedules a repaint on the EDT.
     *
     * <p>This method is thread-safe: it may be called from the CA execution thread.
     *
     * @param snap the new {@link GridSnapshot} to display; must not be {@code null}
     * @throws IllegalArgumentException if the snapshot is not 2-dimensional
     */
    public void update(GridSnapshot snap) {
        Objects.requireNonNull(snap, "snapshot must not be null");
        if (snap.getDimensions().getDimensionCount() != 2) {
            throw new IllegalArgumentException(
                    "GridPanel only supports 2D snapshots, got "
                    + snap.getDimensions().getDimensionCount() + "D");
        }
        this.snapshot = snap;
        int cols = snap.getDimensions().getSize(0);
        int rows = snap.getDimensions().getSize(1);
        Dimension preferred = new Dimension(cols * cellSize, rows * cellSize);
        SwingUtilities.invokeLater(() -> {
            setPreferredSize(preferred);
            revalidate();
            repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GridSnapshot current = snapshot;
        if (current == null) return;

        int cols = current.getDimensions().getSize(0);
        int rows = current.getDimensions().getSize(1);
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                g.setColor(renderer.getColor(current.getState(col, row)));
                g.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
            }
        }
    }
}
