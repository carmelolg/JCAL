package io.github.carmelolg.jcal.core.grid;

import io.github.carmelolg.jcal.model.DefaultCell;
import io.github.carmelolg.jcal.model.GridDimensions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * N-dimensional grid (3D or 4D) backed by a flat {@link DefaultCell} array with row-major indexing.
 *
 * <p>Coordinates are mapped to flat indices using strides:
 * {@code index = coords[0]*strides[0] + coords[1]*strides[1] + ... + coords[n-1]*strides[n-1]}
 *
 * @author Carmelo La Gamba
 */
public class CellGridFlat implements CellGrid {

	private final GridDimensions dims;
	private final int[] strides;
	private final DefaultCell[] cells;
	private volatile List<int[]> coordCache;

	/**
	 * Creates an empty CellGridFlat with the given dimensions.
	 *
	 * @param dims the grid dimensions
	 */
	public CellGridFlat(GridDimensions dims) {
		this.dims = dims;
		this.strides = dims.computeStrides();
		this.cells = new DefaultCell[dims.getTotalCells()];
	}

	/**
	 * Creates a CellGridFlat with a pre-existing cell array. Package-private.
	 *
	 * @param dims the grid dimensions
	 * @param cells the cell array (not copied)
	 */
	CellGridFlat(GridDimensions dims, DefaultCell[] cells) {
		this.dims = dims;
		this.strides = dims.computeStrides();
		this.cells = cells;
	}

	private int flatIndex(int[] coords) {
		int idx = 0;
		for (int i = 0; i < coords.length; i++) idx += coords[i] * strides[i];
		return idx;
	}

	/** {@inheritDoc} */
	@Override
	public DefaultCell get(int... coords) {
		return cells[flatIndex(coords)];
	}

	/** {@inheritDoc} */
	@Override
	public void set(int[] coords, DefaultCell cell) {
		cells[flatIndex(coords)] = cell;
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
					List<int[]> result = new ArrayList<>(dims.getTotalCells());
					int n = dims.getDimensionCount();
					int[] sizes = dims.getSizes();
					int[] coords = new int[n];
					for (int flat = 0; flat < dims.getTotalCells(); flat++) {
						int rem = flat;
						for (int i = n - 1; i >= 0; i--) {
							coords[i] = rem % sizes[i];
							rem /= sizes[i];
						}
						result.add(coords.clone());
					}
					coordCache = Collections.unmodifiableList(result);
				}
			}
		}
		return coordCache;
	}

	/**
	 * Returns the underlying flat cell array.
	 *
	 * @return the raw cells array
	 */
	public DefaultCell[] getCells() {
		return cells;
	}
}
