package it.carmelolg.jcal;

import java.util.Arrays;
import java.util.List;

import it.carmelolg.jcal.model.DefaultCell;
import it.carmelolg.jcal.model.DefaultNeighborhood;
import it.carmelolg.jcal.utils.Utils;

public class MorraNeighborhood extends DefaultNeighborhood {

	@Override
	public List<DefaultCell> getNeighbors(DefaultCell[][] matrix, int i, int j) {
		if (Utils.isInside(matrix, i, j + 1)) {
			return Arrays.asList(matrix[i][j + 1]);
		} else {
			return Arrays.asList(matrix[i][0]);
		}
	}

}
