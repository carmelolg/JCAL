package io.github.carmelolg.jcal.core;

import java.util.List;
import io.github.carmelolg.jcal.core.grid.CellGrid;
import io.github.carmelolg.jcal.core.grid.CellGrid2D;
import io.github.carmelolg.jcal.model.DefaultCell;

/**
 * Abstract base class for n-dimensional (n&gt;2) neighborhood strategies.
 *
 * <p>Subclasses must implement {@link #getNeighbors(CellGrid, int[])} for
 * the multi-dimensional case. The legacy 2D method delegates to the nD method.
 *
 * @author Carmelo La Gamba
 * @see Moore3DNeighborhood
 * @see VonNeumann3DNeighborhood
 * @see Moore4DNeighborhood
 * @see VonNeumann4DNeighborhood
 */
public abstract class DefaultNeighborhoodND extends DefaultNeighborhood {

	/**
	 * Delegates to {@link #getNeighbors(CellGrid, int[])} using a 2D wrapper.
	 * {@inheritDoc}
	 */
	@Override
	public final List<DefaultCell> getNeighbors(DefaultCell[][] matrix, int i, int j) {
		CellGrid2D grid2d = new CellGrid2D(matrix);
		return getNeighbors(grid2d, new int[]{i, j});
	}

	/**
	 * Returns the neighbors of the cell at coords in the given grid.
	 *
	 * @param grid the multi-dimensional grid
	 * @param coords the coordinates of the target cell
	 * @return list of neighboring cells
	 */
	@Override
	public abstract List<DefaultCell> getNeighbors(CellGrid grid, int[] coords);
}
