package io.github.carmelolg.jcal.examples;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger logger = LoggerFactory.getLogger(GameOfLife3DExample.class);

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
		logger.info("Alive cells after 3 iterations (Carter Bays' 3D still life):");
		CellGrid grid = ca.getGrid();
		for (int[] coords : grid.allCoordinates()) {
			Cell cell = grid.get(coords);
			if (cell.getCurrentStatus().equals(ALIVE)) {
				logger.info("  ({},{},{})", coords[0], coords[1], coords[2]);
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
