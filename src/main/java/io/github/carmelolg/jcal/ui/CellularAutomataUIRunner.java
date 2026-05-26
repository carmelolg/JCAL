package io.github.carmelolg.jcal.ui;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.carmelolg.jcal.core.AbstractCellularAutomataRule;
import io.github.carmelolg.jcal.core.CellularAutomata;

/**
 * Fluent façade that wires together a {@link CellularAutomataDisplay}, an
 * {@link AutomataListener}, and the execution thread in a single call.
 *
 * <p>Use this class when you want to visualise a Cellular Automata with minimal
 * boilerplate.  {@link #start()} opens the window and immediately begins the
 * evolution on a background daemon thread, keeping the Swing EDT free.
 *
 * <p><b>Minimal example — Game of Life:</b>
 * <pre>{@code
 * CellState DEAD  = new CellState("dead",  "0");
 * CellState ALIVE = new CellState("alive", "1");
 *
 * CellularAutomata ca = new CellularAutomata(config);
 *
 * CellularAutomataUIRunner.create(ca, new GameOfLifeRule())
 *     .cellSize(12)
 *     .delay(100)
 *     .renderer(state -> state.equals(ALIVE) ? Color.GREEN : Color.BLACK)
 *     .start();
 * }</pre>
 *
 * <p>For more control (custom layouts, multiple windows, manual threading) use
 * {@link CellularAutomataDisplay} and {@link AutomataListener} directly.
 *
 * @author Carmelo La Gamba
 * @see CellularAutomataDisplay
 * @see AutomataListener
 * @see CellRenderer
 */
public class CellularAutomataUIRunner {

    private static final Logger logger = LoggerFactory.getLogger(CellularAutomataUIRunner.class);

    private final CellularAutomata ca;
    private final AbstractCellularAutomataRule rule;

    private String title    = "JCAL Automata";
    private int cellSize    = 10;
    private int delayMs     = 100;
    private CellRenderer renderer;

    private CellularAutomataUIRunner(CellularAutomata ca, AbstractCellularAutomataRule rule) {
        this.ca   = ca;
        this.rule = rule;
    }

    /**
     * Creates a new {@code CellularAutomataUIRunner} builder for the given automaton and rule.
     *
     * @param ca   the {@link CellularAutomata} to visualise; must not be {@code null}
     * @param rule the {@link AbstractCellularAutomataRule} that will drive the evolution; must not be {@code null}
     * @return a new {@code CellularAutomataUIRunner} builder
     */
    public static CellularAutomataUIRunner create(CellularAutomata ca, AbstractCellularAutomataRule rule) {
        Objects.requireNonNull(ca,   "ca must not be null");
        Objects.requireNonNull(rule, "rule must not be null");
        return new CellularAutomataUIRunner(ca, rule);
    }

    /**
     * Sets the window title.
     *
     * @param title the window title; must not be {@code null}
     * @return this builder
     */
    public CellularAutomataUIRunner title(String title) {
        this.title = Objects.requireNonNull(title, "title must not be null");
        return this;
    }

    /**
     * Sets the pixel size of each cell (default: {@code 10}).
     *
     * @param cellSize pixels per cell; must be &gt; 0
     * @return this builder
     * @throws IllegalArgumentException if {@code cellSize} is not positive
     */
    public CellularAutomataUIRunner cellSize(int cellSize) {
        if (cellSize <= 0) throw new IllegalArgumentException("cellSize must be > 0, got: " + cellSize);
        this.cellSize = cellSize;
        return this;
    }

    /**
     * Sets the delay between generations in milliseconds (default: {@code 100}).
     *
     * @param delayMs milliseconds to wait after each generation; must be &gt;= 0
     * @return this builder
     * @throws IllegalArgumentException if {@code delayMs} is negative
     */
    public CellularAutomataUIRunner delay(int delayMs) {
        if (delayMs < 0) throw new IllegalArgumentException("delayMs must be >= 0, got: " + delayMs);
        this.delayMs = delayMs;
        return this;
    }

    /**
     * Sets the {@link CellRenderer} that maps cell states to colours.
     *
     * @param renderer the renderer; must not be {@code null}
     * @return this builder
     */
    public CellularAutomataUIRunner renderer(CellRenderer renderer) {
        this.renderer = Objects.requireNonNull(renderer, "renderer must not be null");
        return this;
    }

    /**
     * Opens the window and starts the automaton evolution on a background daemon thread.
     *
     * <p>This method returns immediately; the evolution continues asynchronously.
     * The window will update after each generation until all iterations are complete
     * or the thread is interrupted.
     *
     * @throws IllegalStateException if no {@link CellRenderer} has been set
     */
    public void start() {
        if (renderer == null) {
            throw new IllegalStateException(
                    "A CellRenderer must be set before calling start(). Use .renderer(...).");
        }

        CellularAutomataDisplay display = new CellularAutomataDisplay(title, renderer, cellSize);
        display.show();

        AutomataListener listener = new AutomataListener(display, delayMs);
        rule.addGenerationListener(listener);

        Thread thread = new Thread(() -> {
            try {
                logger.info("CellularAutomataUIRunner: starting evolution");
                rule.run(ca);
                logger.info("CellularAutomataUIRunner: evolution complete");
            } catch (Exception e) {
                logger.error("CellularAutomataUIRunner: error during evolution", e);
            }
        }, "jcal-ui-runner");
        thread.setDaemon(true);
        thread.start();
    }
}
