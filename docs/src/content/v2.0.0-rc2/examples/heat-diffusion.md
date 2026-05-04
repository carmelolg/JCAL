---
title: "Heat Diffusion"
date: 2026-04-30
draft: false
summary: "Multi-valued cell states and Von Neumann neighborhood heat propagation."
toc: true
---

Demonstrates how to use **multi-valued cell states** to model numeric quantities.
This automaton simulates heat spreading from hot cells to their neighbors using a Von Neumann
(4-directional) neighborhood.

{{< callout type="info" >}}
The example places two HOT cells at opposite corners and evolves for 3 iterations.
Heat spreads orthogonally (not diagonally) to adjacent cells.
{{< /callout >}}

## Full Code

**File:** `CustomStateExample.java`

```java
package io.github.carmelolg.jcal.examples;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.carmelolg.jcal.core.CellularAutomataConfiguration;
import io.github.carmelolg.jcal.core.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder;
import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.core.CellularAutomataExecutor;
import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellState;
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

    private static final Logger log = LoggerFactory.getLogger(CustomStateExample.class);

    // --- Step 1: Define multi-valued states ---
    // The second argument (the value) can be any Object: String, Integer, Map, POJO, …
    static final CellState COLD = new CellState("cold", 0);
    static final CellState WARM = new CellState("warm", 1);
    static final CellState HOT = new CellState("hot", 2);

    public static void main(String[] args) throws Exception {

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
                .setInitalState(initialState)                   // place the hot cells
                .setNeighborhoodType(NeighborhoodType.VON_NEUMANN) // 4-cell orthogonal neighbourhood
                .build();

        // --- Step 4: Initialize the automaton and run ---
        CellularAutomata ca = new CellularAutomata(config);
        CellularAutomataExecutor rule = new HeatDiffusionRule();
        ca = rule.run(ca);   // evolves for 3 steps

        // Print the resulting grid (cell values: 0=cold, 1=warm, 2=hot)
        log.info("Grid after 3 iterations (0=cold, 1=warm, 2=hot):");
        log.info("{}", ca);
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
    static class HeatDiffusionRule extends CellularAutomataExecutor {

        @Override
        public Cell singleRun(Cell cell, List<Cell> neighbors) {
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
```

## Key Concepts

- **Multi-valued states:** Integer values (0=cold, 1=warm, 2=hot) attached to state keys.
- **Von Neumann neighborhood:** 4 orthogonal neighbors (up, down, left, right).
- **State transition logic:** Complex rules based on neighbor counts of different state types.
- **Practical domain modeling:** Applicable to temperature, pressure, concentration, or other
  scalar quantities in cellular automata.

## See Also

- [Game of Life 2D](../game-of-life-2d/) — Two-state automaton
- [3D Game of Life](../game-of-life-3d/) — Three dimensions
- [Custom State Objects](../../articles/custom-state/) — Advanced state patterns
