package io.github.carmelolg.jcal.core;

import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.Cell;

/**
 * A {@link Callable} task that processes one row of the automaton grid during a
 * parallel transition step.
 *
 * <p>Each instance is responsible for a single row (first-dimension slice) of the
 * {@link io.github.carmelolg.jcal.grid.CellGrid}. The transition result for every
 * cell in the row is written into the double-buffer grid ({@code utilsGrid}) so that
 * all cells see a consistent snapshot of the previous generation.
 *
 * <p>Instances are created and submitted to a thread pool by
 * {@link CellularAutomataParallelRule} during each generation step.
 *
 * @author Carmelo La Gamba
 * @see CellularAutomataParallelRule
 * @see CellularAutomataRefinementRunner
 */
public class CellularAutomataRunner implements Callable<Void> {

	private static final Logger logger = LoggerFactory.getLogger(CellularAutomataRunner.class);

	private CellularAutomata ca;
	private int row;
	private CellularAutomataParallelRule executor;

	/**
	 * Callable are used to implement the parallelism using JDK. Each instance of this class run on a single thread.
	 * @param ca the {@link CellularAutomata} instance
	 * @param row the row index this task is responsible for
	 * @param executor the executor implementing the custom transition function
	 */
	protected CellularAutomataRunner(CellularAutomata ca, int row, CellularAutomataParallelRule executor) {
		this.ca = ca;
		this.row = row;
		this.executor = executor;
	}

	@Override
	public Void call() {
		logger.debug("Processing transition for row {}", row);
		CellGrid grid = ca.getGrid();
		CellGrid utilsGrid = ca.getUtilsGrid();

		for (int[] coords : grid.coordinatesForRow(row)) {
			utilsGrid.set(coords, executor.transition(grid.get(coords),
					ca.getNeighborhood().getNeighbors(grid, coords)));
		}

		return null;
	}

}
