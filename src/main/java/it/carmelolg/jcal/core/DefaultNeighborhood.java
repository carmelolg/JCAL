package it.carmelolg.jcal.core;

import java.util.List;

import it.carmelolg.jcal.model.DefaultCell;

public abstract class DefaultNeighborhood {
	public abstract List<DefaultCell> getNeighbors(DefaultCell[][] matrix, int i, int j);
}
