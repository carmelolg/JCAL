package it.carmelolagamba.jcal.configuration;

import java.util.List;

import it.carmelolagamba.jcal.core.DefaultNeighborhood;
import it.carmelolagamba.jcal.model.DefaultCell;
import it.carmelolagamba.jcal.model.DefaultStatus;
import it.carmelolagamba.jcal.model.NeighborhoodType;

/**
 * @author Carmelo La Gamba © 2023 is licensed under CC BY-NC-SA 4.0
 */
public class CellularAutomataConfiguration {

	private int width = 100;
	private int height = 100;
	private boolean isInfinite;
	private int totalIterations;
	private boolean activeCells; // Not used yet
	private DefaultStatus defaultStatus;
	private List<DefaultCell> initalState;
	private NeighborhoodType neighborhoodType;
	private DefaultNeighborhood neighborhood;

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
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

	public DefaultStatus getDefaultStatus() {
		return defaultStatus;
	}

	public List<DefaultCell> getInitalState() {
		return initalState;
	}

	public NeighborhoodType getNeighborhoodType() {
		return neighborhoodType;
	}

	public DefaultNeighborhood getNeighborhood() {
		return neighborhood;
	}

	private CellularAutomataConfiguration(CellularAutomataConfigurationBuilder builder) {
		this.width = builder.width;
		this.height = builder.height;
		this.activeCells = builder.activeCells;
		this.defaultStatus = builder.defaultStatus;
		this.initalState = builder.initalState;
		this.isInfinite = builder.isInfinite;
		this.totalIterations = builder.totalIterations;
		this.neighborhoodType = builder.neighborhoodType;
		this.neighborhood = builder.neighborhood;
	}

	public static class CellularAutomataConfigurationBuilder {

		/** Square map is the default */
		private int width = 100;
		private int height = 100;

		/**
		 * If true, the iterations will be infinite, <i>totalIterations</i> otherwise
		 */
		private boolean isInfinite;
		private int totalIterations;

		private boolean activeCells;
		private DefaultStatus defaultStatus;
		private List<DefaultCell> initalState;
		private NeighborhoodType neighborhoodType;
		private DefaultNeighborhood neighborhood;

		public CellularAutomataConfigurationBuilder() {
		}

		/**
		 * Set the matrix width (the number of columns) <b>Default is 100</b>
		 * 
		 * @param width, the columns number expressed in integer
		 * @return the builder {@link CellularAutomataConfigurationBuilder}
		 */
		public CellularAutomataConfigurationBuilder setWidth(int width) {
			this.width = width;
			return this;
		}

		/**
		 * Set the matrix height (the number of rows) <b>Default is 100</b>
		 * 
		 * @param height, the rows number expressed in integer.
		 * @return the builder {@link CellularAutomataConfigurationBuilder}
		 */
		public CellularAutomataConfigurationBuilder setHeight(int height) {
			this.height = height;
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
		 * @param defaultStatus a {@link DefaultStatus} instance
		 * @return the builder {@link CellularAutomataConfigurationBuilder}
		 */
		public CellularAutomataConfigurationBuilder setDefaultStatus(DefaultStatus defaultStatus) {
			this.defaultStatus = defaultStatus;
			return this;
		}

		/**
		 * Set the inital configuration from where starting the cellular automata.
		 * Pratically, the cells that in the starting phase have different status of
		 * empty/dead.
		 * 
		 * @param initalState a {@link List} of {@link DefaultCell}
		 * @return the builder {@link CellularAutomataConfigurationBuilder}
		 */
		public CellularAutomataConfigurationBuilder setInitalState(List<DefaultCell> initalState) {
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
		 * If you have a custom neighborhood you can set your class here. The class has
		 * to inerhit the {@link DefaultNeighborhood} class.
		 * 
		 * @param neighborhood
		 * @return the builder {@link CellularAutomataConfigurationBuilder}
		 */
		public CellularAutomataConfigurationBuilder setNeighborhood(DefaultNeighborhood neighborhood) {
			this.neighborhood = neighborhood;
			return this;
		}

		/**
		 * Build the configuration object
		 * 
		 * @return the builder {@link CellularAutomataConfigurationBuilder}
		 */
		public CellularAutomataConfiguration build() {
			return new CellularAutomataConfiguration(this);
		}

	}

}
