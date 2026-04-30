package io.github.carmelolg.jcal.core;

import java.util.List;

import io.github.carmelolg.jcal.core.grid.CellGrid;
import io.github.carmelolg.jcal.core.grid.CellGrid2D;
import io.github.carmelolg.jcal.model.DefaultCell;

/**
 * Abstract base class for neighborhood strategies.
 *
 * <p>A neighborhood determines which cells are passed as "neighbours" to
 * {@link CellularAutomataExecutor#singleRun(io.github.carmelolg.jcal.model.DefaultCell, java.util.List)}
 * when evolving the automaton.
 *
 * <p>JCAL ships two implementations:
 * <ul>
 *   <li>{@link MooreNeighborhood} - 8 surrounding cells (orthogonal + diagonal)</li>
 *   <li>{@link VonNeumannNeighborhood} - 4 orthogonal cells only</li>
 * </ul>
 *
 * <p><b>Custom neighborhood example:</b>
 * <pre>{@code
 * public class DiagonalOnlyNeighborhood extends DefaultNeighborhood {
 *     public List<DefaultCell> getNeighbors(DefaultCell[][] matrix, int i, int j) {
 *         List<DefaultCell> result = new ArrayList<>();
 *         int[][] diagonals = {{-1,-1},{-1,1},{1,-1},{1,1}};
 *         for (int[] d : diagonals) {
 *             if (Utils.isInside(matrix, i+d[0], j+d[1]))
 *                 result.add(matrix[i+d[0]][j+d[1]]);
 *         }
 *         return result;
 *     }
 * }
 * }</pre>
 *
 * @author Carmelo La Gamba
 * @see MooreNeighborhood
 * @see VonNeumannNeighborhood
 */
public abstract class DefaultNeighborhood {
	
	/**
	 * Return the neighbors of the cell in position (i,j)
	 * @param matrix the full matrix
	 * @param <b>i</b> the row
	 * @param <b>j</b> the column
	 * @return a list of {@link DefaultCell} that represent the neighbors of the (i,j) cell.
	 */
	public abstract List<DefaultCell> getNeighbors(DefaultCell[][] matrix, int i, int j);

	/**
	 * Returns the neighbors of the cell at the given coordinates in the given grid.
	 *
	 * <p>For 2D grids, delegates to {@link #getNeighbors(DefaultCell[][], int, int)}.
	 * For higher-dimensional grids, subclasses should override this method or extend
	 * {@link DefaultNeighborhoodND}.
	 *
	 * @param grid the grid containing the cells
	 * @param coords the coordinates of the target cell
	 * @return list of neighboring cells
	 * @throws UnsupportedOperationException for non-2D grids if not overridden
	 */
	public List<DefaultCell> getNeighbors(CellGrid grid, int[] coords) {
		if (grid instanceof CellGrid2D cg2d) {
			return getNeighbors(cg2d.asMatrix(), coords[0], coords[1]);
		}
		throw new UnsupportedOperationException(
				"This neighborhood only supports 2D grids. Extend DefaultNeighborhoodND for multi-dimensional support.");
	}
}
