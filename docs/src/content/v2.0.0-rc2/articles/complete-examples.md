---
title: "Complete Examples"
date: 2025-01-01
draft: false
summary: "Fully-commented, runnable examples that demonstrate complete cellular automata implementations."
weight: 25
toc: true
tags: ["examples", "tutorial", "code"]
---

This section showcases **production-ready, fully-commented examples** from the JCAL repository.
Each example can be copied and run immediately. Refer to them as templates for your own automata.

---

## Conway's Game of Life (2D)

**File:** `GameOfLifeExample.java`

The classic two-state 2D cellular automaton. Demonstrates the four core steps:
1. Define cell states
2. Create an initial configuration
3. Build the grid
4. Implement the transition rule

{{< callout type="info" >}}
The example runs a **blinker** pattern (three horizontally-aligned cells) for 2 iterations.
After one generation, the blinker rotates 90°; after two, it returns to its starting state.
{{< /callout >}}

### Full Code

```java
package io.github.carmelolg.jcal.examples;

import java.util.Arrays;
import java.util.List;

import io.github.carmelolg.jcal.core.CellularAutomataConfiguration;
import io.github.carmelolg.jcal.core.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder;
import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.core.CellularAutomataExecutor;
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
        public Cell singleRun(Cell cell, List<Cell> neighbors) {
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

### Key Concepts

- **States:** Two boolean states (dead/alive) with string keys and display values.
- **Neighborhoods:** Uses Moore neighborhood (8 adjacent cells).
- **Configuration:** Finite grid (10×10), 2 iterations, default state (dead).
- **Rule:** Classic Conway rules encoded in `singleRun`.

---

## Heat Diffusion with Custom States

**File:** `CustomStateExample.java`

Demonstrates how to use **multi-valued cell states** to model numeric quantities.
This automaton simulates heat spreading from hot cells to their neighbors using a Von Neumann
(4-directional) neighborhood.

{{< callout type="info" >}}
The example places two HOT cells at opposite corners and evolves for 3 iterations.
Heat spreads orthogonally (not diagonally) to adjacent cells.
{{< /callout >}}

### Full Code

```java
package io.github.carmelolg.jcal.examples;

import java.util.Arrays;
import java.util.List;

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
        System.out.println("Grid after 3 iterations (0=cold, 1=warm, 2=hot):");
        System.out.println(ca);
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

### Key Concepts

- **Multi-valued states:** Integer values (0=cold, 1=warm, 2=hot) attached to state keys.
- **Von Neumann neighborhood:** 4 orthogonal neighbors (up, down, left, right).
- **State transition logic:** Complex rules based on neighbor counts of different state types.
- **Practical domain modeling:** Applicable to temperature, pressure, concentration, or other
  scalar quantities in cellular automata.

---

## 3D Game of Life (Carter Bays' Rules)

**File:** `GameOfLife3DExample.java`

Extends cellular automata to **three dimensions**. Uses Carter Bays' S5,6/B5 rule set,
where cells survive with 5–6 alive neighbors and are born with exactly 5.

{{< callout type="info" >}}
The example creates a **6-cell diagonal still life** in a 7×7×7 grid that remains stable
across 5 iterations. Every cell has exactly 5 alive Moore neighbors within the group.
{{< /callout >}}

### Full Code

