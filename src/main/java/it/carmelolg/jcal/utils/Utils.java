package it.carmelolg.jcal.utils;

import it.carmelolg.jcal.model.DefaultCell;

public class Utils {
	
	public static boolean isInside(DefaultCell[][] matrix, int col, int row) {

		if ((col < 0) || (row < 0)) {
			return false;
		}

		if ((col >= matrix[0].length) || (row >= matrix.length)) {
			return false;
		}

		return true;
	}

	public static DefaultCell[][] cloneMaps(DefaultCell[][] matrix) throws CloneNotSupportedException {

		DefaultCell[][] tmp = new DefaultCell[matrix.length][matrix[0].length];

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				tmp[i][j] = matrix[i][j].clone();
			}
		}

		return tmp;
	}

}
