package io.github.carmelolg.jcal.core;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellGrid;

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
 * {@link CellularAutomataParallelRule}.
 *
 * @author Carmelo La Gamba
 * @see CellularAutomata
 * @see CellularAutomataParallelRule
 */
public abstract class CellularAutomataRule extends AbstractCellularAutomataRule {

	private static final Logger logger = LoggerFactory.getLogger(CellularAutomataRule.class);

	/**
	 * Executes a single generation sequentially: applies refinements in-place on the
	 * current grid, computes transitions into the utility buffer, then swaps the buffers.
	 *
	 * @param ca the automaton to advance by one generation
	 * @return the updated automaton
	 */
	@Override
	protected CellularAutomata executeGeneration(CellularAutomata ca) {

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
		swapBuffers(ca);

		return ca;
	}
}
