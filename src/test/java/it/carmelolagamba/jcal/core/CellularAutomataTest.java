package it.carmelolagamba.jcal.core;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.carmelolagamba.jcal.JUnitDataTest;
import it.carmelolagamba.jcal.configuration.CellularAutomataConfiguration;
import it.carmelolagamba.jcal.configuration.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder;
import it.carmelolagamba.jcal.model.DefaultCell;
import it.carmelolagamba.jcal.model.NeighborhoodType;

public class CellularAutomataTest {

	CellularAutomataConfiguration _config = null;
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
		assertTrue(ca.utilsMap[0].length == JUnitDataTest.HEIGHT,
				"The cloned map height is not " + JUnitDataTest.HEIGHT);
		assertTrue(ca.map[0][0].equals(JUnitDataTest.map[0][0]), "The map is incongruent after the initialization");

	}

	@Test
	void initialStateEmpty() throws Exception {

		CellularAutomataConfigurationBuilder configBuilder = new CellularAutomataConfigurationBuilder();
		CellularAutomataConfiguration config = configBuilder.setHeight(JUnitDataTest.HEIGHT).setWidth(JUnitDataTest.WIDTH).setTotalIterations(10)
				.setStatusList(JUnitDataTest.status).setNeighborhoodType(NeighborhoodType.MOORE).build();
		ca.init(config);
		
		int count = 0;
		for(int i = 0; i<ca.map.length; i++) {
			for(int j = 0; j<ca.map[0].length; j++) {
				if(ca.map[i][j].equals(new DefaultCell(JUnitDataTest.alive, i, j))) {
					count++;
				}
			}
		}
		
		assertTrue(count == 0, "There's something inside the map at the beginning, but it should be empty with the default status");

	}

	@Test
	void check() throws Exception {

		_config = new CellularAutomataConfigurationBuilder().setInfinite(true).setTotalIterations(10).build();
		assertThrows(Exception.class, () -> ca.init(_config), "An exception was expected but it didn't throw anything");

		_config = new CellularAutomataConfigurationBuilder().setInfinite(false).setTotalIterations(0).build();
		assertThrows(Exception.class, () -> ca.init(_config), "An exception was expected but it didn't throw anything");

		_config = new CellularAutomataConfigurationBuilder().setNeighborhood(null).setNeighborhoodType(null).build();
		assertThrows(Exception.class, () -> ca.init(_config), "An exception was expected but it didn't throw anything");

		_config = new CellularAutomataConfigurationBuilder().setNeighborhood(new MooreNeighborhood())
				.setNeighborhoodType(NeighborhoodType.MOORE).build();
		assertThrows(Exception.class, () -> ca.init(_config), "An exception was expected but it didn't throw anything");

		_config = new CellularAutomataConfigurationBuilder().setStatusList(null).build();
		assertThrows(Exception.class, () -> ca.init(_config), "An exception was expected but it didn't throw anything");

	}

}
