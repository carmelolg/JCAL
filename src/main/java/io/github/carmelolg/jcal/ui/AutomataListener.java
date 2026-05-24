package io.github.carmelolg.jcal.ui;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.carmelolg.jcal.core.GenerationListener;
import io.github.carmelolg.jcal.grid.GridSnapshot;

/**
 * A {@link GenerationListener} that forwards each completed generation to a
 * {@link GridDisplay} and optionally throttles the animation speed.
 *
 * <p>Register an instance of this class on a
 * {@link io.github.carmelolg.jcal.core.CellularAutomataRule} to see the grid
 * evolve in real time.  The standard {@link GridDisplay} implementation is
 * {@link CellularAutomataDisplay}; you may supply any alternative display (e.g., a JavaFX
 * wrapper or a test stub) as long as it implements {@link GridDisplay}.
 *
 * <p>The optional {@code delayMs} parameter introduces a {@link Thread#sleep}
 * between generations so the animation is visible at a human-readable pace.
 *
 * <p><b>Typical usage:</b>
 * <pre>{@code
 * CellularAutomataDisplay window = new CellularAutomataDisplay(renderer, 12);
 * window.show();
 *
 * AutomataListener listener = new AutomataListener(window, 100); // 100 ms per frame
 * rule.addGenerationListener(listener);
 *
 * // Run on a background thread so the EDT stays free:
 * new Thread(() -> {
 *     try { rule.run(ca); } catch (Exception e) { e.printStackTrace(); }
 * }).start();
 * }</pre>
 *
 * <p>The delay is applied <em>on the execution thread</em> (i.e., the thread that
 * calls {@link io.github.carmelolg.jcal.core.CellularAutomataRule#run}), so it
 * effectively limits the frame rate without blocking the Swing EDT.
 *
 * @author Carmelo La Gamba
 * @see CellularAutomataDisplay
 * @see GenerationListener
 */
public class AutomataListener implements GenerationListener {

    private static final Logger logger = LoggerFactory.getLogger(AutomataListener.class);

    private final GridDisplay display;
    private final int delayMs;

    /**
     * Creates an {@code AutomataListener} that updates the given display and waits
     * {@code delayMs} milliseconds after each generation.
     *
     * @param display the {@link GridDisplay} to update; must not be {@code null}
     * @param delayMs the delay in milliseconds between generations (0 = no delay)
     * @throws IllegalArgumentException if {@code delayMs} is negative
     */
    public AutomataListener(GridDisplay display, int delayMs) {
        Objects.requireNonNull(display, "display must not be null");
        if (delayMs < 0) throw new IllegalArgumentException("delayMs must be >= 0, got: " + delayMs);
        this.display = display;
        this.delayMs = delayMs;
    }

    /**
     * Creates an {@code AutomataListener} with no inter-generation delay.
     *
     * @param display the {@link GridDisplay} to update; must not be {@code null}
     */
    public AutomataListener(GridDisplay display) {
        this(display, 0);
    }

    /**
     * Called by the engine after each completed generation.
     *
     * <p>Forwards the snapshot to the window and then sleeps {@code delayMs} ms on
     * the current (execution) thread to throttle the animation speed.
     *
     * @param generation the 1-based generation index
     * @param snapshot   the immutable grid state after the transition
     */
    @Override
    public void onGeneration(int generation, GridSnapshot snapshot) {
        display.update(snapshot);
        if (delayMs > 0) {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.debug("AutomataListener interrupted during delay at generation {}", generation);
            }
        }
    }
}
