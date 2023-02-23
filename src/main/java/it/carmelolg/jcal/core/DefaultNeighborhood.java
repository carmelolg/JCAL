package it.carmelolg.jcal.core;

import java.util.List;

import it.carmelolg.jcal.model.DefaultCell;

/**
 * @author Carmelo La Gamba
 * © 2023 is licensed under CC BY-NC-SA 4.0 
 */
public abstract class DefaultNeighborhood {
	
	/**
	 * Return the neighbors of the cell in position (i,j)
	 * @param matrix the full matrix
	 * @param <b>i</b> the row
	 * @param <b>j</b> the column
	 * @return a list of {@link DefaultCell} that represent the neighbors of the (i,j) cell.
	 */
	public abstract List<DefaultCell> getNeighbors(DefaultCell[][] matrix, int i, int j);
}
