package io.github.carmelolg.jcal.core;

import io.github.carmelolg.jcal.configuration.CellularAutomataConfiguration;
import io.github.carmelolg.jcal.core.grid.CellGrid;
import io.github.carmelolg.jcal.core.grid.CellGrid2D;
import io.github.carmelolg.jcal.core.grid.CellGridFlat;
import io.github.carmelolg.jcal.model.DefaultCell;
import io.github.carmelolg.jcal.model.GridDimensions;
import io.github.carmelolg.jcal.model.NeighborhoodType;
import io.github.carmelolg.jcal.utils.Utils;

/**
 * The central class representing a Cellular Automata (CA) instance.
 *
 * <p>Supports 2D, 3D, and 4D grids. For 2D, the grid is backed by a
 * {@link DefaultCell}{@code [][]} matrix for backward compatibility.
 * For 3D/4D, a flat array is used with stride-based indexing.
 *
 * <p><b>Typical usage (2D):</b>
 * <pre>{@code
 * CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
 *     .setWidth(10).setHeight(10)
 *     .setTotalIterations(5)
 *     .setDefaultStatus(dead)
 *     .setNeighborhoodType(NeighborhoodType.MOORE)
 *     .build();
 * CellularAutomata ca = new CellularAutomata(config);
 * ca = new MyExecutor().run(ca);
 * System.out.println(ca);
 * }</pre>
 *
 * @author Carmelo La Gamba
 * @see CellularAutomataExecutor
 * @see io.github.carmelolg.jcal.configuration.CellularAutomataConfiguration
 * @see DefaultNeighborhood
 * © 2023 is licensed under CC BY-NC-SA 4.0
 */
public class CellularAutomata {

	/** The active grid. */
	private CellGrid grid;
	/** The double-buffer grid used during transitions. */
	private CellGrid utilsGrid;
	/** The neighborhood strategy. */
	private DefaultNeighborhood neighborhood;
	/** The configuration. */
	private CellularAutomataConfiguration config;

	/** No-arg constructor. */
	public CellularAutomata() {}

	/**
	 * Build the object passing directly the configuration.
	 *
	 * @param config an {@link CellularAutomataConfiguration} instance
	 * @throws Exception if something is wrong during the configuration
	 */
	public CellularAutomata(CellularAutomataConfiguration config) throws Exception {
		this.init(config);
	}

	/**
	 * Initialize the cellular automata with the given configuration.
	 *
	 * @param _config the {@link CellularAutomataConfiguration} object
	 * @throws Exception if there's some exception during initialization
	 */
	public void init(CellularAutomataConfiguration _config) throws Exception {
		config = _config;
		check();

		int[] dims = config.getDimensions();
		GridDimensions gridDims = new GridDimensions(dims);
		int dimCount = dims.length;

		if (dimCount == 2) {
			DefaultCell[][] matrix = new DefaultCell[dims[0]][dims[1]];
			for (int i = 0; i < dims[0]; i++)
				for (int j = 0; j < dims[1]; j++)
					matrix[i][j] = new DefaultCell(config.getDefaultStatus(), i, j);
			grid = new CellGrid2D(matrix);
		} else {
			CellGridFlat flatGrid = new CellGridFlat(gridDims);
			for (int[] coords : flatGrid.allCoordinates())
				flatGrid.set(coords, new DefaultCell(config.getDefaultStatus(), coords));
			grid = flatGrid;
		}

		if (config.getInitalState() != null && !config.getInitalState().isEmpty()) {
			for (DefaultCell settedCell : config.getInitalState()) {
				grid.set(settedCell.getCoordinates(), settedCell);
			}
		}

		if (config.getNeighborhood() != null) {
			neighborhood = config.getNeighborhood();
		} else {
			neighborhood = resolveNeighborhood(config.getNeighborhoodType(), dimCount);
		}

		try {
			utilsGrid = Utils.cloneGrid(grid);
		} catch (CloneNotSupportedException e) {
			throw new Exception("It's not possible clone the maps. Please contact the lib maintainer");
		}
	}

	private DefaultNeighborhood resolveNeighborhood(NeighborhoodType type, int dimCount) {
		return switch (dimCount) {
			case 2 -> type == NeighborhoodType.MOORE ? new MooreNeighborhood() : new VonNeumannNeighborhood();
			case 3 -> type == NeighborhoodType.MOORE ? new Moore3DNeighborhood() : new VonNeumann3DNeighborhood();
			case 4 -> type == NeighborhoodType.MOORE ? new Moore4DNeighborhood() : new VonNeumann4DNeighborhood();
			default -> throw new IllegalArgumentException("Unsupported dimension count: " + dimCount);
		};
	}

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

