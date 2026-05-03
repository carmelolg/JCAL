package io.github.carmelolg.jcal.neighborhood;

import java.util.ArrayList;
import java.util.List;
import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.utils.Utils;

/**
 * Moore neighborhood for 3D grids: returns all 26 surrounding cells
 * (3^3 - 1 = 26, all cells in the 3x3x3 cube except the center).
 *
 * @author Carmelo La Gamba
 * @see VonNeumann3DNeighborhood
 * @see Neighborhood
 */
public class Moore3DNeighborhood extends Neighborhood implements NDCapable {

	/** {@inheritDoc} */
	@Override
	public List<Cell> getNeighbors(CellGrid grid, int[] coords) {
		List<Cell> neighbors = new ArrayList<>();
		int[] sizes = grid.dimensions().sizes();
		int x = coords[0], y = coords[1], z = coords[2];
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				for (int dz = -1; dz <= 1; dz++) {
					if (dx == 0 && dy == 0 && dz == 0) continue;
					int[] nc = {x + dx, y + dy, z + dz};
					if (Utils.isInside(sizes, nc)) neighbors.add(grid.get(nc));
				}
			}
		}
		return neighbors;
	}
}
