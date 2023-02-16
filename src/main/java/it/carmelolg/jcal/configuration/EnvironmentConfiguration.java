package it.carmelolg.jcal.configuration;

import java.util.List;

import it.carmelolg.jcal.model.DefaultCell;
import it.carmelolg.jcal.model.DefaultNeighborhood;
import it.carmelolg.jcal.model.Neighborhood;

public class EnvironmentConfiguration {

	private int width = 100;
	private int height = 100;
	private boolean isInfinite;
	private int totalIterations;
	private DefaultCell[][] activeCells; // Not used yet
	private List<String> statusList;
	private List<DefaultCell> initalState;
	private Neighborhood neighborhoodType;
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

	public DefaultCell[][] getActiveCells() {
		return activeCells;
	}

	public List<String> getStatusList() {
		return statusList;
	}

	public List<DefaultCell> getInitalState() {
		return initalState;
	}

	public Neighborhood getNeighborhoodType() {
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

		private DefaultCell[][] activeCells;
		private List<String> statusList;
		private List<DefaultCell> initalState;
		private Neighborhood neighborhoodType;
		private DefaultNeighborhood neighborhood;

		public EnvironmentConfigurationBuilder() {
		}

		public EnvironmentConfigurationBuilder setWidth(int width) {
			this.width = width;
			return this;
		}

		public EnvironmentConfigurationBuilder setHeight(int height) {
			this.height = height;
			return this;
		}

		public EnvironmentConfigurationBuilder setInfinite(boolean isInfinite) {
			this.isInfinite = isInfinite;
			return this;
		}

		public EnvironmentConfigurationBuilder setTotalIterations(int totalIterations) {
			this.totalIterations = totalIterations;
			return this;
		}

		public EnvironmentConfigurationBuilder setActiveCells(DefaultCell[][] activeCells) {
			this.activeCells = activeCells;
			return this;
		}

		public EnvironmentConfigurationBuilder setStatusList(List<String> statusList) {
			this.statusList = statusList;
			return this;
		}

		public EnvironmentConfigurationBuilder setInitalState(List<DefaultCell> initalState) {
			this.initalState = initalState;
			return this;
		}

		public EnvironmentConfigurationBuilder setNeighborhoodType(Neighborhood neighborhoodType) {
			this.neighborhoodType = neighborhoodType;
			return this;
		}

		public EnvironmentConfigurationBuilder setNeighborhood(DefaultNeighborhood neighborhood) {
			this.neighborhood = neighborhood;
			return this;
		}

		public EnvironmentConfiguration build() {
			return new EnvironmentConfiguration(this);
		}

	}

}
