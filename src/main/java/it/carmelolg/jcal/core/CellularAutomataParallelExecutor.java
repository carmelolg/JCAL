package it.carmelolg.jcal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.carmelolg.jcal.model.DefaultCell;
import it.carmelolg.jcal.utils.Utils;

/**
 * @author Carmelo La Gamba © 2023 is licensed under CC BY-NC-SA 4.0
 */
public abstract class CellularAutomataParallelExecutor {

	/**
	 * Run using parallelism the transaction function
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

	private CellularAutomata innerRun(CellularAutomata ca) throws CloneNotSupportedException, NoSuchMethodException,
			SecurityException, InterruptedException, ExecutionException {

		ca.setUtilsMap(Utils.cloneMaps(ca.getMap()));

		Collection<CellularAutomataRunner> tasks = new ArrayList<CellularAutomataRunner>();
		for (int i = 0; i < ca.getMap().length; i++) {
			tasks.add(new CellularAutomataRunner(ca, i, 1, this));
		}
		tasks.stream().parallel().forEach(task -> {
			try {
				task.call();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

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
