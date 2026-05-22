package io.github.carmelolg.jcal.core.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.Cell;

public class CellularAutomataRefinementRunner implements Callable<List<Cell>> {

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
	public List<Cell> call() {
		logger.debug("Processing refinement for row {}", row);
		List<Cell> results = new ArrayList<Cell>();
		CellGrid grid = ca.getGrid();

		int totalCells = grid.dimensions().getTotalCells();
		int rowCount = grid.dimensions().getSize(0);
		int sliceSize = totalCells / rowCount;

		List<int[]> slice = grid.allCoordinates().subList(row * sliceSize, (row + 1) * sliceSize);
		for (int[] coords : slice) {
			grid.set(coords, executor.refinements(grid.get(coords)));
		}

		return results;
	}

}
