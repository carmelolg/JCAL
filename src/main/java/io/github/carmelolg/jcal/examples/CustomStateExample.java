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
 * Demonstrates how to use JCAL with a multi-valued custom state.
 *
 * <p>{@link DefaultStatus} accepts any {@link Object} as its {@code value}, so
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
 * <p>This pattern – using integer or enum values inside {@link DefaultStatus} – is the
 * recommended approach for Complex Cellular Automata (CCA) in JCAL.  For even richer
 * state you can store a {@code Map} or a custom POJO inside the {@code value} field.
 *
 * @see GameOfLifeExample for the minimal two-state example
 */
public class CustomStateExample {

    // --- Step 1: Define multi-valued states ---
    // The second argument (the value) can be any Object: String, Integer, Map, POJO, …
    static final DefaultStatus COLD = new DefaultStatus("cold", 0);
    static final DefaultStatus WARM = new DefaultStatus("warm", 1);
    static final DefaultStatus HOT  = new DefaultStatus("hot",  2);

    public static void main(String[] args) throws Exception {

        // --- Step 2: Define the initial hot cells ---
        // Two adjacent hot cells near the centre of the grid
        List<DefaultCell> initialState = Arrays.asList(
            new DefaultCell(HOT, 5, 5),   // centre cell
            new DefaultCell(HOT, 5, 6)    // cell directly to the right
        );

        // --- Step 3: Build the configuration ---
        CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
            .setWidth(10)                                   // 10 columns
            .setHeight(10)                                  // 10 rows
            .setInfinite(false)                             // run for a fixed number of steps
            .setTotalIterations(3)                          // 3 generations
            .setDefaultStatus(COLD)                         // all cells start cold
            .setInitalState(initialState)                   // place the hot cells
            .setNeighborhoodType(NeighborhoodType.VON_NEUMANN) // 4-cell orthogonal neighbourhood
            .build();

        // --- Step 4: Initialize the automaton and run ---
        CellularAutomata ca = new CellularAutomata(config);
        CellularAutomataExecutor rule = new HeatDiffusionRule();
        ca = rule.run(ca);   // evolves for 3 steps

        // Print the resulting grid (cell values: 0=cold, 1=warm, 2=hot)
        System.out.println("Grid after 3 iterations (0=cold, 1=warm, 2=hot):");
        System.out.println(ca);
    }

    // -------------------------------------------------------------------------
    // Inner class: heat diffusion transition rule using custom state values
    // -------------------------------------------------------------------------

    /**
     * Heat diffusion rule: hot cells radiate warmth to cold neighbours.
     *
     * <p>This rule shows how to inspect the {@code value} field of a {@link DefaultStatus}
     * (here an {@link Integer}) to drive branching logic beyond a simple alive/dead check.
     */
    static class HeatDiffusionRule extends CellularAutomataExecutor {

        @Override
        public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {
            // Count how many neighbours are hot or warm
            long hotNeighborCount  = neighbors.stream()
                .filter(n -> n.getCurrentStatus().equals(HOT))
                .count();
            long warmNeighborCount = neighbors.stream()
                .filter(n -> n.getCurrentStatus().equals(WARM))
                .count();

            // Start with the cell's current state as the default next state
            DefaultCell next = new DefaultCell(cell.getCurrentStatus(), cell.getCol(), cell.getRow());

            if (cell.getCurrentStatus().equals(HOT)) {
                // Hot cells are permanent heat sources – they always stay hot
                next.setCurrentStatus(HOT);
            } else if (hotNeighborCount > 0) {
                // Any cell touching a hot cell heats up to warm
                next.setCurrentStatus(WARM);
            } else if (warmNeighborCount >= 2) {
                // Cells surrounded by enough warmth also become warm
                next.setCurrentStatus(WARM);
            }
            // Otherwise the cell retains its current state (stays cold or warm)

            return next;
        }
    }
}
