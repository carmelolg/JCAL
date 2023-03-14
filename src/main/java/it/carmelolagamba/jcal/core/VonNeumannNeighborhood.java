package it.carmelolagamba.jcal.core;

import java.util.ArrayList;
import java.util.List;

import it.carmelolagamba.jcal.model.DefaultCell;
import it.carmelolagamba.jcal.utils.Utils;

/**
 * 
 * This is the Von Neumann neighborhood implementation
 * 
 * @author Carmelo La Gamba
 * © 2023 is licensed under CC BY-NC-SA 4.0 
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
