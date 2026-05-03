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

	/** Creates a 2D grid from a matrix. */
	public CellGrid(Cell[][] matrix) {
		this.dims = new GridDimensions(matrix.length, matrix[0].length);
		this.strides = dims.computeStrides();
		this.cells = new Cell[dims.getTotalCells()];
		int cols = matrix[0].length;
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

	/** Returns {@code true} if this grid has exactly 2 dimensions. */
	public boolean is2D() {
		return dims.getDimensionCount() == 2;
	}

	/** Returns the underlying flat cell array. */
	public Cell[] getCells() {
		return cells;
	}
}
