package io.github.carmelolg.jcal.core;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellState;

/**
 * Abstract base class for implementing the transition function of a Cellular Automata.
 *
 * <p>To define the behaviour of your automaton, create a concrete subclass and implement
 * {@link #singleRun(Cell, java.util.List)}.  That method is called once per cell per
 * generation: it receives the current cell and its neighbours and must return the cell's
 * next state.
 *
 * <p><b>Minimal example – Game of Life rule:</b>
 * <pre>{@code
 * public class GameOfLifeRule extends CellularAutomataExecutor {
 *     private static final CellState DEAD  = new CellState("dead",  "0");
 *     private static final CellState ALIVE = new CellState("alive", "1");
 *
 *     public Cell singleRun(Cell cell, List<Cell> neighbors) {
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
 * @see io.github.carmelolg.jcal.core.parallel.CellularAutomataParallelExecutor
 */
public abstract class CellularAutomataExecutor {

	private static final Logger logger = LoggerFactory.getLogger(CellularAutomataExecutor.class);

	/**
	 * Run the transaction function
	 * 
	 * @param ca the {@link CellularAutomata} configured
	 * @return the new {@link CellularAutomata} after n-interactions
	 * @throws Exception if something go wrong.
	 */
	public CellularAutomata run(CellularAutomata ca) throws Exception {

		NativeRule nativeRule = ca.getConfig().getNativeRule();
		if (nativeRule != null && NativeEngine.isAvailable()) {
			logger.info("Routing to native Rust engine (rule={})", nativeRule);
			return runNative(ca, nativeRule);
		}

		logger.info("Starting execution with {} iterations", 
			ca.getConfig().isInfinite() ? "infinite" : ca.getConfig().getTotalIterations());
		
		if (ca.getConfig().isInfinite()) {
			while (!Thread.currentThread().isInterrupted()) {
				innerRun(ca);
			}
		} else {
			int totalIterations = ca.getConfig().getTotalIterations();
			for (int i = 0; i < totalIterations; i++) {
				innerRun(ca);
				if ((i + 1) % Math.max(1, totalIterations / 10) == 0 || i == 0) {
					logger.debug("Completed iteration {}/{}", i + 1, totalIterations);
				}
			}
		}
		logger.info("Execution completed");
		return ca;

	}

	// ─────────────────────────────────────────────────────────────────────────
	// Native execution path
	// ─────────────────────────────────────────────────────────────────────────

	private CellularAutomata runNative(CellularAutomata ca, NativeRule rule) throws Exception {
		CellularAutomataConfiguration cfg = ca.getConfig();
		int[] dims = cfg.getDimensions();
		int ndim = dims.length;

		// Neighborhood: 0 = Moore, 1 = Von Neumann
		int nbhd = cfg.getNeighborhoodType() != null
				? cfg.getNeighborhoodType().ordinal()
				: 0;

		// Encode current grid → flat int[]
		CellGrid grid = ca.getGrid();
		List<int[]> allCoords = grid.allCoordinates();
		int cellCount = allCoords.size();
		int[] states = new int[cellCount];
		CellState defaultStatus = cfg.getDefaultStatus();
		for (int i = 0; i < cellCount; i++) {
			states[i] = grid.get(allCoords.get(i)).getCurrentStatus().equals(defaultStatus) ? 0 : 1;
		}

		// Identify the "alive" state (first non-default entry in initialState list)
		CellState aliveStatus = defaultStatus;
		if (cfg.getInitalState() != null) {
			for (Cell c : cfg.getInitalState()) {
				if (!c.getCurrentStatus().equals(defaultStatus)) {
					aliveStatus = c.getCurrentStatus();
					break;
				}
			}
		}

		try (NativeAutomaton automaton = createNativeAutomaton(ndim, dims, nbhd, rule)) {
			automaton.initCells(states);
			automaton.run(cfg.getTotalIterations());
			automaton.getGrid(states);
		}

		// Decode flat int[] → grid
		final CellState alive = aliveStatus;
		for (int i = 0; i < cellCount; i++) {
			int[] coords = allCoords.get(i);
			CellState newState = states[i] == 0 ? defaultStatus : alive;
			grid.get(coords).setCurrentStatus(newState);
		}
		ca.setGrid(grid);

		logger.info("Native execution completed");
		return ca;
	}

	private NativeAutomaton createNativeAutomaton(int ndim, int[] dims, int nbhd, NativeRule rule) {
		return switch (ndim) {
			case 2 -> NativeEngine.create2d(dims[0], dims[1], nbhd, rule.getId());
			case 3 -> NativeEngine.create3d(dims[0], dims[1], dims[2], nbhd, rule.getId());
			case 4 -> NativeEngine.create4d(dims[0], dims[1], dims[2], dims[3], nbhd, rule.getId());
			default -> throw new IllegalArgumentException("Unsupported dimensions: " + ndim);
		};
	}

	private CellularAutomata innerRun(CellularAutomata ca) throws CloneNotSupportedException {

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
			next.set(coords, singleRun(current.get(coords),
					ca.getNeighborhood().getNeighbors(current, coords)));
		}

		// Step 3: double-buffer swap
		logger.debug("Swapping buffers");
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
	 * @return the {@link Cell} updated
	 */
	public abstract Cell singleRun(Cell cell, List<Cell> neighbors);
	
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
