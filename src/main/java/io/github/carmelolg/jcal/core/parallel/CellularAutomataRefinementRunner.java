package io.github.carmelolg.jcal.core.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.core.grid.CellGrid;
import io.github.carmelolg.jcal.model.DefaultCell;

public class CellularAutomataRefinementRunner implements Callable<List<DefaultCell>> {

	private CellularAutomata ca;
	private int row, offset;
	private CellularAutomataParallelExecutor executor;

	/**
	 * Callable are used to implement the parallelism using JDK. Each instance of this class run on a single thread.
	 * @param ca the {@link CellularAutomata} instance
	 * @param row the current row where to run the transition function
	 * @param offset the offset in order to create a chunk where run the transition function. Ex. chunk [row, row + offset]
	 * @param executor the executor implemented in order to run the custom transition function
	 */
	protected CellularAutomataRefinementRunner(CellularAutomata ca, int row, int offset, CellularAutomataParallelExecutor executor) {
		this.ca = ca;
		this.row = row;
		this.offset = offset;
		this.executor = executor;
	}

	@Override
	public List<DefaultCell> call() throws Exception {
		List<DefaultCell> results = new ArrayList<DefaultCell>();
		CellGrid grid = ca.getGrid();

		for (int[] coords : grid.allCoordinates()) {
			if (coords[0] >= row && coords[0] < (row + 1) * offset) {
				grid.set(coords, executor.refinements(grid.get(coords)));
			}
		}

		return results;
	}

}
