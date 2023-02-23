package it.carmelolg.jcal.utils;

import it.carmelolg.jcal.model.DefaultCell;

/**
 * @author Carmelo La Gamba
 * © 2023 is licensed under CC BY-NC-SA 4.0 
 */
public class Utils {
	
	/**
	 * This method check if a position in matrix is out of bound or not.
	 * @param <b>matrix</b> the full matrix
	 * @param <b>col</b> the column expressed by int
	 * @param <b>row</b> the row expressed by int
	 * @return <b>true</b> if the (col,row) position is inside the matrix, <b>false</b> otherwise
	 */
	public static boolean isInside(DefaultCell[][] matrix, int col, int row) {

		if ((col < 0) || (row < 0)) {
			return false;
		}

		if ((col >= matrix[0].length) || (row >= matrix.length)) {
			return false;
		}

		return true;
	}

	/**
	 * Clone the matrix in input in another one without refs on memory
	 * @param <b>matrix</b> the matrix to clone
	 * @return the cloned matrix.
	 * @throws CloneNotSupportedException
	 */
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
