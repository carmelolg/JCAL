package io.github.carmelolg.jcal.core;

import java.util.ArrayList;
import java.util.List;

import io.github.carmelolg.jcal.model.DefaultCell;
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
 * @see DefaultNeighborhood
 */
public class MooreNeighborhood extends DefaultNeighborhood {

	@Override
	public List<DefaultCell> getNeighbors(DefaultCell[][] matrix, int i, int j) {
		List<DefaultCell> neighbors = new ArrayList<DefaultCell>();

		for (int k = i - 1; k <= i + 1; k++) {
			for (int l = j - 1; l <= j + 1; l++) {
				if (Utils.isInside(matrix, k, l) && !(i == k && j == l)) {
					neighbors.add(matrix[k][l]);
				}
			}
		}

		return neighbors;
	}

}
