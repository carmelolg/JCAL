package io.github.carmelolg.jcal.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.carmelolg.jcal.JUnitDataTest;
import io.github.carmelolg.jcal.configuration.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder;
import io.github.carmelolg.jcal.core.MooreNeighborhood;
import io.github.carmelolg.jcal.model.DefaultCell;
import io.github.carmelolg.jcal.model.DefaultStatus;
import io.github.carmelolg.jcal.model.NeighborhoodType;

public class CellularAutomataConfigurationTest {

	@BeforeAll
	static void setup() {
		JUnitDataTest.init();
	}

	@Test
	public void testGetActiveCells() {
		CellularAutomataConfigurationBuilder builder = new CellularAutomataConfigurationBuilder();
		CellularAutomataConfiguration config = builder
				.setActiveCells(true)
				.setWidth(10)
				.setHeight(10)
				.setTotalIterations(5)
				.setDefaultStatus(JUnitDataTest.dead)
				.setNeighborhoodType(NeighborhoodType.MOORE)
				.build();

		assertTrue(config.getActiveCells(), "getActiveCells should return true when set to true");

		config = builder
				.setActiveCells(false)
				.build();

		assertFalse(config.getActiveCells(), "getActiveCells should return false when set to false");
	}

	@Test
	public void testAllGetters() {
		List<DefaultCell> initialState = new ArrayList<>();
		initialState.add(new DefaultCell(JUnitDataTest.alive, 1, 1));
		
		MooreNeighborhood neighborhood = new MooreNeighborhood();
		DefaultStatus defaultStatus = new DefaultStatus("test", "0");
		
		CellularAutomataConfigurationBuilder builder = new CellularAutomataConfigurationBuilder();
		CellularAutomataConfiguration config = builder
				.setWidth(20)
				.setHeight(30)
				.setInfinite(true)
				.setTotalIterations(10)
				.setActiveCells(true)
				.setDefaultStatus(defaultStatus)
				.setInitalState(initialState)
				.setNeighborhood(neighborhood)
				.build();

		assertEquals(20, config.getWidth(), "Width should be 20");
		assertEquals(30, config.getHeight(), "Height should be 30");
		assertTrue(config.isInfinite(), "isInfinite should be true");
		assertEquals(10, config.getTotalIterations(), "Total iterations should be 10");
		assertTrue(config.getActiveCells(), "getActiveCells should be true");
		assertEquals(defaultStatus, config.getDefaultStatus(), "Default status should match");
		assertEquals(initialState, config.getInitalState(), "Initial state should match");
		assertNotNull(config.getNeighborhood(), "Neighborhood should not be null");
		assertEquals(neighborhood, config.getNeighborhood(), "Neighborhood should match");
	}

	@Test
	public void testBuilderWithNeighborhoodType() {
		CellularAutomataConfigurationBuilder builder = new CellularAutomataConfigurationBuilder();
		CellularAutomataConfiguration config = builder
				.setNeighborhoodType(NeighborhoodType.VON_NEUMANN)
				.setWidth(15)
				.setHeight(15)
				.setTotalIterations(5)
				.setDefaultStatus(JUnitDataTest.dead)
				.build();

		assertEquals(NeighborhoodType.VON_NEUMANN, config.getNeighborhoodType(), "NeighborhoodType should be VON_NEUMANN");
	}
}
