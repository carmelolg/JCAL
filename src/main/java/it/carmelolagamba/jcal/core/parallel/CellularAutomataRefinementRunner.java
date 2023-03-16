package it.carmelolagamba.jcal.core.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import it.carmelolagamba.jcal.core.CellularAutomata;
import it.carmelolagamba.jcal.model.DefaultCell;

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

		for (int i = row; i < (row + 1) * offset; i++) {
			for (int j = 0; j < ca.getMap()[0].length; j++) {
				ca.getMap()[i][j] = executor.refinements(ca.getMap()[i][j]);
			}
		}

		return results;
	}

}
