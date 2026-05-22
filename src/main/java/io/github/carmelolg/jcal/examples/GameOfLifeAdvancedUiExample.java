package io.github.carmelolg.jcal.examples;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.core.CellularAutomataConfiguration;
import io.github.carmelolg.jcal.core.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder;
import io.github.carmelolg.jcal.core.CellularAutomataRule;
import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.grid.GridSnapshot;
import io.github.carmelolg.jcal.neighborhood.NeighborhoodType;
import io.github.carmelolg.jcal.ui.CellularAutomataDisplay;

/**
 * Advanced Game of Life example using {@link CellularAutomataDisplay} directly.
 *
 * <p>Standalone example: This class is completely independent and does not depend
 * on other examples. It defines its own {@link GameOfLifeRule}.
 *
 * <p>This example demonstrates the <b>flexibility and customization</b> available when
 * using {@code CellularAutomataDisplay} instead of the simpler {@code CellularAutomataUIRunner}.
 *
 * <p><b>Features:</b>
 * <ul>
 *   <li><b>Play/Pause button</b> — pause the evolution mid-run.</li>
 *   <li><b>Speed slider</b> — adjust rendering delay on-the-fly (50–500 ms).</li>
 *   <li><b>Live statistics panel</b> — shows generation count and alive cell count.</li>
 *   <li><b>Custom colour renderer</b> — green for alive, black for dead.</li>
 *   <li><b>Resizable window</b> — manually packed to fit content.</li>
 * </ul>
 *
 * <p><b>Run:</b>
 * <pre>{@code
 * mvn compile exec:java \
 *   -Dexec.mainClass="io.github.carmelolg.jcal.examples.GameOfLifeAdvancedUiExample"
 * }</pre>
 *
 * @see GameOfLifeUiExample simpler alternative using CellularAutomataUIRunner
 */
public class GameOfLifeAdvancedUiExample {

    static final CellState DEAD  = new CellState("dead",  "0");
    static final CellState ALIVE = new CellState("alive", "1");

