package io.github.carmelolg.jcal.neighborhood;

import java.util.ArrayList;
import java.util.List;

import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.utils.Utils;

/**
 * Moore neighborhood: returns the 8 cells surrounding the given cell
 * (orthogonal + diagonal).
 *
 * <p>This is the most common neighborhood choice for classic automata such as
 * Conway's Game of Life.
 *
 * <p>Cells on the edge of the grid simply have fewer neighbours (no wrapping).
 *
 * @author Carmelo La Gamba
 * @see VonNeumannNeighborhood
 * @see Neighborhood
 */
public class MooreNeighborhood extends Neighborhood {

	@Override
	public List<Cell> getNeighbors(CellGrid grid, int[] coords) {
		if (!grid.is2D()) throw new UnsupportedOperationException(
				"Moore neighborhood only supports 2D grids. For n-dimensional support, use Moore3DNeighborhood or Moore4DNeighborhood.");
		int[] sizes = grid.dimensions().sizes();
		int i = coords[0], j = coords[1];
		List<Cell> neighbors = new ArrayList<>();
		for (int k = i - 1; k <= i + 1; k++)
			for (int l = j - 1; l <= j + 1; l++)
				if (k >= 0 && k < sizes[0] && l >= 0 && l < sizes[1] && !(k == i && l == j))
					neighbors.add(grid.get(k, l));
		return neighbors;
	}
}
