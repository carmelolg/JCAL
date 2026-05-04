package io.github.carmelolg.jcal.core.parallel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.Cell;

/**
 * Parallel variant of {@link io.github.carmelolg.jcal.core.CellularAutomataExecutor} that
 * processes cells concurrently using Java parallel streams.
 *
 * <p>The API is identical to its sequential counterpart: override
 * {@link #singleRun(io.github.carmelolg.jcal.grid.Cell, java.util.List)} with the
 * cell transition logic.  The framework distributes work across rows automatically.
 *
 * <p>Use this executor for large grids where sequential execution is too slow.  For small
 * grids or rapid prototyping, the non-parallel
 * {@link io.github.carmelolg.jcal.core.CellularAutomataExecutor} is simpler.
 *
 * @author Carmelo La Gamba
 * @see io.github.carmelolg.jcal.core.CellularAutomataExecutor
 */
public abstract class CellularAutomataParallelExecutor {

    private static final Logger logger = LoggerFactory.getLogger(CellularAutomataParallelExecutor.class);

    /**
     * Run using parallelism the transaction function
     *
     * @param ca the {@link CellularAutomata} configured
     * @return the new {@link CellularAutomata} after n-interactions
     * @throws Exception if something go wrong.
     */
    public CellularAutomata run(CellularAutomata ca) throws Exception {

        logger.info("Starting parallel execution with {} iterations", 
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
        logger.info("Parallel execution completed");
        return ca;

    }

    private CellularAutomata innerRun(CellularAutomata ca) throws SecurityException {

        int rowCount = ca.getGrid().dimensions().getSize(0);
        logger.debug("Starting parallel iteration cycle with {} rows", rowCount);

        // Step 1: refinements in-place on grid
        logger.debug("Submitting refinement tasks");
        Collection<CellularAutomataRefinementRunner> refinementTasks = new ArrayList<CellularAutomataRefinementRunner>();
        for (int i = 0; i < rowCount; i++) {
            refinementTasks.add(new CellularAutomataRefinementRunner(ca, i, 1, this));
        }
        refinementTasks.stream().parallel().forEach(task -> {
            try {
                task.call();
            } catch (Exception e) {
                logger.error("Error during parallel refinement", e);
                throw new RuntimeException(e);
            }
        });
        logger.debug("Refinement tasks completed");

        // Step 2: transition — read grid, write utilsGrid
        logger.debug("Submitting transition tasks");
        Collection<CellularAutomataRunner> tasks = new ArrayList<CellularAutomataRunner>();
        for (int i = 0; i < rowCount; i++) {
            tasks.add(new CellularAutomataRunner(ca, i, 1, this));
        }
        tasks.stream().parallel().forEach(task -> {
            try {
                task.call();
            } catch (Exception e) {
                logger.error("Error during parallel transition", e);
                throw new RuntimeException(e);
            }
        });
        logger.debug("Transition tasks completed");

        // Step 3: double-buffer swap
        logger.debug("Swapping buffers");
        CellGrid temp = ca.getGrid();
        ca.setGrid(ca.getUtilsGrid());
        ca.setUtilsGrid(temp);

        return ca;

    }

    /**
     * The single run is the transaction function's core. Here, you explain what
     * happen and what your transaction function do. Consider to implement only what
     * happen in a single cell, this behavior will be replaced for all cells of the
     * matrix You will receive in input the single cell and its neighbors
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
     *
     * @param cell the current cell to update
     * @return a {@link Cell} instance.
     */
    public Cell refinements(Cell cell) {
        return cell;
    }
}
