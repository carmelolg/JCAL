package io.github.carmelolg.jcal.core;

import io.github.carmelolg.jcal.configuration.CellularAutomataConfiguration;
import io.github.carmelolg.jcal.model.DefaultCell;
import io.github.carmelolg.jcal.model.NeighborhoodType;
import io.github.carmelolg.jcal.utils.Utils;

/**
 * The central class representing a Cellular Automata (CA) instance.
 *
 * <p>A Cellular Automata is formally described as the quadruple {@code <Zd, S, X, σ>}:
 * <ul>
 *   <li><b>Zd</b> – a d-dimensional matrix of cells (the grid managed by this class)</li>
 *   <li><b>S</b> – the set of possible cell states, represented by {@link io.github.carmelolg.jcal.model.DefaultStatus}</li>
 *   <li><b>X</b> – the cell neighborhood, provided by {@link DefaultNeighborhood} implementations</li>
 *   <li><b>σ</b> – the transition function, implemented in a subclass of {@link CellularAutomataExecutor}</li>
 * </ul>
 *
 * <p><b>Typical usage:</b>
 * <pre>{@code
 * // 1. Define states
 * DefaultStatus dead  = new DefaultStatus("dead",  "0");
 * DefaultStatus alive = new DefaultStatus("alive", "1");
 *
 * // 2. Build configuration
 * CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
 *     .setWidth(10).setHeight(10)
 *     .setTotalIterations(5)
 *     .setDefaultStatus(dead)
 *     .setNeighborhoodType(NeighborhoodType.MOORE)
 *     .build();
 *
 * // 3. Initialize and run
 * CellularAutomata ca = new CellularAutomata(config);
 * ca = new MyExecutor().run(ca);
 * System.out.println(ca);
 * }</pre>
 *
 * <p><b>Extension points:</b>
 * <ul>
 *   <li>Subclass {@link CellularAutomataExecutor} to define the transition function.</li>
 *   <li>Subclass {@link DefaultNeighborhood} for a custom neighborhood shape.</li>
 *   <li>Subclass {@link io.github.carmelolg.jcal.model.DefaultStatus} to carry complex per-cell data.</li>
 * </ul>
 *
 * @author Carmelo La Gamba
 * @see CellularAutomataExecutor
 * @see io.github.carmelolg.jcal.configuration.CellularAutomataConfiguration
 * @see DefaultNeighborhood
 * © 2023 is licensed under CC BY-NC-SA 4.0
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
				map[i][j] = new DefaultCell(config.getDefaultStatus(), i, j);
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
		
		if (config.getDefaultStatus() == null) {
			throw new Exception("You must define the default status.");
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
