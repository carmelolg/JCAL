package io.github.carmelolg.jcal.ui;

import io.github.carmelolg.jcal.grid.GridSnapshot;

/**
 * Abstraction for any component capable of displaying a {@link GridSnapshot}.
 *
 * <p>{@link AutomataListener} depends on this interface rather than on the concrete
 * {@link AutomataWindow}, which makes it easy to:
 * <ul>
 *   <li>swap in a custom rendering component (e.g., a JavaFX canvas wrapper)</li>
 *   <li>unit-test listeners without a real Swing window</li>
 * </ul>
 *
 * <p>{@link AutomataWindow} is the standard implementation provided by JCAL.
 *
 * @author Carmelo La Gamba
 * @see AutomataWindow
 * @see AutomataListener
 */
@FunctionalInterface
public interface GridDisplay {

    /**
     * Pushes a new {@link GridSnapshot} to the display.
     *
     * <p>Implementations must be prepared to receive calls from any thread and are
     * responsible for dispatching to the correct UI thread if required.
     *
     * @param snapshot the grid state to display; never {@code null}
     */
    void update(GridSnapshot snapshot);
}