```java
package io.github.carmelolg.jcal.examples;

import java.util.ArrayList;
import java.util.List;

import io.github.carmelolg.jcal.core.CellularAutomataConfiguration;
import io.github.carmelolg.jcal.core.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder;
import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.core.CellularAutomataExecutor;
import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.neighborhood.NeighborhoodType;

/**
 * A three-dimensional variant of Conway's Game of Life using JCAL.
 *
 * <p>This example demonstrates how to extend the library to 3D grids
 * using the {@code setDimensions(x, y, z)} builder method and a
 * {@link CellGrid}-backed grid.
 *
 * <p>The rules used here are <em>Carter Bays' 3D Life</em> (one common variant):
 * <ul>
 *   <li>An alive cell with 5 or 6 alive neighbours survives.</li>
 *   <li>A dead cell with exactly 5 alive neighbours is born.</li>
 *   <li>All other cells die or remain dead.</li>
 * </ul>
 *
 * <p>The initial configuration is a 6-cell diagonal <em>still life</em>:
 * each of the six cells has exactly 5 alive Moore neighbours within the group,
 * satisfying the survival condition, and no adjacent dead cell has exactly 5
 * alive neighbours, so no new cells are born.  The pattern therefore remains
 * unchanged across all iterations.
 *
 * <p><b>Note on the 2×2×2 block</b>: a compact 2×2×2 block is <em>not</em>
 * a valid seed for these rules — every cell in the block has 7 alive neighbours
 * (exceeding the survival range of 5–6), and no adjacent dead cell reaches the
 * birth threshold of exactly 5, so the block collapses to zero in one step.
 *
 * @see GameOfLifeExample for the 2D version
 */
public class GameOfLife3DExample {

	public static final CellState DEAD  = new CellState("dead",  "0");
	public static final CellState ALIVE = new CellState("alive", "1");

	/**
	 * The six coordinates that form the 3D still-life under Carter Bays' S5,6/B5 rules.
	 * Each cell has exactly 5 alive Moore neighbours within the group.
	 */
	public static final int[][] STILL_LIFE_COORDS = {
		{3, 3, 3}, {3, 4, 3}, {4, 3, 3}, {4, 4, 3}, {3, 3, 4}, {4, 4, 4}
	};

	public static void main(String[] args) throws Exception {

		// 7x7x7 grid; initial state is a 6-cell diagonal still life
		List<Cell> initialState = new ArrayList<>();
		for (int[] c : STILL_LIFE_COORDS)
			initialState.add(new Cell(ALIVE, c[0], c[1], c[2]));

		CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
				.setDimensions(7, 7, 7)
				.setTotalIterations(5)
				.setDefaultStatus(DEAD)
				.setNeighborhoodType(NeighborhoodType.MOORE)
				.setInitalState(initialState)
				.build();

		CellularAutomata ca = new CellularAutomata(config);

		CellularAutomataExecutor rule = new Carter3DLifeRule();
		ca = rule.run(ca);

		// Print the alive cells after 3 iterations (still life: identical to the initial state)
		System.out.println("Alive cells after 3 iterations (Carter Bays' 3D still life):");
		CellGrid grid = ca.getGrid();
		for (int[] coords : grid.allCoordinates()) {
			Cell cell = grid.get(coords);
			if (cell.getCurrentStatus().equals(ALIVE)) {
				System.out.printf("  (%d,%d,%d)%n", coords[0], coords[1], coords[2]);
			}
		}
	}

	/**
	 * Carter Bays' survival rule for 3D Life:
	 * survive on 5–6 alive neighbours; born on exactly 5 alive neighbours.
	 */
	public static class Carter3DLifeRule extends CellularAutomataExecutor {

		@Override
		public Cell singleRun(Cell cell, List<Cell> neighbors) {
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
```

### Key Concepts

- **Multi-dimensional grids:** Use `setDimensions(x, y, z)` for 3D, or `setDimensions(x, y, z, w)` for 4D.
- **CellGrid access:** Use `ca.getGrid()` to access the underlying grid and iterate all coordinates.
- **3D coordinates:** Cells store their position as an array (e.g., `[3, 3, 3]`).
- **Higher-dimensional rules:** Same executor pattern as 2D; neighborhoods extend to adjacent cells
  in all dimensions.

---

## Tips for Writing Your Own Examples

1. **Start with a 2D automaton** using the Game of Life template.
2. **Define states clearly** with meaningful keys and display values.
3. **Keep `singleRun` simple** — it runs millions of times; avoid expensive operations.
4. **Test with small grids** (5×5 or 10×10) before scaling up.
5. **Use neighborhoods wisely:**
   - `MOORE` for 8-neighbor (2D) or 26-neighbor (3D) patterns.
   - `VON_NEUMANN` for 4-neighbor (2D) or 6-neighbor (3D) orthogonal flows.
6. **Validate behavior** by printing intermediate states and checking patterns.

---

## See Also

- [Getting Started](../getting-started/) — installation and first steps.
- [Implementing a Rule](../implementing-a-rule/) — detailed executor documentation.
- [Custom State Objects](../custom-state/) — advanced state patterns.
- [3D and 4D Support](../3d-4d-support/) — in-depth guide to multi-dimensional automata.
- [Configuration Reference](../configuration/) — all builder options.
