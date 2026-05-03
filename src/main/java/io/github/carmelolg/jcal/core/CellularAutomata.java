package io.github.carmelolg.jcal.core;

import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.GridDimensions;
import io.github.carmelolg.jcal.neighborhood.NeighborhoodType;
import io.github.carmelolg.jcal.neighborhood.Neighborhood;
import io.github.carmelolg.jcal.neighborhood.NDCapable;
import io.github.carmelolg.jcal.neighborhood.MooreNeighborhood;
import io.github.carmelolg.jcal.neighborhood.Moore3DNeighborhood;
import io.github.carmelolg.jcal.neighborhood.Moore4DNeighborhood;
import io.github.carmelolg.jcal.neighborhood.VonNeumannNeighborhood;
import io.github.carmelolg.jcal.neighborhood.VonNeumann3DNeighborhood;
import io.github.carmelolg.jcal.neighborhood.VonNeumann4DNeighborhood;
import io.github.carmelolg.jcal.utils.Utils;

/**
 * The central class representing a Cellular Automata (CA) instance.
 *
 * <p>Supports 2D, 3D, and 4D grids backed by a unified {@link CellGrid}.
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
 * @see io.github.carmelolg.jcal.core.CellularAutomataConfiguration
 * @see Neighborhood
 * © 2023 is licensed under CC BY-NC-SA 4.0
 */
public class CellularAutomata {

    /**
     * The active grid.
     */
    private CellGrid grid;
    /**
     * The double-buffer grid used during transitions.
     */
    private CellGrid utilsGrid;
    /**
     * The neighborhood strategy.
     */
    private Neighborhood neighborhood;
    /**
     * The configuration.
     */
    private CellularAutomataConfiguration config;

    /**
     * No-arg constructor.
     */
    public CellularAutomata() {
    }

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

        grid = new CellGrid(gridDims);
        for (int[] coords : grid.allCoordinates())
            grid.set(coords, new Cell(config.getDefaultStatus(), coords));

        if (config.getInitalState() != null && !config.getInitalState().isEmpty()) {
            for (Cell settedCell : config.getInitalState()) {
                grid.set(settedCell.getCoordinates(), settedCell);
            }
        }

        if (config.getNeighborhood() != null) {
            neighborhood = config.getNeighborhood();
        } else {
            neighborhood = resolveNeighborhood(config.getNeighborhoodType(), dims.length);
        }

        utilsGrid = Utils.cloneGrid(grid);
    }

    private Neighborhood resolveNeighborhood(NeighborhoodType type, int dimCount) {
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
                && !(config.getNeighborhood() instanceof NDCapable)) {
            throw new Exception("For n-dimensional CAs (n > 2), the custom neighborhood must implement NDCapable");
        }
        if (config.getInitalState() != null) {
            for (Cell cell : config.getInitalState()) {
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
     * @return the {@link Neighborhood}
     */
    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    /**
     * Sets the neighborhood.
     *
     * @param neighborhood the new neighborhood
     */
    public void setNeighborhood(Neighborhood neighborhood) {
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
        if (grid.is2D()) {
            int rows = grid.dimensions().sizes()[0];
            int cols = grid.dimensions().sizes()[1];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++)
                    builder.append(grid.get(i, j).getCurrentStatus() + " ");
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
