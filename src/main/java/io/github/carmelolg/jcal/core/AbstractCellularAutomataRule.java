package io.github.carmelolg.jcal.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.GridSnapshot;

/**
 * Common base for all cellular automata rule executors.
 *
 * <p>Provides shared listener management ({@link GenerationListener}) and declares the
 * contract that both sequential ({@link CellularAutomataRule}) and parallel
 * ({@link io.github.carmelolg.jcal.core.parallel.CellularAutomataParallelRule})
 * implementations must fulfil.
 *
 * @author Carmelo La Gamba
 * @see CellularAutomataRule
 * @see io.github.carmelolg.jcal.core.parallel.CellularAutomataParallelRule
 */
public abstract class AbstractCellularAutomataRule {

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

	/**
	 * Notifies all registered listeners that a generation has completed.
	 *
	 * @param generation the completed generation number (1-based)
	 * @param ca         the automaton after the generation
	 */
	protected void notifyListeners(int generation, CellularAutomata ca) {
		if (!listeners.isEmpty()) {
			GridSnapshot snapshot = GridSnapshot.of(generation, ca.getGrid());
			for (GenerationListener l : listeners) {
				l.onGeneration(generation, snapshot);
			}
		}
	}

	/**
	 * Swaps the double-buffer grids of the automaton after a generation step.
	 * Called by subclasses at the end of each {@code innerRun()}.
	 *
	 * @param ca the automaton whose buffers to swap
	 */
	protected final void swapBuffers(CellularAutomata ca) {
		CellGrid temp = ca.getGrid();
		ca.setGrid(ca.getUtilsGrid());
		ca.setUtilsGrid(temp);
	}

	/**
	 * Runs the automaton for the number of iterations defined in its configuration.
	 *
	 * @param ca the {@link CellularAutomata} to run
	 * @return the updated {@link CellularAutomata}
	 * @throws CellularAutomataException if something goes wrong during execution
	 */
	public abstract CellularAutomata run(CellularAutomata ca);

	/**
	 * The transition function applied to every cell each generation.
	 *
	 * @param cell      the current cell
	 * @param neighbors the cell's neighbours
	 * @return the cell in its next state
	 */
	public abstract Cell transition(Cell cell, List<Cell> neighbors);

	/**
	 * Optional pre-processing step for Complex Cellular Automata.
	 * Override to update cell state before neighbours are evaluated.
	 * Simple automata do not need to override this method.
	 *
	 * @param cell the current cell
	 * @return the (possibly updated) cell
	 */
	public Cell refinements(Cell cell) {
		return cell;
	}
}
