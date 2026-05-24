package io.github.carmelolg.jcal.core.parallel;

import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.Cell;

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
