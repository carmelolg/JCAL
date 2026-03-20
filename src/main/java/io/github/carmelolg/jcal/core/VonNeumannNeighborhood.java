package io.github.carmelolg.jcal.core;

import java.util.ArrayList;
import java.util.List;

import io.github.carmelolg.jcal.model.DefaultCell;
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
 * @see DefaultNeighborhood
 */
public class VonNeumannNeighborhood extends DefaultNeighborhood {

	@Override
	public List<DefaultCell> getNeighbors(DefaultCell[][] matrix, int i, int j) {
		List<DefaultCell> neighbors = new ArrayList<DefaultCell>();
		
		for (int k = i - 1; k <= i + 1; k++) {
			if (Utils.isInside(matrix, k, j) && i != k) {
				neighbors.add(matrix[k][j]);
			}
		}

		for (int l = j - 1; l <= j + 1; l++) {
			if (Utils.isInside(matrix, i, l) && l != j) {
				neighbors.add(matrix[i][l]);
			}
		}

		return neighbors;
	}

}
