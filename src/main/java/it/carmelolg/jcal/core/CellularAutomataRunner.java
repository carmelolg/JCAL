package it.carmelolg.jcal.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import it.carmelolg.jcal.model.DefaultCell;

public class CellularAutomataRunner implements Callable<List<DefaultCell>> {

	private CellularAutomata ca;
	private int row, offset;
	private CellularAutomataParallelExecutor executor;

	public CellularAutomataRunner(CellularAutomata ca, int row, int offset, CellularAutomataParallelExecutor executor) {
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
				ca.getUtilsMap()[i][j] = executor.singleRun(ca.getMap()[i][j], ca.getNeighborhood().getNeighbors(ca.getMap(), i, j));
			}
		}

		return results;
	}

}
