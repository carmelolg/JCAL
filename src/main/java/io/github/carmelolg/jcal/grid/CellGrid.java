package io.github.carmelolg.jcal.grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * N-dimensional grid of {@link Cell}s (2D to 4D).
 * Backed by a flat array with row-major (stride) indexing.
 *
 * @author Carmelo La Gamba
 */
public class CellGrid {

	private final GridDimensions dims;
	private final int[] strides;
	private final Cell[] cells;
	private volatile List<int[]> coordCache;
	private volatile List<List<int[]>> rowCoordCache;

	/** Creates a 2D grid from a matrix. Throws {@link IllegalArgumentException} if the matrix is null, empty, or jagged. */
	public CellGrid(Cell[][] matrix) {
		if (matrix == null || matrix.length == 0)
			throw new IllegalArgumentException("matrix must not be null or empty");
		int cols = matrix[0].length;
		if (cols == 0)
			throw new IllegalArgumentException("matrix rows must not be empty");
		for (int i = 1; i < matrix.length; i++)
			if (matrix[i].length != cols)
				throw new IllegalArgumentException(
					"jagged matrix: row 0 has " + cols + " columns but row " + i + " has " + matrix[i].length);
		this.dims = new GridDimensions(matrix.length, cols);
		this.strides = dims.computeStrides();
		this.cells = new Cell[dims.getTotalCells()];
		for (int i = 0; i < matrix.length; i++)
			System.arraycopy(matrix[i], 0, cells, i * cols, cols);
	}

	/** Creates an empty nD grid from dimensions. */
	public CellGrid(GridDimensions dims) {
		this.dims = dims;
		this.strides = dims.computeStrides();
		this.cells = new Cell[dims.getTotalCells()];
	}

	private int flatIndex(int[] coords) {
		int idx = 0;
		for (int i = 0; i < coords.length; i++) idx += coords[i] * strides[i];
		return idx;
	}

	public Cell get(int... coords) {
		return cells[flatIndex(coords)];
	}

	/**
	 * Returns the cell at {@code (col, row)} in a 2D grid.
	 * Avoids the varargs array allocation of {@link #get(int...)} on hot paths.
	 *
	 * @param col the column index (first dimension)
	 * @param row the row index (second dimension)
	 * @return the cell at the given position
	 */
	public Cell get(int col, int row) {
		return cells[col * strides[0] + row * strides[1]];
	}

	public void set(int[] coords, Cell cell) {
		cells[flatIndex(coords)] = cell;
	}

	public GridDimensions dimensions() {
		return dims;
	}

	public List<int[]> allCoordinates() {
		if (coordCache == null) {
			synchronized (this) {
				if (coordCache == null) coordCache = generateCoordinates();
			}
		}
		return coordCache;
	}

	private List<int[]> generateCoordinates() {
		List<int[]> result = new ArrayList<>(dims.getTotalCells());
		int n = dims.getDimensionCount();
		int[] sizes = dims.sizes();
		int[] coords = new int[n];
		for (int flat = 0; flat < dims.getTotalCells(); flat++) {
			int rem = flat;
			for (int i = n - 1; i >= 0; i--) {
				coords[i] = rem % sizes[i];
				rem /= sizes[i];
			}
			result.add(coords.clone());
		}
		return Collections.unmodifiableList(result);
	}

	/**
	 * Returns coordinates for all cells in the specified row (first dimension slice).
	 * Results are cached per row using double-checked locking.
	 * 
	 * @param row the row index (coordinate[0])
	 * @return list of coordinates where coordinate[0] equals {@code row}
	 */
	public List<int[]> coordinatesForRow(int row) {
		if (rowCoordCache == null) {
			synchronized (this) {
				if (rowCoordCache == null) {
					int numRows = dims.sizes()[0];
					List<List<int[]>> cache = new ArrayList<>(numRows);
					for (int r = 0; r < numRows; r++) cache.add(null);
					rowCoordCache = cache;
				}
			}
		}
		List<int[]> cached = rowCoordCache.get(row);
		if (cached == null) {
			synchronized (this) {
				cached = rowCoordCache.get(row);
				if (cached == null) {
					cached = generateCoordinatesForRow(row);
					rowCoordCache.set(row, cached);
				}
			}
		}
		return cached;
	}

	private List<int[]> generateCoordinatesForRow(int row) {
		int n = dims.getDimensionCount();
		int[] sizes = dims.sizes();
		int cellsPerRow = dims.getTotalCells() / sizes[0];
		List<int[]> result = new ArrayList<>(cellsPerRow);
		int[] coords = new int[n];
		coords[0] = row;
		for (int flat = row * cellsPerRow; flat < (row + 1) * cellsPerRow; flat++) {
			int rem = flat;
			for (int i = n - 1; i >= 1; i--) {
				coords[i] = rem % sizes[i];
				rem /= sizes[i];
			}
			result.add(coords.clone());
		}
		return Collections.unmodifiableList(result);
	}

	/** Returns {@code true} if this grid has exactly 2 dimensions. */
	public boolean is2D() {
		return dims.getDimensionCount() == 2;
	}
}
