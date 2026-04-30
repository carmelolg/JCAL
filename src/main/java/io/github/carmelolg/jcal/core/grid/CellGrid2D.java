package io.github.carmelolg.jcal.core.grid;

import io.github.carmelolg.jcal.model.DefaultCell;
import io.github.carmelolg.jcal.model.GridDimensions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 2D grid backed by a {@link DefaultCell}{@code [][]} matrix.
 *
 * <p>The {@link #asMatrix()} method returns the underlying matrix directly (not a copy)
 * for backward compatibility with code that modifies cells in-place.
 *
 * @author Carmelo La Gamba
 */
public class CellGrid2D implements CellGrid {

	private DefaultCell[][] matrix;
	private GridDimensions dims;
	private volatile List<int[]> coordCache;

	/**
	 * Creates a CellGrid2D wrapping the given matrix.
	 *
	 * @param matrix the 2D cell matrix (not copied)
	 */
	public CellGrid2D(DefaultCell[][] matrix) {
		this.matrix = matrix;
		this.dims = new GridDimensions(matrix.length, matrix[0].length);
	}

	/** {@inheritDoc} */
	@Override
	public DefaultCell get(int... coords) {
		return matrix[coords[0]][coords[1]];
	}

	/** {@inheritDoc} */
	@Override
	public void set(int[] coords, DefaultCell cell) {
		matrix[coords[0]][coords[1]] = cell;
	}

	/** {@inheritDoc} */
	@Override
	public GridDimensions dimensions() {
		return dims;
	}

	/** {@inheritDoc} */
	@Override
	public List<int[]> allCoordinates() {
		if (coordCache == null) {
			synchronized (this) {
				if (coordCache == null) {
					List<int[]> coords = new ArrayList<>();
					for (int i = 0; i < matrix.length; i++)
						for (int j = 0; j < matrix[0].length; j++)
							coords.add(new int[]{i, j});
					coordCache = Collections.unmodifiableList(coords);
				}
			}
		}
		return coordCache;
	}

	/**
	 * Returns the underlying matrix directly (not a copy).
	 * Critical for backward compatibility and parallel in-place writes.
	 *
	 * @return the raw {@link DefaultCell}{@code [][]}
	 */
	public DefaultCell[][] asMatrix() {
		return matrix;
	}

	/**
	 * Replaces the underlying matrix and updates dimensions.
	 *
	 * @param matrix the new matrix
	 */
	public void setMatrix(DefaultCell[][] matrix) {
		this.matrix = matrix;
		this.dims = new GridDimensions(matrix.length, matrix[0].length);
		this.coordCache = null;
	}
}
