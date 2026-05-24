package io.github.carmelolg.jcal.core.parallel;

import java.util.ArrayList;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.carmelolg.jcal.core.AbstractCellularAutomataRule;
import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.core.CellularAutomataException;

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
 * @see io.github.carmelolg.jcal.core.CellularAutomataRule
 */
public abstract class CellularAutomataParallelRule extends AbstractCellularAutomataRule {

    private static final Logger logger = LoggerFactory.getLogger(CellularAutomataParallelRule.class);

    /**
     * Run using parallelism the transaction function
     *
     * @param ca the {@link CellularAutomata} configured
     * @return the new {@link CellularAutomata} after n-interactions
     * @throws CellularAutomataException if something goes wrong during execution
     */
    public CellularAutomata run(CellularAutomata ca) {

        logger.info("Starting parallel execution with {} iterations",
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
        logger.info("Parallel execution completed");
        return ca;

    }

    private CellularAutomata innerRun(CellularAutomata ca) {

        int rowCount = ca.getGrid().dimensions().getSize(0);
        logger.debug("Starting parallel iteration cycle with {} rows", rowCount);

        // Step 1: refinements in-place on grid
        logger.debug("Submitting refinement tasks");
        Collection<CellularAutomataRefinementRunner> refinementTasks = new ArrayList<CellularAutomataRefinementRunner>();
        for (int i = 0; i < rowCount; i++) {
            refinementTasks.add(new CellularAutomataRefinementRunner(ca, i, this));
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
            tasks.add(new CellularAutomataRunner(ca, i, this));
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
        swapBuffers(ca);

        return ca;

    }
}
