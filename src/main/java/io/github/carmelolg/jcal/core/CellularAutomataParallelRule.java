package io.github.carmelolg.jcal.core;

import java.util.ArrayList;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parallel variant of {@link io.github.carmelolg.jcal.core.CellularAutomataRule} that
 * processes cells concurrently using Java parallel streams.
 *
 * <p>The API is identical to its sequential counterpart: override
 * {@link #transition(io.github.carmelolg.jcal.grid.Cell, java.util.List)} with the
 * cell transition logic.  The framework distributes work across rows automatically.
 *
 * <p>Use this executor for large grids where sequential execution is too slow.  For small
 * grids or rapid prototyping, the non-parallel
 * {@link io.github.carmelolg.jcal.core.CellularAutomataRule} is simpler.
 *
 * @author Carmelo La Gamba
 * @see CellularAutomataRule
 */
public abstract class CellularAutomataParallelRule extends AbstractCellularAutomataRule {

    private static final Logger logger = LoggerFactory.getLogger(CellularAutomataParallelRule.class);

    /**
     * Executes a single generation using parallel streams: applies refinements
     * concurrently per row, computes transitions into the utility buffer in parallel,
     * then swaps the buffers.
     *
     * @param ca the automaton to advance by one generation
     * @return the updated automaton
     */
    @Override
    protected CellularAutomata executeGeneration(CellularAutomata ca) {

        int rowCount = ca.getGrid().dimensions().getSize(0);
        logger.debug("Starting parallel iteration cycle with {} rows", rowCount);

        // Step 1: refinements in-place on grid
        // Any CellularAutomataException from transition() propagates directly (no wrapping)
        logger.debug("Submitting refinement tasks");
        Collection<CellularAutomataRefinementRunner> refinementTasks = new ArrayList<CellularAutomataRefinementRunner>();
        for (int i = 0; i < rowCount; i++) {
            refinementTasks.add(new CellularAutomataRefinementRunner(ca, i, this));
        }
        refinementTasks.stream().parallel().forEach(task -> task.call());
        logger.debug("Refinement tasks completed");

        // Step 2: transition — read grid, write utilsGrid
        // Any CellularAutomataException from transition() propagates directly (no wrapping)
        logger.debug("Submitting transition tasks");
        Collection<CellularAutomataRunner> tasks = new ArrayList<CellularAutomataRunner>();
        for (int i = 0; i < rowCount; i++) {
            tasks.add(new CellularAutomataRunner(ca, i, this));
        }
        tasks.stream().parallel().forEach(task -> task.call());
        logger.debug("Transition tasks completed");

        // Step 3: double-buffer swap
        logger.debug("Swapping buffers");
        swapBuffers(ca);

        return ca;

    }
}
