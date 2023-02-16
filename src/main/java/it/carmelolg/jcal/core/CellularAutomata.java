package it.carmelolg.jcal.core;

import it.carmelolg.jcal.configuration.EnvironmentConfiguration;
import it.carmelolg.jcal.model.DefaultCell;
import it.carmelolg.jcal.model.DefaultNeighborhood;
import it.carmelolg.jcal.model.MooreNeighborhood;
import it.carmelolg.jcal.model.Neighborhood;
import it.carmelolg.jcal.model.VonNeumannNeighborhood;
import it.carmelolg.jcal.utils.Utils;

public class CellularAutomata {

	protected DefaultCell[][] map;
	protected DefaultCell[][] utilsMap;
	protected DefaultNeighborhood neighborhood;
	protected EnvironmentConfiguration config;

	public CellularAutomata() {
	}

	public void init(EnvironmentConfiguration _config) throws Exception {
		config = _config;

		// Init map
		map = new DefaultCell[config.getWidth()][config.getHeight()];

		// Init cells
		for (int i = 0; i < config.getWidth(); i++) {
			for (int j = 0; j < config.getHeight(); j++) {
				map[i][j] = new DefaultCell(config.getStatusList().stream().findFirst().get(), i, j);
			}
		}

		// Init initial state
		for (DefaultCell settedCell : config.getInitalState()) {
			map[settedCell.col][settedCell.row] = settedCell;
		}

		// Define the neighborhood
		if (config.getNeighborhood() != null) {
			neighborhood = config.getNeighborhood();
		} else {
			neighborhood = config.getNeighborhoodType().equals(Neighborhood.MOORE) ? new MooreNeighborhood()
					: new VonNeumannNeighborhood();
		}

		// Clone map into UtilsMap
		try {
			utilsMap = Utils.cloneMaps(map);
		} catch (CloneNotSupportedException e) {
			throw new Exception("It's not possible clone the maps. Please contact the lib maintainer");
		}

	}

	public void check() throws Exception {

		if (config.isInfinite() && config.getTotalIterations() > 0) {
			throw new Exception("It's not possibile loop infinitely with total interactions setted");
		}

		if (!config.isInfinite() && config.getTotalIterations() < 1) {
			throw new Exception("It's not possibile to run because the number of interactions is not setted");
		}

		if (config.getNeighborhoodType() == null && config.getNeighborhood() == null) {
			throw new Exception("Set the neighborhood type or implement your Neighborhood by yourself.");
		}

		if (config.getStatusList() == null) {
			throw new Exception("Set the cell's status list.");
		}

		if (config.getNeighborhoodType() != null && config.getNeighborhood() != null) {
			throw new Exception("You can choose only one between NeighborhoodType and Neighborhood");
		}

	}

	public DefaultCell[][] getMap() {
		return map;
	}

	public void setMap(DefaultCell[][] map) {
		this.map = map;
	}

	public DefaultCell[][] getUtilsMap() {
		return utilsMap;
	}

	public void setUtilsMap(DefaultCell[][] map) {
		this.utilsMap = map;
	}

	public DefaultNeighborhood getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(DefaultNeighborhood neighborhood) {
		this.neighborhood = neighborhood;
	}

	public void setConfig(EnvironmentConfiguration config) {
		this.config = config;
	}

	public EnvironmentConfiguration getConfig() {
		return config;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				builder.append(map[i][j].currentStatus + " ");
			}
			builder.append("\n");
		}
		return builder.toString();
	}
}
