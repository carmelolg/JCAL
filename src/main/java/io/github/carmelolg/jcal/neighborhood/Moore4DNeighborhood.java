package io.github.carmelolg.jcal.neighborhood;

import java.util.ArrayList;
import java.util.List;
import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.utils.Utils;

/**
 * Moore neighborhood for 4D grids: returns all 80 surrounding cells
 * (3^4 - 1 = 80, all cells in the 3x3x3x3 hypercube except the center).
 *
 * @author Carmelo La Gamba
 * @see VonNeumann4DNeighborhood
 * @see Neighborhood
 */
public class Moore4DNeighborhood extends Neighborhood implements NDCapable {

	/** {@inheritDoc} */
	@Override
	public List<Cell> getNeighbors(CellGrid grid, int[] coords) {
		List<Cell> neighbors = new ArrayList<>();
		int[] sizes = grid.dimensions().sizes();
		int w = coords[0], x = coords[1], y = coords[2], z = coords[3];
		for (int dw = -1; dw <= 1; dw++) {
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					for (int dz = -1; dz <= 1; dz++) {
						if (dw == 0 && dx == 0 && dy == 0 && dz == 0) continue;
						int[] nc = {w + dw, x + dx, y + dy, z + dz};
						if (Utils.isInside(sizes, nc)) neighbors.add(grid.get(nc));
					}
				}
			}
		}
		return neighbors;
	}
}
