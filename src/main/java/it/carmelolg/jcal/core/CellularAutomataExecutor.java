package it.carmelolg.jcal.core;

import java.util.List;

import it.carmelolg.jcal.model.DefaultCell;
import it.carmelolg.jcal.utils.Utils;

public abstract class CellularAutomataExecutor {

	public CellularAutomata run(CellularAutomata ca) throws Exception {

		// Start transaction function
		if (ca.getConfig().isInfinite()) {
			while (true) {
				return innerRun(ca);
			}
		} else {
			int i = 0;
			while (i < ca.getConfig().getTotalIterations() - 1) {
				innerRun(ca);
				i++;
			}
			return innerRun(ca);
		}
		
	}

	private CellularAutomata innerRun(CellularAutomata ca) throws CloneNotSupportedException {

		// Clone map on utils map
		ca.setUtilsMap(Utils.cloneMaps(ca.getMap()));
		
		for (int i = 0; i < ca.getMap().length; i++) {
			for (int j = 0; j < ca.getMap()[i].length; j++) {
				ca.getUtilsMap()[i][j] = singleRun(ca.getMap()[i][j], ca.getNeighborhood().getNeighbors(ca.getMap(), i, j));
			}
		}

		// Clone the new state on the main map
		ca.setMap(Utils.cloneMaps(ca.getUtilsMap()));
		
		return ca;
		
	}

	public abstract DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors)
			throws CloneNotSupportedException;
}
