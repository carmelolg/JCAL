package io.github.carmelolg.jcal.core;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.carmelolg.jcal.neighborhood.Neighborhood;
import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.neighborhood.NeighborhoodType;

/**
 * Immutable configuration object for a Cellular Automata instance.
 *
 * <p>Build instances exclusively through the inner {@link CellularAutomataConfigurationBuilder}:
 * <pre>{@code
 * CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
 *     .setWidth(20)
 *     .setHeight(20)
 *     .setTotalIterations(10)
 *     .setDefaultStatus(dead)
 *     .setNeighborhoodType(NeighborhoodType.MOORE)
 *     .build();
 * }</pre>
 *
 * <p>Key settings:
 * <ul>
 *   <li>{@link #getWidth()} / {@link #getHeight()} - grid dimensions (default 100x100)</li>
 *   <li>{@link #isInfinite()} / {@link #getTotalIterations()} - run mode (mutually exclusive)</li>
 *   <li>{@link #getDefaultStatus()} - initial state applied to every cell</li>
 *   <li>{@link #getInitalState()} - optional list of cells with non-default initial states</li>
 *   <li>{@link #getNeighborhoodType()} or {@link #getNeighborhood()} - neighborhood strategy
 *       (exactly one must be set)</li>
 * </ul>
 *
 * @author Carmelo La Gamba
 * @see CellularAutomataConfigurationBuilder
 * @see io.github.carmelolg.jcal.core.CellularAutomata
 */