		int[] dims = config.getDimensions();
		if (dims.length < 2 || dims.length > 4) {
			throw new Exception("Grid dimensions must be between 2 and 4, got: " + dims.length);
		}
		for (int d : dims) {
			if (d <= 0) throw new Exception("All grid dimension sizes must be > 0");
		}
		int dimCount = dims.length;
		if (config.getNeighborhood() != null && dimCount > 2
				&& !(config.getNeighborhood() instanceof DefaultNeighborhoodND)) {
			throw new Exception("For nD CAs (n > 2), the custom neighborhood must extend DefaultNeighborhoodND");
		}
		if (config.getInitalState() != null) {
			for (DefaultCell cell : config.getInitalState()) {
				int[] coords = cell.getCoordinates();
				if (coords.length != dimCount) {
					throw new Exception("Initial state cell coordinates must match dimension count");
				}
				for (int d = 0; d < dimCount; d++) {
					if (coords[d] < 0 || coords[d] >= dims[d]) {
						throw new Exception("Initial state cell coordinate out of bounds");
					}
				}
			}
		}
	}

	// --- Backward-compatible 2D accessors ---

	/**
	 * Returns the 2D map matrix. Only valid for 2D CAs.
	 *
	 * @return the underlying {@link DefaultCell}{@code [][]}
	 * @throws UnsupportedOperationException for nD CAs
	 */
	public DefaultCell[][] getMap() {
		if (grid instanceof CellGrid2D cg2d) return cg2d.asMatrix();
		throw new UnsupportedOperationException("Use getGrid() for nD CAs");
	}

	/**
	 * Replaces the 2D map.
	 *
	 * @param map the new matrix
	 */
	public void setMap(DefaultCell[][] map) {
		this.grid = new CellGrid2D(map);
	}

	/**
	 * Returns the 2D utils map matrix. Only valid for 2D CAs.
	 *
	 * @return the underlying {@link DefaultCell}{@code [][]}
	 * @throws UnsupportedOperationException for nD CAs
	 */
	public DefaultCell[][] getUtilsMap() {
		if (utilsGrid instanceof CellGrid2D cg2d) return cg2d.asMatrix();
		throw new UnsupportedOperationException("Use getUtilsGrid() for nD CAs");
	}

	/**
	 * Replaces the 2D utils map.
	 *
	 * @param map the new matrix
	 */
	public void setUtilsMap(DefaultCell[][] map) {
		this.utilsGrid = new CellGrid2D(map);
	}

	/**
	 * Returns the active grid.
	 *
	 * @return the {@link CellGrid}
	 */
	public CellGrid getGrid() {
		return grid;
	}

	/**
	 * Sets the active grid.
	 *
	 * @param grid the new grid
	 */
	public void setGrid(CellGrid grid) {
		this.grid = grid;
	}

	/**
	 * Returns the double-buffer grid.
	 *
	 * @return the utils {@link CellGrid}
	 */
	public CellGrid getUtilsGrid() {
		return utilsGrid;
	}

	/**
	 * Sets the double-buffer grid.
	 *
	 * @param utilsGrid the new utils grid
	 */
	public void setUtilsGrid(CellGrid utilsGrid) {
		this.utilsGrid = utilsGrid;
	}

	/**
	 * Returns the neighborhood.
	 *
	 * @return the {@link DefaultNeighborhood}
	 */
	public DefaultNeighborhood getNeighborhood() {
		return neighborhood;
	}

	/**
	 * Sets the neighborhood.
	 *
	 * @param neighborhood the new neighborhood
	 */
	public void setNeighborhood(DefaultNeighborhood neighborhood) {
		this.neighborhood = neighborhood;
	}

	/**
	 * Sets the configuration.
	 *
	 * @param config the new config
	 */
	public void setConfig(CellularAutomataConfiguration config) {
		this.config = config;
	}

	/**
	 * Returns the configuration.
	 *
	 * @return the config
	 */
	public CellularAutomataConfiguration getConfig() {
		return config;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (grid instanceof CellGrid2D cg2d) {
			DefaultCell[][] matrix = cg2d.asMatrix();
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[0].length; j++) {
					builder.append(matrix[i][j].getCurrentStatus() + " ");
				}
				builder.append("\n");
			}
		} else {
			GridDimensions gdims = grid.dimensions();
			int sliceSize = 1;
			for (int i = 1; i < gdims.getDimensionCount(); i++) sliceSize *= gdims.getSize(i);
			int count = 0;
			for (int[] coords : grid.allCoordinates()) {
				builder.append(grid.get(coords).getCurrentStatus() + " ");
				if (++count % sliceSize == 0) builder.append("\n");
			}
		}
		return builder.toString();
	}
}
