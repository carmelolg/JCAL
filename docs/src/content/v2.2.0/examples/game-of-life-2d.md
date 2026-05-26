---
title: "Conway's Game of Life (2D)"
date: 2026-04-30
draft: false
summary: "Classic two-state 2D cellular automaton with Moore neighborhood."
toc: true
---

The classic two-state 2D cellular automaton. Demonstrates the four core steps:
1. Define cell states
2. Create an initial configuration
3. Build the grid
4. Implement the transition rule

{{< callout type="info" >}}
The example runs a **blinker** pattern (three horizontally-aligned cells) for 2 iterations.
After one generation, the blinker rotates 90°; after two, it returns to its starting state.
{{< /callout >}}

## Full Code

**File:** `GameOfLifeExample.java`

```java
package io.github.carmelolg.jcal.examples;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.carmelolg.jcal.core.CellularAutomataConfiguration;
import io.github.carmelolg.jcal.core.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder;
import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.core.CellularAutomataRule;
import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.neighborhood.NeighborhoodType;

/**
 * Minimal, fully-commented example of Conway's Game of Life using JCAL.
 *
 * <p>This example demonstrates the four steps needed to run any automaton:
 * <ol>
 *   <li>Define the possible cell states ({@link CellState}).</li>
 *   <li>Build the configuration ({@link CellularAutomataConfiguration}).</li>
 *   <li>Implement the transition rule by extending {@link CellularAutomataRule}.</li>
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

    private static final Logger log = LoggerFactory.getLogger(GameOfLifeExample.class);

    // --- Step 1: Define possible cell states ---
    // A status has a string key (for identification) and an arbitrary value (for display/logic).
    static final CellState DEAD  = new CellState("dead",  "0");
    static final CellState ALIVE = new CellState("alive", "1");

    public static void main(String[] args) throws Exception {

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

        // --- Step 4: Initialize the automaton and run ---
        CellularAutomata ca = new CellularAutomata(config); // allocates the grid
        CellularAutomataRule rule = new GameOfLifeRule();
        ca = rule.run(ca);                                   // evolves for 2 steps

        // Print the resulting grid (each cell shows its status value)
        log.info("Grid after 2 iterations:");
        log.info("{}", ca);
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
```

## Key Concepts

- **States:** Two boolean states (dead/alive) with string keys and display values.
- **Neighborhoods:** Uses Moore neighborhood (8 adjacent cells).
- **Configuration:** Finite grid (10×10), 2 iterations, default state (dead).
- **Rule:** Classic Conway rules encoded in `transition`.

## See Also

- [Heat Diffusion](../heat-diffusion/) — Multi-valued states
- [3D Game of Life](../game-of-life-3d/) — Three dimensions
- [Implementing a Rule](../../articles/implementing-a-rule/) — Detailed executor documentation
