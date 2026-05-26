package io.github.carmelolg.jcal.ui;

import java.awt.Color;

import io.github.carmelolg.jcal.grid.CellState;

/**
 * Maps a {@link CellState} to an AWT {@link Color} for rendering purposes.
 *
 * <p>Implement this interface to control how each cell state is visualised in a
 * {@link GridPanel}.  The interface is annotated with {@link FunctionalInterface}
 * so it can be supplied as a lambda or method reference.
 *
 * <p><b>Example — two-state (alive/dead) mapping:</b>
 * <pre>{@code
 * CellState DEAD  = new CellState("dead",  "0");
 * CellState ALIVE = new CellState("alive", "1");
 *
 * CellRenderer renderer = state -> state.equals(ALIVE) ? Color.GREEN : Color.BLACK;
 * }</pre>
 *
 * <p><b>Example — multi-state (heat diffusion) mapping:</b>
 * <pre>{@code
 * CellRenderer renderer = state -> switch (state.getKey()) {
 *     case "hot"  -> Color.RED;
 *     case "warm" -> Color.ORANGE;
 *     default     -> Color.BLUE;
 * };
 * }</pre>
 *
 * @author Carmelo La Gamba
 * @see GridPanel
 * @see CellularAutomataDisplay
 */
@FunctionalInterface
public interface CellRenderer {

    /**
     * Returns the color to use when painting a cell with the given state.
     *
     * @param state the current {@link CellState} of the cell; never {@code null}
     * @return the {@link Color} to paint that cell; must not be {@code null}
     */
    Color getColor(CellState state);
}
