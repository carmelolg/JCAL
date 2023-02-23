package it.carmelolg.jcal.configuration;

import java.util.List;

import it.carmelolg.jcal.core.DefaultNeighborhood;
import it.carmelolg.jcal.model.DefaultCell;
import it.carmelolg.jcal.model.NeighborhoodType;

/**
 * @author Carmelo La Gamba
 * © 2023 is licensed under CC BY-NC-SA 4.0 
 */
public class EnvironmentConfiguration {

	private int width = 100;
	private int height = 100;
	private boolean isInfinite;
	private int totalIterations;
	private boolean activeCells; // Not used yet
	private List<String> statusList;
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

	public List<String> getStatusList() {
		return statusList;
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

	public void setNeighborhood(DefaultNeighborhood neighborhood) {
		this.neighborhood = neighborhood;
	}

	private EnvironmentConfiguration(EnvironmentConfigurationBuilder builder) {
		this.width = builder.width;
		this.height = builder.height;
		this.activeCells = builder.activeCells;
		this.statusList = builder.statusList;
		this.initalState = builder.initalState;
		this.isInfinite = builder.isInfinite;
		this.totalIterations = builder.totalIterations;
		this.neighborhoodType = builder.neighborhoodType;
		this.neighborhood = builder.neighborhood;
	}

	public static class EnvironmentConfigurationBuilder {

		/** Square map is the default */
		private int width = 100;
		private int height = 100;

		/**
		 * If true, the iterations will be infinite, <i>totalIterations</i> otherwise
		 */
		private boolean isInfinite;
		private int totalIterations;

		private boolean activeCells;
		private List<String> statusList;
		private List<DefaultCell> initalState;
		private NeighborhoodType neighborhoodType;
		private DefaultNeighborhood neighborhood;

		public EnvironmentConfigurationBuilder() {
		}

		/**
		 * Set the matrix width (the number of columns)
		 * <b>Default is 100</b>
		 * @param width {the columns number expressed in integer}
		 * @return the builder {@link EnvironmentConfigurationBuilder} 
		 */
		public EnvironmentConfigurationBuilder setWidth(int width) {
			this.width = width;
			return this;
		}

		/**
		 * Set the matrix height (the number of rows)
		 * <b>Default is 100</b>
		 * @param height {the rows number expressed in integer.}
		 * @return the builder {@link EnvironmentConfigurationBuilder} 
		 */
		public EnvironmentConfigurationBuilder setHeight(int height) {
			this.height = height;
			return this;
		}

		/**
		 * @param isInfinite <b><i>true</i></b> if you want to run infinitely, <b><i>false</i></b> otherwise
		 * @return the builder {@link EnvironmentConfigurationBuilder} 
		 */
		public EnvironmentConfigurationBuilder setInfinite(boolean isInfinite) {
			this.isInfinite = isInfinite;
			return this;
		}

		/**
		 * Set the number of iterations of the transaction function
		 * @param totalIterations the number of iteractions
		 * @return the builder {@link EnvironmentConfigurationBuilder} 
		 */
		public EnvironmentConfigurationBuilder setTotalIterations(int totalIterations) {
			this.totalIterations = totalIterations;
			return this;
		}
		
		/**
		 * <b>Function temporary suspended.</b>
		 * @param activeCells <b><i>true</i></b> if you want otpimize the transaction function using on the iterations only the active cells (cells with status not empty/dead), <b><i>false</i></b> otherwise
		 * @return the builder {@link EnvironmentConfigurationBuilder} 
		 */
		@Deprecated
		public EnvironmentConfigurationBuilder setActiveCells(boolean activeCells) {
			this.activeCells = activeCells;
			return this;
		}

		/**
		 * Set the list of status usable on the Cellular Automata.
		 * @param statusList a {@link List} of {@link String}
		 * @return the builder {@link EnvironmentConfigurationBuilder} 
		 */
		public EnvironmentConfigurationBuilder setStatusList(List<String> statusList) {
			this.statusList = statusList;
			return this;
		}

		/**
		 * Set the inital configuration from where starting the cellular automata. Pratically, the cells that in the starting phase have different status of empty/dead.
		 * @param initalState a {@link List} of {@link DefaultCell}
		 * @return the builder {@link EnvironmentConfigurationBuilder} 
		 */
		public EnvironmentConfigurationBuilder setInitalState(List<DefaultCell> initalState) {
			this.initalState = initalState;
			return this;
		}

		/**
		 * If you don't have a custom neighborhood you can choose one already implemented in the enum NeighborhoodType
		 * @param neighborhoodType the type of neighboorhood
		 * @return the builder {@link EnvironmentConfigurationBuilder} 
		 */
		public EnvironmentConfigurationBuilder setNeighborhoodType(NeighborhoodType neighborhoodType) {
			this.neighborhoodType = neighborhoodType;
			return this;
		}

		/**
		 * If you have a custom neighborhood you can set your class here.
		 * The class has to inerhit the {@link DefaultNeighborhood} class.
		 * @param neighborhood
		 * @return the builder {@link EnvironmentConfigurationBuilder} 
		 */
		public EnvironmentConfigurationBuilder setNeighborhood(DefaultNeighborhood neighborhood) {
			this.neighborhood = neighborhood;
			return this;
		}

		/**
		 * Build the configuration object
		 * @return the builder {@link EnvironmentConfigurationBuilder} 
		 */
		public EnvironmentConfiguration build() {
			return new EnvironmentConfiguration(this);
		}

	}

}
