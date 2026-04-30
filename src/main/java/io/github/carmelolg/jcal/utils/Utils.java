package io.github.carmelolg.jcal.utils;

import io.github.carmelolg.jcal.core.grid.CellGrid;
import io.github.carmelolg.jcal.core.grid.CellGrid2D;
import io.github.carmelolg.jcal.core.grid.CellGridFlat;
import io.github.carmelolg.jcal.model.DefaultCell;

/**
 * Internal utility methods used by the JCAL framework.
 *
 * <p>These helpers are used throughout the library but are not part of the
 * public extension API.  Callers may use them when implementing custom
 * {@link io.github.carmelolg.jcal.core.DefaultNeighborhood} classes.
 *
 * @author Carmelo La Gamba
 */
public class Utils {
	
	/**
	 * This method check if a position in matrix is out of bound or not.
	 * @param <b>matrix</b> the full matrix
	 * @param <b>col</b> the column expressed by int
	 * @param <b>row</b> the row expressed by int
	 * @return <b>true</b> if the (col,row) position is inside the matrix, <b>false</b> otherwise
	 */
	public static boolean isInside(DefaultCell[][] matrix, int col, int row) {

		if ((col < 0) || (row < 0)) {
			return false;
		}

		if ((row >= matrix[0].length) || (col >= matrix.length)) {
			return false;
		}

		return true;
	}

	/**
	 * Clone the matrix in input in another one without refs on memory
	 * @param <b>matrix</b> the matrix to clone
	 * @return the cloned matrix.
	 * @throws CloneNotSupportedException
	 */
	public static DefaultCell[][] cloneMaps(DefaultCell[][] matrix) throws CloneNotSupportedException {

		DefaultCell[][] tmp = new DefaultCell[matrix.length][matrix[0].length];

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				tmp[i][j] = matrix[i][j].clone();
			}
		}

		return tmp;
	}

	/**
	 * Checks if the given coordinates are inside a grid with the given sizes.
	 *
	 * @param sizes the sizes of each dimension
	 * @param coords the coordinates to check
	 * @return {@code true} if all {@code coords[i]} are in {@code [0, sizes[i])}
	 */
	public static boolean isInside(int[] sizes, int[] coords) {
		if (coords.length != sizes.length) return false;
		for (int i = 0; i < coords.length; i++) {
			if (coords[i] < 0 || coords[i] >= sizes[i]) return false;
		}
		return true;
	}

	/**
	 * Clones a {@link CellGrid} by deep-copying all cells.
	 *
	 * @param grid the grid to clone
	 * @return a deep copy of the grid
	 * @throws CloneNotSupportedException if any cell cannot be cloned
	 * @throws UnsupportedOperationException if the grid type is unknown
	 */
	public static CellGrid cloneGrid(CellGrid grid) throws CloneNotSupportedException {
		if (grid instanceof CellGrid2D cg2d) {
			return new CellGrid2D(cloneMaps(cg2d.asMatrix()));
		}
		if (grid instanceof CellGridFlat cgf) {
			CellGridFlat copy = new CellGridFlat(cgf.dimensions());
			for (int[] coords : cgf.allCoordinates()) {
				DefaultCell orig = cgf.get(coords);
				copy.set(coords, orig.clone());
			}
			return copy;
		}
		throw new UnsupportedOperationException(
				"Cannot clone grid of type: " + grid.getClass().getName());
	}

}
