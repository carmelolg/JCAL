package io.github.carmelolg.jcal.neighborhood;

/**
 * Marker interface for neighborhoods that support n-dimensional (n &gt; 2) grids.
 *
 * <p>When defining a custom neighborhood for 3D, 4D, or higher-dimensional
 * cellular automata, implement this interface in addition to extending {@link Neighborhood}:
 *
 * <pre>{@code
 * public class Custom3DNeighborhood extends Neighborhood implements NDCapable {
 *     @Override
 *     public List<Cell> getNeighbors(CellGrid grid, int[] coords) {
 *         // Custom logic for 3D+ grids
 *     }
 * }
 * }</pre>
 *
 * <p>This marker is used by {@link CellularAutomata} to validate that
 * n-dimensional grids are paired with neighborhoods that can handle them.
 *
 * @author Carmelo La Gamba
 * @see Neighborhood
 * @see CellularAutomata
 */
public interface NDCapable {
	// Marker interface - no methods needed
}
