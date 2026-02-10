package io.github.carmelolg.jcal.core.parallel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.model.DefaultCell;
import io.github.carmelolg.jcal.utils.Utils;

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

		
		Collection<CellularAutomataRefinementRunner> refinementTasks = new ArrayList<CellularAutomataRefinementRunner>();
		for (int i = 0; i < ca.getMap().length; i++) {
			refinementTasks.add(new CellularAutomataRefinementRunner(ca, i, 1, this));
		}
		refinementTasks.stream().parallel().forEach(task -> {
			try {
				task.call();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		
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
	
	/**
	 * If you want to implement a CCA (Complex Cellular Automata), you need refine your cells status before the next iteration.
	 * If you override this function, you'll able to update the status of the current cells before the next iteration.
	 * <b>If you use a simple CA, you can skip this implementation.</b>
	 * @param cell the current cell to update
	 * @return a {@link DefaultCell} instance.
	 */
	public DefaultCell refinements(DefaultCell cell) {
		return cell;
	}
}
