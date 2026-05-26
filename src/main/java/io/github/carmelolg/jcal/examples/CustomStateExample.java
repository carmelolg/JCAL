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
 * Demonstrates how to use JCAL with a multi-valued custom state.
 *
 * <p>{@link CellState} accepts any {@link Object} as its {@code value}, so
 * you can model states that carry more than a single boolean flag.  This example
 * simulates a simplified <em>heat diffusion</em> automaton with three temperature
 * levels: {@code COLD}, {@code WARM}, and {@code HOT}.
 *
 * <p><b>Rules:</b>
 * <ul>
 *   <li>A HOT cell stays HOT.</li>
 *   <li>A COLD cell adjacent (Von Neumann) to at least one HOT cell becomes WARM.</li>
 *   <li>A COLD cell adjacent to two or more WARM cells becomes WARM.</li>
 *   <li>All other cells keep their current state.</li>
 * </ul>
 *
 * <p>The Von Neumann neighborhood (4 orthogonal cells) is used because heat flows
 * along axes, not diagonally.
 *
 * <p>This pattern – using integer or enum values inside {@link CellState} – is the
 * recommended approach for Complex Cellular Automata (CCA) in JCAL.  For even richer
 * state you can store a {@code Map} or a custom POJO inside the {@code value} field.
 *
 * @see GameOfLifeExample for the minimal two-state example
 */
public class CustomStateExample {

    private static final Logger logger = LoggerFactory.getLogger(CustomStateExample.class);

    // --- Step 1: Define multi-valued states ---
    // The second argument (the value) can be any Object: String, Integer, Map, POJO, …
    static final CellState COLD = new CellState("cold", 0);
    static final CellState WARM = new CellState("warm", 1);
    static final CellState HOT = new CellState("hot", 2);

    public static void main(String[] args) {

        // --- Step 2: Define the initial hot cells ---
        // Two adjacent hot cells near the centre of the grid
        List<Cell> initialState = Arrays.asList(
                new Cell(HOT, 0, 0),   // centre cell
                new Cell(HOT, 9, 9)    // cell directly to the right
        );

        // --- Step 3: Build the configuration ---
        CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
                .setWidth(10)                                   // 10 columns
                .setHeight(10)                                  // 10 rows
                .setInfinite(false)                             // run for a fixed number of steps
                .setTotalIterations(3)                          // 3 generations
                .setDefaultStatus(COLD)                         // all cells start cold
                .setInitialState(initialState)                   // place the hot cells
                .setNeighborhoodType(NeighborhoodType.VON_NEUMANN) // 4-cell orthogonal neighbourhood
                .build();

        // --- Step 4: Initialize the automaton, register listener, and run ---
        CellularAutomata ca = new CellularAutomata(config);
        CellularAutomataRule rule = new HeatDiffusionRule();

        // The listener logs the grid (0=cold, 1=warm, 2=hot) after each generation.
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
            logger.info("Grid after generation {} (0=cold, 1=warm, 2=hot):\n{}", gen, sb);
        });

        rule.run(ca); // evolves for 3 steps, listener fires after each one
    }

    // -------------------------------------------------------------------------
    // Inner class: heat diffusion transition rule using custom state values
    // -------------------------------------------------------------------------

    /**
     * Heat diffusion rule: hot cells radiate warmth to cold neighbours.
     *
     * <p>This rule shows how to inspect the {@code value} field of a {@link CellState}
     * (here an {@link Integer}) to drive branching logic beyond a simple alive/dead check.
     */
    static class HeatDiffusionRule extends CellularAutomataRule {

        @Override
        public Cell transition(Cell cell, List<Cell> neighbors) {
            // Count how many neighbours are hot or warm
            long hotNeighborCount = neighbors.stream()
                    .filter(n -> n.getCurrentStatus().equals(HOT))
                    .count();
            long warmNeighborCount = neighbors.stream()
                    .filter(n -> n.getCurrentStatus().equals(WARM))
                    .count();

            return getDefaultCell(cell, hotNeighborCount, warmNeighborCount);
        }

        private Cell getDefaultCell(Cell cell, long hotNeighborCount, long warmNeighborCount) {
            Cell next = new Cell(cell.getCurrentStatus(), cell.getCol(), cell.getRow());
            CellState current = cell.getCurrentStatus();

            if (current.equals(HOT)) {
                next.setCurrentStatus(HOT);
            } else if (current.equals(WARM)) {
                if (hotNeighborCount > 0) {
                    next.setCurrentStatus(HOT);
                } else if (warmNeighborCount < 1) {
                    next.setCurrentStatus(COLD);
                } else {
                    next.setCurrentStatus(WARM);
                }
            } else if (current.equals(COLD)) {
                if (hotNeighborCount > 0 || warmNeighborCount >= 2) {
                    next.setCurrentStatus(WARM);
                } else {
                    next.setCurrentStatus(COLD);
                }
            } else {
                throw new IllegalStateException("Unexpected cell state: " + current);
            }

            return next;
        }

    }
}