public class CellularAutomataConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CellularAutomataConfiguration.class);

    private int[] dimensions = {100, 100};
    private boolean isInfinite;
    private int totalIterations;
    private boolean activeCells; // Not used yet
    private CellState defaultStatus;
    private List<Cell> initalState;
    private NeighborhoodType neighborhoodType;
    private Neighborhood neighborhood;
    private NativeRule nativeRule;

    /**
     * Returns the grid width (dimension 0).
     *
     * @return the width
     */
    public int getWidth() {
        return dimensions[0];
    }

    /**
     * Returns the grid height (dimension 1).
     *
     * @return the height
     */
    public int getHeight() {
        return dimensions[1];
    }

    /**
     * Returns a copy of the full dimensions array.
     *
     * @return defensive copy of dimensions
     */
    public int[] getDimensions() {
        return dimensions.clone();
    }

    public boolean isInfinite() {
        return isInfinite;
    }

    public int getTotalIterations() {
        return totalIterations;
    }

    public boolean getActiveCells() {
        return activeCells;
    }

    public CellState getDefaultStatus() {
        return defaultStatus;
    }

    public List<Cell> getInitalState() {
        return initalState;
    }

    public NeighborhoodType getNeighborhoodType() {
        return neighborhoodType;
    }

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    /**
     * Returns the built-in Rust rule configured for native execution,
     * or {@code null} when the Java execution path should be used.
     *
     * @return the {@link NativeRule}, or {@code null}
     */
    public NativeRule getNativeRule() {
        return nativeRule;
    }

    private CellularAutomataConfiguration(CellularAutomataConfigurationBuilder builder) {
        this.dimensions = builder.dimensions.clone();
        this.activeCells = builder.activeCells;
        this.defaultStatus = builder.defaultStatus;
        this.initalState = builder.initalState;
        this.isInfinite = builder.isInfinite;
        this.totalIterations = builder.totalIterations;
        this.neighborhoodType = builder.neighborhoodType;
        this.neighborhood = builder.neighborhood;
        this.nativeRule = builder.nativeRule;
    }

    public static class CellularAutomataConfigurationBuilder {

        private static final Logger logger = LoggerFactory.getLogger(CellularAutomataConfigurationBuilder.class);

        /**
         * Square map is the default
         */
        private int[] dimensions = {100, 100};

        /**
         * If true, the iterations will be infinite, <i>totalIterations</i> otherwise
         */
        private boolean isInfinite;
        private int totalIterations;

        private boolean activeCells;
        private CellState defaultStatus;
        private List<Cell> initalState;
        private NeighborhoodType neighborhoodType;
        private Neighborhood neighborhood;
        private NativeRule nativeRule;

        public CellularAutomataConfigurationBuilder() {
        }

        /**
         * Set the matrix width (the number of columns) <b>Default is 100</b>
         *
         * @param width, the columns number expressed in integer
         * @return the builder {@link CellularAutomataConfigurationBuilder}
         */
        public CellularAutomataConfigurationBuilder setWidth(int width) {
            this.dimensions[0] = width;
            return this;
        }

        /**
         * Set the matrix height (the number of rows) <b>Default is 100</b>
         *
         * @param height, the rows number expressed in integer.
         * @return the builder {@link CellularAutomataConfigurationBuilder}
         */
        public CellularAutomataConfigurationBuilder setHeight(int height) {
            this.dimensions[1] = height;
            return this;
        }

        /**
         * Set the dimensions of the grid for 3D/4D support.
         *
         * @param dims the size of each dimension (2 to 4 values)
         * @return the builder {@link CellularAutomataConfigurationBuilder}
         */
        public CellularAutomataConfigurationBuilder setDimensions(int... dims) {
            this.dimensions = dims.clone();
            return this;
        }

        /**
         * @param isInfinite <b><i>true</i></b> if you want to run infinitely,
         *                   <b><i>false</i></b> otherwise
         * @return the builder {@link CellularAutomataConfigurationBuilder}
         */
        public CellularAutomataConfigurationBuilder setInfinite(boolean isInfinite) {
            this.isInfinite = isInfinite;
            return this;
        }

        /**
         * Set the number of iterations of the transition function
         *
         * @param totalIterations the number of iteractions
         * @return the builder {@link CellularAutomataConfigurationBuilder}
         */
        public CellularAutomataConfigurationBuilder setTotalIterations(int totalIterations) {
            this.totalIterations = totalIterations;
            return this;
        }

        /**
         * <b>Function temporary suspended.</b>
         *
         * @param activeCells <b><i>true</i></b> if you want otpimize the transition
         *                    function using on the iterations only the active cells
         *                    (cells with status not empty/dead), <b><i>false</i></b>
         *                    otherwise
         * @return the builder {@link CellularAutomataConfigurationBuilder}
         */
        @Deprecated
        public CellularAutomataConfigurationBuilder setActiveCells(boolean activeCells) {
            this.activeCells = activeCells;
            return this;
        }

        /**
         * Set the default status. This status is setted on each cells when CA is configured for the first time
         *
         * @param defaultStatus a {@link CellState} instance
         * @return the builder {@link CellularAutomataConfigurationBuilder}
         */
        public CellularAutomataConfigurationBuilder setDefaultStatus(CellState defaultStatus) {
            this.defaultStatus = defaultStatus;
            return this;
        }

        /**
         * Set the inital configuration from where starting the cellular automata.
         * Pratically, the cells that in the starting phase have different status of
         * empty/dead.
         *
         * @param initalState a {@link List} of {@link Cell}
         * @return the builder {@link CellularAutomataConfigurationBuilder}
         */
        public CellularAutomataConfigurationBuilder setInitalState(List<Cell> initalState) {
            this.initalState = initalState;
            return this;
        }

        /**
         * If you don't have a custom neighborhood you can choose one already
         * implemented in the enum NeighborhoodType
         *
         * @param neighborhoodType the type of neighboorhood
         * @return the builder {@link CellularAutomataConfigurationBuilder}
         */
        public CellularAutomataConfigurationBuilder setNeighborhoodType(NeighborhoodType neighborhoodType) {
            this.neighborhoodType = neighborhoodType;
            return this;
        }

        /**
         * If you have a custom neighborhood you can set your class here. The class must
         * extend the {@link Neighborhood} class. For n-dimensional support (n &gt; 2),
         * the class should also implement {@link io.github.carmelolg.jcal.neighborhood.NDCapable}.
         *
         * @param neighborhood
         * @return the builder {@link CellularAutomataConfigurationBuilder}
         */
        public CellularAutomataConfigurationBuilder setNeighborhood(Neighborhood neighborhood) {
            this.neighborhood = neighborhood;
            return this;
        }

        /**
         * Enables the native Rust execution path for this configuration.
         *
         * <p>When a matching built-in rule is available and the native engine is loaded,
         * {@link CellularAutomataExecutor#run(CellularAutomata)} will delegate to the
         * Rust core instead of the Java transition loop.
         *
         * <p>Only binary (two-state) automata are supported natively.  Automata with
         * more than two states fall back to the Java path automatically.
         *
         * @param rule the {@link NativeRule} to use
         * @return the builder {@link CellularAutomataConfigurationBuilder}
         */
        public CellularAutomataConfigurationBuilder useNativeRule(NativeRule rule) {
            this.nativeRule = rule;
            return this;
        }

        /**
         * Build the configuration object
         *
         * @return the builder {@link CellularAutomataConfigurationBuilder}
         */
        public CellularAutomataConfiguration build() {
            logger.debug("Building configuration: dimensions={}, infinite={}, iterations={}, neighborhood={}",
                java.util.Arrays.toString(dimensions), isInfinite, totalIterations,
                neighborhoodType != null ? neighborhoodType : (neighborhood != null ? neighborhood.getClass().getSimpleName() : "NONE"));
            return new CellularAutomataConfiguration(this);
        }

    }

}
