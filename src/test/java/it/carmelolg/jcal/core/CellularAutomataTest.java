package it.carmelolg.jcal.core;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.carmelolg.jcal.JUnitDataTest;
import it.carmelolg.jcal.configuration.EnvironmentConfiguration;
import it.carmelolg.jcal.configuration.EnvironmentConfiguration.EnvironmentConfigurationBuilder;
import it.carmelolg.jcal.model.NeighborhoodType;

public class CellularAutomataTest {

	EnvironmentConfiguration _config = null;
	CellularAutomata ca = new CellularAutomata();
	
	@BeforeAll
	static void setup() {
		JUnitDataTest.init();
	}

	@BeforeEach
	void beforeEach() {
		_config = JUnitDataTest.config;
	}
	
	@Test
	void init() throws Exception {

		ca.init(_config);
		assertInstanceOf(MooreNeighborhood.class.getClass(), ca.neighborhood.getClass());
		assertTrue(ca.map.length == JUnitDataTest.WIDTH, "The map length is not " + JUnitDataTest.WIDTH);
		assertTrue(ca.map[0].length == JUnitDataTest.HEIGHT, "The map height is not " + JUnitDataTest.HEIGHT);
		assertTrue(ca.utilsMap.length == JUnitDataTest.WIDTH, "The cloned map length is not " + JUnitDataTest.WIDTH);
		assertTrue(ca.utilsMap[0].length == JUnitDataTest.HEIGHT, "The cloned map height is not " + JUnitDataTest.HEIGHT);
		assertTrue(ca.map[0][0].equals(JUnitDataTest.map[0][0]), "The map is incongruent after the initialization");
		
	}
	
	@Test
	void check() throws Exception {
		
		_config = new EnvironmentConfigurationBuilder().setInfinite(true).setTotalIterations(10).build();		
		assertThrows(Exception.class, () -> ca.init(_config), "An exception was expected but it didn't throw anything");
		
		_config = new EnvironmentConfigurationBuilder().setInfinite(false).setTotalIterations(0).build();
		assertThrows(Exception.class, () -> ca.init(_config), "An exception was expected but it didn't throw anything");
		
		_config = new EnvironmentConfigurationBuilder().setNeighborhood(null).setNeighborhoodType(null).build();
		assertThrows(Exception.class, () -> ca.init(_config), "An exception was expected but it didn't throw anything");

		_config = new EnvironmentConfigurationBuilder().setNeighborhood(new MooreNeighborhood()).setNeighborhoodType(NeighborhoodType.MOORE).build();
		assertThrows(Exception.class, () -> ca.init(_config), "An exception was expected but it didn't throw anything");
		
		_config = new EnvironmentConfigurationBuilder().setStatusList(null).build();
		assertThrows(Exception.class, () -> ca.init(_config), "An exception was expected but it didn't throw anything");
		
	}
	
	

}
