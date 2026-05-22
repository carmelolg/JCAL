package io.github.carmelolg.jcal.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Objects;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import io.github.carmelolg.jcal.grid.GridSnapshot;

/**
 * A Swing {@link JFrame} that displays the evolution of a 2D Cellular Automata grid.
 *
 * <p>{@code CellularAutomataDisplay} owns a {@link GridPanel} for rendering and a status label
 * showing the current generation.  It is a pure display component: call
 * {@link #update(GridSnapshot)} to push a new frame.
 *
 * <p>To drive the display from the automaton engine, wrap it in an
 * {@link AutomataListener} (which accepts any {@link GridDisplay}) and register
 * that listener on your
 * {@link io.github.carmelolg.jcal.core.CellularAutomataRule}.
 *
 * <p><b>Example:</b>
 * <pre>{@code
 * CellularAutomataDisplay display = new CellularAutomataDisplay("Game of Life", renderer, 12);
 * display.show();
 *
 * AutomataListener listener = new AutomataListener(display, 100);
 * rule.addGenerationListener(listener);
 * }</pre>
 *
 * <p>All Swing mutations inside this class are dispatched on the EDT.
 *
 * @author Carmelo La Gamba
 * @see GridPanel
 * @see AutomataListener
 * @see CellRenderer
 */
public class CellularAutomataDisplay implements GridDisplay {

    private final JFrame frame;
    private final GridPanel gridPanel;
    private final JLabel generationLabel;

    /**
     * Creates a {@code CellularAutomataDisplay} with the given title, renderer and cell size.
     *
     * @param title      the window title; must not be {@code null}
     * @param renderer   the {@link CellRenderer} used by the inner {@link GridPanel}; must not be {@code null}
     * @param cellSize   the pixel size of each cell; must be &gt; 0
     */
    public CellularAutomataDisplay(String title, CellRenderer renderer, int cellSize) {
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(renderer, "renderer must not be null");

        gridPanel = new GridPanel(renderer, cellSize);

        generationLabel = new JLabel("Generation: 0", SwingConstants.CENTER);
        generationLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(gridPanel, BorderLayout.CENTER);
        frame.add(generationLabel, BorderLayout.SOUTH);
    }

    /**
     * Creates a {@code CellularAutomataDisplay} with the default title {@code "JCAL Automata"}.
     *
     * @param renderer the {@link CellRenderer}; must not be {@code null}
     * @param cellSize the pixel size of each cell; must be &gt; 0
     */
    public CellularAutomataDisplay(CellRenderer renderer, int cellSize) {
        this("JCAL Automata", renderer, cellSize);
    }

    /**
     * Updates the displayed grid and generation counter.
     *
     * <p>This method is thread-safe: it delegates all Swing mutations to the EDT.
     *
     * @param snap the {@link GridSnapshot} to display; must not be {@code null}
     */
    public void update(GridSnapshot snap) {
        Objects.requireNonNull(snap, "snapshot must not be null");
        gridPanel.update(snap);
        SwingUtilities.invokeLater(() -> {
            generationLabel.setText("Generation: " + snap.getGeneration());
            frame.pack();
        });
    }

    /**
     * Makes the window visible and packs it to its preferred size.
     *
     * <p>Safe to call from any thread.
     */
    public void show() {
        SwingUtilities.invokeLater(() -> {
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    /**
     * Disposes the window, releasing all native resources.
     *
     * <p>Safe to call from any thread.
     */
    public void close() {
        SwingUtilities.invokeLater(frame::dispose);
    }

    /**
     * Returns the underlying {@link JFrame} for advanced customisation.
     *
     * @return the {@link JFrame}
     */
    public JFrame getFrame() {
        return frame;
    }
}
