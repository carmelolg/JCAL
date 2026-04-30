package io.github.carmelolg.jcal.core;

import java.util.ArrayList;
import java.util.List;
import io.github.carmelolg.jcal.core.grid.CellGrid;
import io.github.carmelolg.jcal.model.DefaultCell;
import io.github.carmelolg.jcal.utils.Utils;

/**
 * Von Neumann neighborhood for 4D grids: returns the 8 orthogonally adjacent cells.
 *
 * @author Carmelo La Gamba
 * @see Moore4DNeighborhood
 * @see DefaultNeighborhoodND
 */
public class VonNeumann4DNeighborhood extends DefaultNeighborhoodND {

	/** {@inheritDoc} */
	@Override
	public List<DefaultCell> getNeighbors(CellGrid grid, int[] coords) {
		List<DefaultCell> neighbors = new ArrayList<>();
		int[] sizes = grid.dimensions().getSizes();
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
