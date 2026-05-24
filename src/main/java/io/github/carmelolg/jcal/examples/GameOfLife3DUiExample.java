package io.github.carmelolg.jcal.examples;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.core.CellularAutomataConfiguration;
import io.github.carmelolg.jcal.core.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder;
import io.github.carmelolg.jcal.core.CellularAutomataRule;
import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.grid.GridSnapshot;
import io.github.carmelolg.jcal.neighborhood.NeighborhoodType;

/**
 * Graphical demonstration of a 3D Game of Life using the JCAL UI layer.
 *
 * <p>Standalone example: This class is completely independent and does not depend
 * on other examples. It defines its own {@link Carter3DLifeRule} and still-life pattern.
 *
 * <p>Since a 3D grid cannot be displayed on a single 2D surface, this example
 * renders each <em>Z-slice</em> as a separate panel laid out side by side in
 * one window.  Each panel shows the X-Y plane at a fixed Z value.
 *
 * <p>The rule used is <em>Carter Bays' 3D Life</em> (S5,6/B5):
 * alive cells survive with 5-6 alive neighbours; dead cells are born with
 * exactly 5 alive neighbours.
 *
 * <p><b>Window layout</b> — one column per Z-slice (7 slices for a 7x7x7 grid):
 * <pre>
 *  [ Z=0 | Z=1 | Z=2 | Z=3 | Z=4 | Z=5 | Z=6 ]
 * </pre>
 *
 * <p><b>Run:</b>
 * <pre>{@code
 * mvn compile exec:java \
 *   -Dexec.mainClass="io.github.carmelolg.jcal.examples.GameOfLife3DUiExample"
 * }</pre>
 *
 * @see GameOfLifeUiExample for the 2D graphical version
 */
public class GameOfLife3DUiExample {

    static final CellState DEAD  = new CellState("dead",  "0");
    static final CellState ALIVE = new CellState("alive", "1");

    /** Pixel size of each cell in the rendered grid. */
    private static final int CELL_SIZE = 20;

    /** Milliseconds to wait between generations. */
    private static final int DELAY_MS = 200;

    /** Six coordinates forming the 3D still-life pattern (defined locally, standalone). */
    private static final int[][] STILL_LIFE_COORDS = {
        {3, 3, 3}, {3, 4, 3}, {4, 3, 3}, {4, 4, 3}, {3, 3, 4}, {4, 4, 4}
    };

    public static void main(String[] args) throws Exception {

        // Build initial state with still-life pattern
        List<Cell> initialState = new ArrayList<>();
        for (int[] c : STILL_LIFE_COORDS) {
            initialState.add(new Cell(ALIVE, c[0], c[1], c[2]));
        }

        CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
            .setDimensions(7, 7, 7)
            .setTotalIterations(20)
            .setDefaultStatus(DEAD)
            .setNeighborhoodType(NeighborhoodType.MOORE)
            .setInitialState(initialState)
            .build();

        CellularAutomata ca = new CellularAutomata(config);

        // --- Build the Swing window with one SlicePanel per Z value ---
        int sizeX = 7;
        int sizeY = 7;
        int sizeZ = 7;

        JFrame frame = new JFrame("3D Game of Life (Carter Bays S5,6/B5) — JCAL");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel generationLabel = new JLabel("Generation: 0", SwingConstants.CENTER);
        generationLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JPanel slicesContainer = new JPanel(new GridLayout(1, sizeZ, 4, 0));
        slicesContainer.setBackground(Color.DARK_GRAY);

        SlicePanel[] slicePanels = new SlicePanel[sizeZ];
        for (int z = 0; z < sizeZ; z++) {
            slicePanels[z] = new SlicePanel(z, sizeX, sizeY, CELL_SIZE);
            slicesContainer.add(slicePanels[z]);
        }

        frame.setLayout(new BorderLayout(0, 4));
        frame.add(slicesContainer, BorderLayout.CENTER);
        frame.add(generationLabel, BorderLayout.SOUTH);

        SwingUtilities.invokeLater(() -> {
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        // --- Register a GenerationListener ---
        Carter3DLifeRule rule = new Carter3DLifeRule();

        rule.addGenerationListener((int gen, GridSnapshot snap) -> {
            SwingUtilities.invokeLater(() ->
                generationLabel.setText("Generation: " + gen));

            for (SlicePanel panel : slicePanels) {
                panel.update(snap);
            }
            try {
                Thread.sleep(DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread thread = new Thread(() -> {
            try {
                rule.run(ca);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }, "jcal-3d-ui-runner");
        thread.setDaemon(true);
        thread.start();

        Thread.sleep(20L * DELAY_MS + 3_000);
    }

    /**
     * Renders a single Z-slice of a 3D GridSnapshot as a JPanel.
     */
    static class SlicePanel extends JPanel {

        private static final long serialVersionUID = 1L;

        private final int z;
        private final int sizeX;
        private final int sizeY;
        private final int cellSize;

        private volatile GridSnapshot snapshot;

        SlicePanel(int z, int sizeX, int sizeY, int cellSize) {
            this.z        = z;
            this.sizeX    = sizeX;
            this.sizeY    = sizeY;
            this.cellSize = cellSize;
            setBackground(Color.BLACK);
            setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Z=" + z));
            setPreferredSize(new Dimension(sizeX * cellSize, sizeY * cellSize + 20));
        }

        void update(GridSnapshot snap) {
            this.snapshot = snap;
            SwingUtilities.invokeLater(this::repaint);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            GridSnapshot current = snapshot;
            if (current == null) return;

            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    CellState state = current.getState(new int[]{x, y, z});
                    g.setColor(state.equals(ALIVE) ? Color.CYAN : Color.BLACK);
                    g.fillRect(x * cellSize, y * cellSize + 20, cellSize - 1, cellSize - 1);
                }
            }
        }
    }

    /**
     * Carter Bays' 3D Life rule (S5,6/B5).
     * Defined locally so this example is completely standalone.
     */
    static class Carter3DLifeRule extends CellularAutomataRule {

        @Override
        public Cell transition(Cell cell, List<Cell> neighbors) {
            long aliveCount = neighbors.stream()
                .filter(n -> n.getCurrentStatus().equals(ALIVE))
                .count();

            Cell next = new Cell(DEAD, cell.getCoordinates());

            boolean alive = cell.getCurrentStatus().equals(ALIVE);
            if (alive && (aliveCount == 5 || aliveCount == 6)) {
                next.setCurrentStatus(ALIVE);
            } else if (!alive && aliveCount == 5) {
                next.setCurrentStatus(ALIVE);
            }
            return next;
        }
    }
}
