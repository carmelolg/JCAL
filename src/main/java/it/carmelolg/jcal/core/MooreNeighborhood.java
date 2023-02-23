package it.carmelolg.jcal.core;

import java.util.ArrayList;
import java.util.List;

import it.carmelolg.jcal.model.DefaultCell;
import it.carmelolg.jcal.utils.Utils;

/**
 * 
 * This is the Moore Neighborhood implementation.
 * 
 * @author Carmelo La Gamba
 * © 2023 is licensed under CC BY-NC-SA 4.0 
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
