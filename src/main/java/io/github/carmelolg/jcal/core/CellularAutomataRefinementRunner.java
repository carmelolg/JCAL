package io.github.carmelolg.jcal.core;

import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.Cell;

/**
 * A {@link Callable} task that applies the refinement step to one row of the
 * automaton grid during a parallel Complex Cellular Automata (CCA) pre-processing pass.
 *
 * <p>Each instance is responsible for a single row (first-dimension slice) of the
 * {@link io.github.carmelolg.jcal.grid.CellGrid}. The refinement result for every
 * cell in the row is written back into the main grid in-place.
 *
 * <p>Instances are created and submitted to a thread pool by
 * {@link CellularAutomataParallelRule} before the transition step in each generation.
 *
 * @author Carmelo La Gamba
 * @see CellularAutomataParallelRule
 * @see CellularAutomataRunner
 */
public class CellularAutomataRefinementRunner implements Callable<Void> {

	private static final Logger logger = LoggerFactory.getLogger(CellularAutomataRefinementRunner.class);

	private CellularAutomata ca;
	private int row;
	private CellularAutomataParallelRule executor;

	/**
	 * Callable are used to implement the parallelism using JDK. Each instance of this class run on a single thread.
	 * @param ca the {@link CellularAutomata} instance
	 * @param row the row index this task is responsible for
	 * @param executor the executor implementing the custom refinement function
	 */
	protected CellularAutomataRefinementRunner(CellularAutomata ca, int row, CellularAutomataParallelRule executor) {
		this.ca = ca;
		this.row = row;
		this.executor = executor;
	}

	@Override
	public Void call() {
		logger.debug("Processing refinement for row {}", row);
		CellGrid grid = ca.getGrid();

		for (int[] coords : grid.coordinatesForRow(row)) {
			grid.set(coords, executor.refinements(grid.get(coords)));
		}

		return null;
	}

}
