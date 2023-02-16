package it.carmelolg.jcal.model;

import java.util.List;

public abstract class DefaultNeighborhood {
	public abstract List<DefaultCell> getNeighbors(DefaultCell[][] matrix, int i, int j);
}
