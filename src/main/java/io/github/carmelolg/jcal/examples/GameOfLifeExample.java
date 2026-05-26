package io.github.carmelolg.jcal.examples;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.carmelolg.jcal.core.CellularAutomataConfiguration;
import io.github.carmelolg.jcal.core.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder;
import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.core.CellularAutomataRule;
import io.github.carmelolg.jcal.core.GenerationListener;
import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.grid.GridSnapshot;
import io.github.carmelolg.jcal.neighborhood.NeighborhoodType;

/**
 * Minimal, fully-commented example of Conway's Game of Life using JCAL.
 *
 * <p>This example demonstrates the four steps needed to run any automaton:
 * <ol>
 *   <li>Define the possible cell states ({@link CellState}).</li>
 *   <li>Build the configuration ({@link CellularAutomataConfiguration}).</li>
 *   <li>Implement the transition rule by extending {@link CellularAutomataRule}.</li>
 *   <li>Register a {@link GenerationListener} to inspect the grid after each step.</li>
 *   <li>Initialize the grid and call {@link CellularAutomataRule#run(CellularAutomata)}.</li>
 * </ol>
 *
 * <p>The initial pattern used here is the <em>blinker</em>: three horizontally adjacent
 * alive cells that oscillate between horizontal and vertical every generation.
 *
 * <p><b>Expected output after 2 iterations:</b> the blinker returns to its original
 * horizontal orientation (period-2 oscillator).
 *
 * <p>Copy-paste this class and change {@link GameOfLifeRule#transition} to experiment with
 * different rules.
 *
 * @see CustomStateExample for a more advanced example with multi-value cell states
 */
public class GameOfLifeExample {

    private static final Logger logger = LoggerFactory.getLogger(GameOfLifeExample.class);

    // --- Step 1: Define possible cell states ---
    // A status has a string key (for identification) and an arbitrary value (for display/logic).
    static final CellState DEAD  = new CellState("dead",  "0");
    static final CellState ALIVE = new CellState("alive", "1");

    public static void main(String[] args) {

        // --- Step 2: Set the initial live cells ---
        // Blinker pattern: three cells in a horizontal row at the centre of the grid.
        List<Cell> initialState = Arrays.asList(
            new Cell(ALIVE, 5, 4),  // (col=5, row=4)
            new Cell(ALIVE, 5, 5),  // (col=5, row=5) – centre
            new Cell(ALIVE, 5, 6)   // (col=5, row=6)
        );

        // --- Step 3: Build the configuration ---
        CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
            .setWidth(10)                            // 10 columns
            .setHeight(10)                           // 10 rows
            .setInfinite(false)                      // run for a fixed number of steps
            .setTotalIterations(2)                   // 2 generations
            .setDefaultStatus(DEAD)                  // all cells start as dead
            .setInitialState(initialState)            // override specific cells
            .setNeighborhoodType(NeighborhoodType.MOORE) // 8-cell Moore neighborhood
            .build();

        // --- Step 4: Initialize the automaton, register listener, and run ---
        CellularAutomata ca = new CellularAutomata(config); // allocates the grid
        CellularAutomataRule rule = new GameOfLifeRule();

        // The listener is called once per generation with an immutable GridSnapshot.
        rule.addGenerationListener((int gen, GridSnapshot snap) -> {
            int cols = snap.getDimensions().getSize(0);
            int rows = snap.getDimensions().getSize(1);
            StringBuilder sb = new StringBuilder();
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    sb.append(snap.getState(col, row).getValue()).append(" ");
                }
                sb.append("\n");
            }
            logger.info("Grid after generation {}:\n{}", gen, sb);
        });

        rule.run(ca); // evolves for 2 steps, listener fires after each one
    }

    // -------------------------------------------------------------------------
    // Inner class: Conway's Game of Life transition rule
    // -------------------------------------------------------------------------

    /**
     * Implements Conway's Game of Life transition rules:
     * <ul>
     *   <li>A dead cell with exactly 3 alive neighbours becomes alive (birth).</li>
     *   <li>An alive cell with 2 or 3 alive neighbours survives.</li>
     *   <li>All other cells die or stay dead (underpopulation / overcrowding).</li>
     * </ul>
     *
     * <p>Override {@code transition} to change the rule without touching anything else.
     */
    static class GameOfLifeRule extends CellularAutomataRule {

        @Override
        public Cell transition(Cell cell, List<Cell> neighbors) {
            // Count how many neighbours are currently alive
            long aliveNeighborCount = neighbors.stream()
                .filter(n -> n.getCurrentStatus().equals(ALIVE))
                .count();

            // Start with a dead result cell at the same position
            Cell next = new Cell(DEAD, cell.getCol(), cell.getRow());

            boolean isCurrentlyAlive = cell.getCurrentStatus().equals(ALIVE);

            if (!isCurrentlyAlive && aliveNeighborCount == 3) {
                // Rule: dead cell with exactly 3 alive neighbours is born
                next.setCurrentStatus(ALIVE);
            } else if (isCurrentlyAlive && (aliveNeighborCount == 2 || aliveNeighborCount == 3)) {
                // Rule: alive cell with 2 or 3 alive neighbours survives
                next.setCurrentStatus(ALIVE);
            }
            // All other cases: cell stays / becomes dead (already set above)

            return next;
        }
    }
}
