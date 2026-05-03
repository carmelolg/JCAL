package io.github.carmelolg.jcal.neighborhood;

import java.util.ArrayList;
import java.util.List;

import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.utils.Utils;

/**
 * Von Neumann neighborhood: returns the 4 orthogonally adjacent cells
 * (up, down, left, right) of the given cell.
 *
 * <p>Use this neighborhood for diffusion-based models or whenever diagonal
 * interactions are not desired (e.g. lava flow, heat propagation, etc.).
 *
 * <p>Cells on the edge of the grid simply have fewer neighbours (no wrapping).
 *
 * @author Carmelo La Gamba
 * @see MooreNeighborhood
 * @see Neighborhood
 */
public class VonNeumannNeighborhood extends Neighborhood {

	@Override
	public List<Cell> getNeighbors(CellGrid grid, int[] coords) {
		if (!grid.is2D()) throw new UnsupportedOperationException(
				"Von Neumann neighborhood only supports 2D grids. For n-dimensional support, use VonNeumann3DNeighborhood or VonNeumann4DNeighborhood.");
		int[] sizes = grid.dimensions().sizes();
		int i = coords[0], j = coords[1];
		List<Cell> neighbors = new ArrayList<>();
		for (int k = i - 1; k <= i + 1; k++)
			if (k >= 0 && k < sizes[0] && k != i) neighbors.add(grid.get(k, j));
		for (int l = j - 1; l <= j + 1; l++)
			if (l >= 0 && l < sizes[1] && l != j) neighbors.add(grid.get(i, l));
		return neighbors;
	}
}
