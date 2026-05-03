package io.github.carmelolg.jcal.core.grid;

import io.github.carmelolg.jcal.model.DefaultCell;
import io.github.carmelolg.jcal.model.GridDimensions;
import java.util.List;

/**
 * Abstraction over an n-dimensional grid of {@link DefaultCell}s.
 *
 * <p>Implementations include {@link CellGrid2D} for backward-compatible 2D grids
 * and {@link CellGridFlat} for 3D/4D flat-array grids.
 *
 * @author Carmelo La Gamba
 */
public interface CellGrid {

	/**
	 * Gets the cell at the given coordinates.
	 *
	 * @param coords the coordinates (one per dimension)
	 * @return the cell at those coordinates
	 */
	DefaultCell get(int... coords);

	/**
	 * Sets the cell at the given coordinates.
	 *
	 * @param coords the coordinates (one per dimension)
	 * @param cell the cell to place
	 */
	void set(int[] coords, DefaultCell cell);

	/**
	 * Returns the dimensions of this grid.
	 *
	 * @return the {@link GridDimensions}
	 */
	GridDimensions dimensions();

	/**
	 * Returns all coordinate arrays, in row-major order.
	 *
	 * @return list of coordinate arrays
	 */
	List<int[]> allCoordinates();
}
