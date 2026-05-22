package io.github.carmelolg.jcal.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.GridSnapshot;

/**
 * Abstract base class for implementing the transition function of a Cellular Automata.
 *
 * <p>To define the behaviour of your automaton, create a concrete subclass and implement
 * {@link #transition(Cell, java.util.List)}.  That method is called once per cell per
 * generation: it receives the current cell and its neighbours and must return the cell's
 * next state.
 *
 * <p><b>Minimal example – Game of Life rule:</b>
 * <pre>{@code
 * public class GameOfLifeRule extends CellularAutomataRule {
 *     private static final CellState DEAD  = new CellState("dead",  "0");
 *     private static final CellState ALIVE = new CellState("alive", "1");
 *
 *     public Cell transition(Cell cell, List<Cell> neighbors) {
 *         long aliveCount = neighbors.stream()
 *             .filter(n -> n.getCurrentStatus().equals(ALIVE)).count();
 *         Cell next = new Cell(DEAD, cell.getCol(), cell.getRow());
 *         boolean isAlive = cell.getCurrentStatus().equals(ALIVE);
 *         if (!isAlive && aliveCount == 3) next.setCurrentStatus(ALIVE);
 *         else if (isAlive && (aliveCount == 2 || aliveCount == 3)) next.setCurrentStatus(ALIVE);
 *         return next;
 *     }
 * }
 * }</pre>
 *
 * <p>For <em>Complex Cellular Automata</em> that need to pre-process cell state before
 * computing neighbours, override {@link #refinements(Cell)} as well.
 *
 * <p>For multi-threaded execution see
 * {@link io.github.carmelolg.jcal.core.parallel.CellularAutomataParallelExecutor}.
 *
 * @author Carmelo La Gamba
 * @see CellularAutomata
 * @see io.github.carmelolg.jcal.core.parallel.CellularAutomataParallelRule
 */
public abstract class CellularAutomataRule {

	private static final Logger logger = LoggerFactory.getLogger(CellularAutomataRule.class);

	private final List<GenerationListener> listeners = new ArrayList<>();

	/**
	 * Registers a {@link GenerationListener} that will be notified after each completed
	 * generation when {@link #run(CellularAutomata)} is called.
	 *
	 * <p>Multiple listeners can be registered; they are called in registration order.
	 *
	 * @param listener the listener to add; must not be {@code null}
	 * @see GenerationListener
	 * @see GridSnapshot
	 */
	public void addGenerationListener(GenerationListener listener) {
		Objects.requireNonNull(listener, "listener must not be null");
		listeners.add(listener);
	}

	private void notifyListeners(int generation, CellularAutomata ca) {
		if (!listeners.isEmpty()) {
			GridSnapshot snapshot = GridSnapshot.of(generation, ca.getGrid());
			for (GenerationListener l : listeners) {
				l.onGeneration(generation, snapshot);
			}
		}
	}

	/**
	 * Run the transaction function
	 * 
	 * @param ca the {@link CellularAutomata} configured
	 * @return the new {@link CellularAutomata} after n-interactions
	 * @throws Exception if something go wrong.
	 */
	public CellularAutomata run(CellularAutomata ca) throws Exception {

		logger.info("Starting execution with {} iterations", 
			ca.getConfig().isInfinite() ? "infinite" : ca.getConfig().getTotalIterations());
		
		if (ca.getConfig().isInfinite()) {
			int gen = 0;
			while (!Thread.currentThread().isInterrupted()) {
				innerRun(ca);
				notifyListeners(++gen, ca);
			}
		} else {
			int totalIterations = ca.getConfig().getTotalIterations();
			for (int i = 0; i < totalIterations; i++) {
				innerRun(ca);
				notifyListeners(i + 1, ca);
				if ((i + 1) % Math.max(1, totalIterations / 10) == 0 || i == 0) {
					logger.debug("Completed iteration {}/{}", i + 1, totalIterations);
				}
			}
		}
		logger.info("Execution completed");
		return ca;

	}

	private CellularAutomata innerRun(CellularAutomata ca) {

		CellGrid current = ca.getGrid();
		CellGrid next = ca.getUtilsGrid();

		logger.debug("Starting iteration cycle");
		
		// Step 1: refinements in-place on current
		logger.debug("Applying refinements");
		for (int[] coords : current.allCoordinates()) {
			current.set(coords, refinements(current.get(coords)));
		}

		// Step 2: transition — read current, write next
		logger.debug("Computing transitions");
		for (int[] coords : current.allCoordinates()) {
			next.set(coords, transition(current.get(coords),
					ca.getNeighborhood().getNeighbors(current, coords)));
		}

		// Step 3: double-buffer swap
		logger.debug("Swapping buffers");
		ca.setGrid(next);
		ca.setUtilsGrid(current);

		return ca;
	}

	/**
	 * The transition function's core. Here, you explain what
	 * happen and what your transaction function do. Consider to implement only what
	 * happen in a single cell, this behavior will be replaced for all cells of the
	 * matrix You will receive in input the single cell and its neighbors
	 * 
	 * 
	 * @param <b>cell</b>      a single cell
	 * @param <b>neighbors</b> the neighbors
	 * @return the {@link Cell} updated
	 */
	public abstract Cell transition(Cell cell, List<Cell> neighbors);
	
	/**
	 * If you want to implement a CCA (Complex Cellular Automata), you need refine your cells status before the next iteration.
	 * If you override this function, you'll be able to update the status of the current cells before the next iteration.
	 * <b>If you use a simple CA, you can skip this implementation.</b>
	 * @param cell the current cell to update
	 * @return a {@link Cell} instance.
	 */
	public Cell refinements(Cell cell) {
		return cell;
	}
}
