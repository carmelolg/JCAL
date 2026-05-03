package io.github.carmelolg.jcal.neighborhood;

import java.util.ArrayList;
import java.util.List;
import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.utils.Utils;

/**
 * Von Neumann neighborhood for 4D grids: returns the 8 orthogonally adjacent cells.
 *
 * @author Carmelo La Gamba
 * @see Moore4DNeighborhood
 * @see Neighborhood
 */
public class VonNeumann4DNeighborhood extends Neighborhood implements NDCapable {

	/** {@inheritDoc} */
	@Override
	public List<Cell> getNeighbors(CellGrid grid, int[] coords) {
		List<Cell> neighbors = new ArrayList<>();
		int[] sizes = grid.dimensions().sizes();
		int n = coords.length;
		for (int d = 0; d < n; d++) {
			for (int delta : new int[]{-1, 1}) {
				int[] nc = coords.clone();
				nc[d] += delta;
				if (Utils.isInside(sizes, nc)) neighbors.add(grid.get(nc));
			}
		}
		return neighbors;
	}
}
