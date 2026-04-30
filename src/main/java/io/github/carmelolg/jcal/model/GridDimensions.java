package io.github.carmelolg.jcal.model;

/**
 * Immutable description of an n-dimensional grid (2 to 4 dimensions).
 *
 * <p>Supports 2D, 3D, and 4D grids. Provides size, stride, and coordinate utilities.
 *
 * @author Carmelo La Gamba
 */
public final class GridDimensions {

	private final int[] sizes;

	/**
	 * Creates a GridDimensions with the given sizes.
	 *
	 * @param sizes the size of each dimension (2 to 4 values, all &gt; 0)
	 * @throws IllegalArgumentException if sizes.length is not between 2 and 4, or any size &lt;= 0
	 */
	public GridDimensions(int... sizes) {
		if (sizes.length < 2 || sizes.length > 4) {
			throw new IllegalArgumentException(
					"GridDimensions supports 2 to 4 dimensions, got: " + sizes.length);
		}
		for (int s : sizes) {
			if (s <= 0) throw new IllegalArgumentException(
					"All dimension sizes must be > 0, got: " + s);
		}
		this.sizes = sizes.clone();
	}

	/**
	 * Returns the number of dimensions.
	 *
	 * @return the dimension count
	 */
	public int getDimensionCount() {
		return sizes.length;
	}

	/**
	 * Returns a copy of the sizes array.
	 *
	 * @return defensive copy of sizes
	 */
	public int[] getSizes() {
		return sizes.clone();
	}

	/**
	 * Returns the size of dimension {@code dim}.
	 *
	 * @param dim the dimension index
	 * @return the size of that dimension
	 */
	public int getSize(int dim) {
		return sizes[dim];
	}

	/**
	 * Returns total number of cells (product of all sizes).
	 *
	 * @return the total cell count
	 */
	public int getTotalCells() {
		int total = 1;
		for (int s : sizes) total *= s;
		return total;
	}

	/**
	 * Computes row-major strides.
	 *
	 * @return strides array where {@code strides[i]} is the step in the flat array
	 *         to advance dimension {@code i} by 1
	 */
	public int[] computeStrides() {
		int n = sizes.length;
		int[] strides = new int[n];
		strides[n - 1] = 1;
		for (int i = n - 2; i >= 0; i--) {
			strides[i] = strides[i + 1] * sizes[i + 1];
		}
		return strides;
	}
}
