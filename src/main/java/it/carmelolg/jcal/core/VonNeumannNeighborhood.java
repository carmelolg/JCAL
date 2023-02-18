package it.carmelolg.jcal.core;

import java.util.ArrayList;
import java.util.List;

import it.carmelolg.jcal.model.DefaultCell;
import it.carmelolg.jcal.utils.Utils;

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