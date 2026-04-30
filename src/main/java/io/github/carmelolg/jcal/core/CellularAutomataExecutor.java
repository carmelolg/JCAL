package io.github.carmelolg.jcal.core;

import java.util.List;

import io.github.carmelolg.jcal.core.grid.CellGrid;
import io.github.carmelolg.jcal.model.DefaultCell;

/**
 * Abstract base class for implementing the transition function of a Cellular Automata.
 *
 * <p>To define the behaviour of your automaton, create a concrete subclass and implement
 * {@link #singleRun(DefaultCell, java.util.List)}.  That method is called once per cell per
 * generation: it receives the current cell and its neighbours and must return the cell's
 * next state.
 *
 * <p><b>Minimal example – Game of Life rule:</b>
 * <pre>{@code
 * public class GameOfLifeRule extends CellularAutomataExecutor {
 *     private static final DefaultStatus DEAD  = new DefaultStatus("dead",  "0");
 *     private static final DefaultStatus ALIVE = new DefaultStatus("alive", "1");
 *
 *     public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {
 *         long aliveCount = neighbors.stream()
 *             .filter(n -> n.getCurrentStatus().equals(ALIVE)).count();
 *         DefaultCell next = new DefaultCell(DEAD, cell.getCol(), cell.getRow());
 *         boolean isAlive = cell.getCurrentStatus().equals(ALIVE);
 *         if (!isAlive && aliveCount == 3) next.setCurrentStatus(ALIVE);
 *         else if (isAlive && (aliveCount == 2 || aliveCount == 3)) next.setCurrentStatus(ALIVE);
 *         return next;
 *     }
 * }
 * }</pre>
 *
 * <p>For <em>Complex Cellular Automata</em> that need to pre-process cell state before
 * computing neighbours, override {@link #refinements(DefaultCell)} as well.
 *
 * <p>For multi-threaded execution see
 * {@link io.github.carmelolg.jcal.core.parallel.CellularAutomataParallelExecutor}.
 *
 * @author Carmelo La Gamba
 * @see CellularAutomata
 * @see io.github.carmelolg.jcal.core.parallel.CellularAutomataParallelExecutor
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
			while (!Thread.currentThread().isInterrupted()) {
				innerRun(ca);
			}
		} else {
			for (int i = 0; i < ca.getConfig().getTotalIterations(); i++) {
				innerRun(ca);
			}
		}
		return ca;

	}

	private CellularAutomata innerRun(CellularAutomata ca) throws CloneNotSupportedException {

		CellGrid current = ca.getGrid();
		CellGrid next = ca.getUtilsGrid();

		// Step 1: refinements in-place on current
		for (int[] coords : current.allCoordinates()) {
			current.set(coords, refinements(current.get(coords)));
		}

		// Step 2: transition — read current, write next
		for (int[] coords : current.allCoordinates()) {
			next.set(coords, singleRun(current.get(coords),
					ca.getNeighborhood().getNeighbors(current, coords)));
		}

		// Step 3: double-buffer swap
		ca.setGrid(next);
		ca.setUtilsGrid(current);

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
