package it.carmelolagamba.jcal.core;

import it.carmelolagamba.jcal.configuration.CellularAutomataConfiguration;
import it.carmelolagamba.jcal.model.DefaultCell;
import it.carmelolagamba.jcal.model.NeighborhoodType;
import it.carmelolagamba.jcal.utils.Utils;

/**
 * This class represent the Cellular Automata object. A Cellular Automata is a
 * quadruple like <Zd,S,X,o>
 * 
 * <b>Zd</b> is a set of cells, a d-dimension matrix of cells
 * 
 * <b>S</b> is a set of status where the single cell can be in (contained in
 * {@link DefaultCell})
 * 
 * <b>X</b> is a set of cell's neighbors, calculated by
 * {@link DefaultNeighborhood} class
 * 
 * <b>o</b> is the transaction function implemented on
 * {@link CellularAutomataExecutor}
 * 
 * @author Carmelo La Gamba
 * 
 *         © 2023 is licensed under CC BY-NC-SA 4.0
 */
public class CellularAutomata {

	protected DefaultCell[][] map;
	protected DefaultCell[][] utilsMap;
	protected DefaultNeighborhood neighborhood;
	protected CellularAutomataConfiguration config;

	public CellularAutomata() {
	}
	
	/**
	 * Build the object passing directly the configuration
	 * @param config an {@link CellularAutomataConfiguration} instance
	 * @throws Exception if something is wrong during the configuration
	 */
	public CellularAutomata(CellularAutomataConfiguration config) throws Exception {
		this.init(config);
	}

	/**
	 * Initialize the cellular automata with the properly configurations. <b>This
	 * step is mandatory for using the library</b>
	 * 
	 * @param _config the {@link CellularAutomataConfiguration} object
	 * @throws Exception if there's some exception during the cells cloning
	 */
	public void init(CellularAutomataConfiguration _config) throws Exception {
		config = _config;

		/* Step 1, check if CA could be runned and it's consistent */
		check();

		/* Step 2, initalize all matrix */
		map = new DefaultCell[config.getWidth()][config.getHeight()];

		for (int i = 0; i < config.getWidth(); i++) {
			for (int j = 0; j < config.getHeight(); j++) {
				map[i][j] = new DefaultCell(config.getStatusList().stream().findFirst().get(), i, j);
			}
		}

		if(config.getInitalState() != null && config.getInitalState().size() > 0) {
			for (DefaultCell settedCell : config.getInitalState()) {
				map[settedCell.col][settedCell.row] = settedCell;
			}			
		}

		/* Step 3, define the neighborhood */
		if (config.getNeighborhood() != null) {
			neighborhood = config.getNeighborhood();
		} else {
			neighborhood = config.getNeighborhoodType().equals(NeighborhoodType.MOORE) ? new MooreNeighborhood()
					: new VonNeumannNeighborhood();
		}

		/*
		 * Step 4, the last, initalize the cloned matrix in order to perform the
		 * transaction function
		 */
		try {
			utilsMap = Utils.cloneMaps(map);
		} catch (CloneNotSupportedException e) {
			throw new Exception("It's not possible clone the maps. Please contact the lib maintainer");
		}

	}

	/**
	 * Throws an exception if the default rules are violated.
	 * 
	 * @throws Exception
	 */
	private void check() throws Exception {

		if (config.isInfinite() && config.getTotalIterations() > 0) {
			throw new Exception("It's not possibile loop infinitely with total interactions setted");
		}

		if (!config.isInfinite() && config.getTotalIterations() < 1) {
			throw new Exception("It's not possibile to run because the number of interactions is not setted");
		}

		if (config.getNeighborhoodType() == null && config.getNeighborhood() == null) {
			throw new Exception("Set the neighborhood type or implement your Neighborhood by yourself.");
		}

		if (config.getNeighborhoodType() != null && config.getNeighborhood() != null) {
			throw new Exception("You can choose only one between NeighborhoodType and Neighborhood");
		}

		if (config.getStatusList() == null) {
			throw new Exception("Set the cell's status list.");
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

	public void setConfig(CellularAutomataConfiguration config) {
		this.config = config;
	}

	public CellularAutomataConfiguration getConfig() {
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
