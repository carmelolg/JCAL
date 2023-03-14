package it.carmelolagamba.jcal.core;

import java.util.List;

import it.carmelolagamba.jcal.model.DefaultCell;
import it.carmelolagamba.jcal.utils.Utils;

/**
 * @author Carmelo La Gamba © 2023 is licensed under CC BY-NC-SA 4.0
 */
public abstract class CellularAutomataExecutor {

	/**
	 * Run the transaction function
	 * 
	 * @param ca the {@link CellularAutomata} configured
	 * @return the new {@link CellularAutomata} after n-interactions
	 * @throws Exception if something go wrong.
	 */
	public CellularAutomata run(CellularAutomata ca) throws Exception {

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

		ca.setUtilsMap(Utils.cloneMaps(ca.getMap()));

		for (int i = 0; i < ca.getMap().length; i++) {
			for (int j = 0; j < ca.getMap()[i].length; j++) {
				ca.getUtilsMap()[i][j] = singleRun(ca.getMap()[i][j],
						ca.getNeighborhood().getNeighbors(ca.getMap(), i, j));
			}
		}

		ca.setMap(Utils.cloneMaps(ca.getUtilsMap()));

		return ca;

	}

	/**
	 * The single run is the transaction function's core. Here, you explain what
	 * happen and what your transaction function do. Consider to implement only what
	 * happen in a single cell, this behavior will be replaced for all cells of the
	 * matrix You will receive in input the single cell and its neighbors
	 * 
	 * 
	 * @param <b>cell</b>      a single cell
	 * @param <b>neighbors</b> the neighbors
	 * @return the {@link DefaultCell} updated
	 */
	public abstract DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors);
}
