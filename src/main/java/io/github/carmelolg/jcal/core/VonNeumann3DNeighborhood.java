package io.github.carmelolg.jcal.core;

import java.util.ArrayList;
import java.util.List;
import io.github.carmelolg.jcal.core.grid.CellGrid;
import io.github.carmelolg.jcal.model.DefaultCell;
import io.github.carmelolg.jcal.utils.Utils;

/**
 * Von Neumann neighborhood for 3D grids: returns the 6 orthogonally adjacent cells
 * (up, down, left, right, front, back).
 *
 * @author Carmelo La Gamba
 * @see Moore3DNeighborhood
 * @see DefaultNeighborhoodND
 */
public class VonNeumann3DNeighborhood extends DefaultNeighborhoodND {

	/** {@inheritDoc} */
	@Override
	public List<DefaultCell> getNeighbors(CellGrid grid, int[] coords) {
		List<DefaultCell> neighbors = new ArrayList<>();
		int[] sizes = grid.dimensions().getSizes();
		int x = coords[0], y = coords[1], z = coords[2];
		int[][] offsets = {{1,0,0},{-1,0,0},{0,1,0},{0,-1,0},{0,0,1},{0,0,-1}};
		for (int[] off : offsets) {
			int[] nc = {x + off[0], y + off[1], z + off[2]};
			if (Utils.isInside(sizes, nc)) neighbors.add(grid.get(nc));
		}
		return neighbors;
	}
}
