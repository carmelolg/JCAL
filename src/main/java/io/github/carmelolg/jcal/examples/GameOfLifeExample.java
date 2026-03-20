package io.github.carmelolg.jcal.examples;

import java.util.Arrays;
import java.util.List;

import io.github.carmelolg.jcal.configuration.CellularAutomataConfiguration;
import io.github.carmelolg.jcal.configuration.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder;
import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.core.CellularAutomataExecutor;
import io.github.carmelolg.jcal.model.DefaultCell;
import io.github.carmelolg.jcal.model.DefaultStatus;
import io.github.carmelolg.jcal.model.NeighborhoodType;

/**
 * Minimal, fully-commented example of Conway's Game of Life using JCAL.
 *
 * <p>This example demonstrates the four steps needed to run any automaton:
 * <ol>
 *   <li>Define the possible cell states ({@link DefaultStatus}).</li>
 *   <li>Build the configuration ({@link CellularAutomataConfiguration}).</li>
 *   <li>Implement the transition rule by extending {@link CellularAutomataExecutor}.</li>
 *   <li>Initialize the grid and call {@link CellularAutomataExecutor#run(CellularAutomata)}.</li>
 * </ol>
 *
 * <p>The initial pattern used here is the <em>blinker</em>: three horizontally adjacent
 * alive cells that oscillate between horizontal and vertical every generation.
 *
 * <p><b>Expected output after 2 iterations:</b> the blinker returns to its original
 * horizontal orientation (period-2 oscillator).
 *
 * <p>Copy-paste this class and change {@link GameOfLifeRule#singleRun} to experiment with
 * different rules.
 *
 * @see CustomStateExample for a more advanced example with multi-value cell states
 */
public class GameOfLifeExample {

    // --- Step 1: Define possible cell states ---
    // A status has a string key (for identification) and an arbitrary value (for display/logic).
    static final DefaultStatus DEAD  = new DefaultStatus("dead",  "0");
    static final DefaultStatus ALIVE = new DefaultStatus("alive", "1");

    public static void main(String[] args) throws Exception {

        // --- Step 2: Set the initial live cells ---
        // Blinker pattern: three cells in a horizontal row at the centre of the grid.
        List<DefaultCell> initialState = Arrays.asList(
            new DefaultCell(ALIVE, 5, 4),  // (col=5, row=4)
            new DefaultCell(ALIVE, 5, 5),  // (col=5, row=5) – centre
            new DefaultCell(ALIVE, 5, 6)   // (col=5, row=6)
        );

        // --- Step 3: Build the configuration ---
        CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
            .setWidth(10)                            // 10 columns
            .setHeight(10)                           // 10 rows
            .setInfinite(false)                      // run for a fixed number of steps
            .setTotalIterations(2)                   // 2 generations
            .setDefaultStatus(DEAD)                  // all cells start as dead
            .setInitalState(initialState)            // override specific cells
            .setNeighborhoodType(NeighborhoodType.MOORE) // 8-cell Moore neighborhood
            .build();

        // --- Step 4: Initialize the automaton and run ---
        CellularAutomata ca = new CellularAutomata(config); // allocates the grid
        CellularAutomataExecutor rule = new GameOfLifeRule();
        ca = rule.run(ca);                                   // evolves for 2 steps

        // Print the resulting grid (each cell shows its status value)
        System.out.println("Grid after 2 iterations:");
        System.out.println(ca);
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
     * <p>Override {@code singleRun} to change the rule without touching anything else.
     */
    static class GameOfLifeRule extends CellularAutomataExecutor {

        @Override
        public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {
            // Count how many neighbours are currently alive
            long aliveNeighborCount = neighbors.stream()
                .filter(n -> n.getCurrentStatus().equals(ALIVE))
                .count();

            // Start with a dead result cell at the same position
            DefaultCell next = new DefaultCell(DEAD, cell.getCol(), cell.getRow());

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