    public static void main(String[] args) throws Exception {

        // Setup automaton with glider + blinker
        List<Cell> initialState = Arrays.asList(
            // Glider
            new Cell(ALIVE, 2, 1),
            new Cell(ALIVE, 3, 2),
            new Cell(ALIVE, 1, 3),
            new Cell(ALIVE, 2, 3),
            new Cell(ALIVE, 3, 3),
            // Blinker
            new Cell(ALIVE, 18, 20),
            new Cell(ALIVE, 19, 20),
            new Cell(ALIVE, 20, 20)
        );

        CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
            .setWidth(40)
            .setHeight(40)
            .setInfinite(false)
            .setTotalIterations(200)
            .setDefaultStatus(DEAD)
            .setInitalState(initialState)
            .setNeighborhoodType(NeighborhoodType.MOORE)
            .build();

        CellularAutomata ca = new CellularAutomata(config);

        // Mutable state for play/pause and speed control
        AtomicBoolean paused = new AtomicBoolean(false);
        AtomicInteger delayMs = new AtomicInteger(100);

        // Create the main display
        // Note: The renderer (lambda) is fully customizable. Try different colours:
        //   - Color.GREEN, Color.YELLOW, Color.CYAN, Color.MAGENTA, new Color(0xFF6B00) etc.
        // This demonstrates the flexibility of CellularAutomataDisplay vs CellularAutomataUIRunner.
        CellularAutomataDisplay display = new CellularAutomataDisplay(
            "Game of Life — Advanced (with Controls)",
            state -> state.equals(ALIVE) ? Color.YELLOW : Color.BLACK,
            14
        );

        // Build a custom control panel
        JPanel controlPanel = new JPanel(new BorderLayout(10, 10));
        controlPanel.setBackground(new Color(240, 240, 240));

        // Play/Pause button
        JButton pauseButton = new JButton("Pause");
        pauseButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        pauseButton.addActionListener(e -> {
            paused.set(!paused.get());
            pauseButton.setText(paused.get() ? "Resume" : "Pause");
        });

        // Speed slider: 50–500 ms
        JSlider speedSlider = new JSlider(50, 500, 100);
        speedSlider.setMajorTickSpacing(100);
        speedSlider.setMinorTickSpacing(10);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(e -> delayMs.set(speedSlider.getValue()));

        JLabel speedLabel = new JLabel("Speed (ms):");
        speedLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));

        JPanel speedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        speedPanel.setBackground(controlPanel.getBackground());
        speedPanel.add(speedLabel);
        speedPanel.add(speedSlider);

        // Stats labels
        JLabel generationStatsLabel = new JLabel("Generation: 0");
        generationStatsLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));

        JLabel aliveCountLabel = new JLabel("Alive: 0");
        aliveCountLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        statsPanel.setBackground(controlPanel.getBackground());
        statsPanel.add(generationStatsLabel);
        statsPanel.add(aliveCountLabel);

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(controlPanel.getBackground());
        topPanel.add(pauseButton, BorderLayout.WEST);
        topPanel.add(speedPanel, BorderLayout.CENTER);

        controlPanel.add(topPanel, BorderLayout.NORTH);
        controlPanel.add(statsPanel, BorderLayout.SOUTH);

        // Add control panel to display window
        display.getFrame().add(controlPanel, BorderLayout.NORTH);
        display.show();

        // Create rule and listener
        GameOfLifeRule rule = new GameOfLifeRule();

        rule.addGenerationListener((int gen, GridSnapshot snap) -> {
            display.update(snap);

            int aliveCount = countAlive(snap);
            SwingUtilities.invokeLater(() -> {
                generationStatsLabel.setText(String.format("Generation: %d", gen));
                aliveCountLabel.setText(String.format("Alive: %d", aliveCount));
            });

            // Respect pause
            while (paused.get()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            try {
                Thread.sleep(delayMs.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Launch on custom thread
        Thread thread = new Thread(() -> {
            try {
                rule.run(ca);
                SwingUtilities.invokeLater(() -> pauseButton.setEnabled(false));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "jcal-advanced-ui");
        thread.setDaemon(true);
        thread.start();

        Thread.sleep(200L * 100 + 5_000);
    }

    /**
     * Counts the number of alive cells in a snapshot.
     */
    private static int countAlive(GridSnapshot snap) {
        int count = 0;
        int[] sizes = snap.getDimensions().sizes();
        int cols = sizes[0];
        int rows = sizes[1];
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                if (snap.getState(col, row).equals(ALIVE)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Simple AtomicBoolean holder.
     */
    static class AtomicBoolean {
        private boolean value;

        AtomicBoolean(boolean initialValue) {
            this.value = initialValue;
        }

        synchronized boolean get() {
            return value;
        }

        synchronized void set(boolean newValue) {
            this.value = newValue;
        }
    }

    /**
     * Simple AtomicInteger holder.
     */
    static class AtomicInteger {
        private int value;

        AtomicInteger(int initialValue) {
            this.value = initialValue;
        }

        synchronized int get() {
            return value;
        }

        synchronized void set(int newValue) {
            this.value = newValue;
        }
    }

    /**
     * Conway's Game of Life rule (defined locally, standalone).
     */
    static class GameOfLifeRule extends CellularAutomataRule {

        @Override
        public Cell transition(Cell cell, List<Cell> neighbors) {
            long aliveNeighborCount = neighbors.stream()
                .filter(n -> n.getCurrentStatus().equals(ALIVE))
                .count();

            Cell next = new Cell(DEAD, cell.getCol(), cell.getRow());

            boolean isCurrentlyAlive = cell.getCurrentStatus().equals(ALIVE);

            if (!isCurrentlyAlive && aliveNeighborCount == 3) {
                next.setCurrentStatus(ALIVE);
            } else if (isCurrentlyAlive && (aliveNeighborCount == 2 || aliveNeighborCount == 3)) {
                next.setCurrentStatus(ALIVE);
            }

            return next;
        }
    }
}
