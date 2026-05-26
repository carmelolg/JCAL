package io.github.carmelolg.jcal.neighborhood;

import java.util.List;

import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.Cell;

/**
 * Abstract base class for neighborhood strategies in all dimensions.
 *
 * <p>A neighborhood determines which cells are passed as "neighbours" to
 * {@link CellularAutomataRule#transition(io.github.carmelolg.jcal.grid.Cell, java.util.List)}
 * when evolving the automaton.
 *
 * <p>JCAL ships six built-in implementations:
 * <ul>
 *   <li>{@link MooreNeighborhood} - 2D: 8 surrounding cells</li>
 *   <li>{@link VonNeumannNeighborhood} - 2D: 4 orthogonal cells</li>
 *   <li>{@link Moore3DNeighborhood} - 3D: 26 surrounding cells (implements {@link NDCapable})</li>
 *   <li>{@link VonNeumann3DNeighborhood} - 3D: 6 orthogonal cells (implements {@link NDCapable})</li>
 *   <li>{@link Moore4DNeighborhood} - 4D: 80 surrounding cells (implements {@link NDCapable})</li>
 *   <li>{@link VonNeumann4DNeighborhood} - 4D: 8 orthogonal cells (implements {@link NDCapable})</li>
 * </ul>
 *
 * <p><b>Custom neighborhood example (2D):</b>
 * <pre>{@code
 * public class DiagonalOnlyNeighborhood extends Neighborhood {
 *     public List<Cell> getNeighbors(CellGrid grid, int[] coords) {
 *         List<Cell> result = new ArrayList<>();
 *         int[] sizes = grid.dimensions().sizes();
 *         int i = coords[0], j = coords[1];
 *         int[][] diagonals = {{-1,-1},{-1,1},{1,-1},{1,1}};
 *         for (int[] d : diagonals) {
 *             int[] nc = {i + d[0], j + d[1]};
 *             if (Utils.isInside(sizes, nc))
 *                 result.add(grid.get(nc));
 *         }
 *         return result;
 *     }
 * }
 * }</pre>
 *
 * <p><b>Custom neighborhood example (3D/4D):</b>
 * <pre>{@code
 * public class Custom3DNeighborhood extends Neighborhood implements NDCapable {
 *     public List<Cell> getNeighbors(CellGrid grid, int[] coords) {
 *         // Custom logic for 3D+ grids
 *     }
 * }
 * }</pre>
 *
 * @author Carmelo La Gamba
 * @see MooreNeighborhood
 * @see VonNeumannNeighborhood
 * @see NDCapable
 */
public abstract class Neighborhood {

	/**
	 * Returns the neighbors of the cell at the given coordinates in the given grid.
	 *
	 * <p>This is the primary interface and must be implemented by all subclasses.
	 * It works for both 2D and higher-dimensional grids.
	 *
	 * <p>For n-dimensional support, subclasses should also implement {@link NDCapable}
	 * to signal that they can handle grids with dimensions &gt; 2.
	 *
	 * @param grid the grid containing the cells (2D, 3D, 4D, etc.)
	 * @param coords the coordinates of the target cell
	 * @return list of neighboring cells
	 */
	public abstract List<Cell> getNeighbors(CellGrid grid, int[] coords);
}
