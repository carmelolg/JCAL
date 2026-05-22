package io.github.carmelolg.jcal.core;

/**
 * Unchecked exception thrown by the JCAL engine when a configuration is invalid
 * or an internal operation fails.
 *
 * <p>Replaces the broad {@code throws Exception} previously declared on
 * {@link CellularAutomata} constructors and {@link CellularAutomataRule#run}.
 * Callers may catch this class explicitly when they need to handle automata errors
 * separately from other runtime failures.
 *
 * @author Carmelo La Gamba
 * @see CellularAutomata
 * @see CellularAutomataRule
 */
public class CellularAutomataException extends RuntimeException {

    public CellularAutomataException(String message) {
        super(message);
    }

    public CellularAutomataException(String message, Throwable cause) {
        super(message, cause);
    }
}
