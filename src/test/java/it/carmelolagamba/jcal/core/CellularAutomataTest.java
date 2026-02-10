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
		CellularAutomataConfiguration config = configBuilder.setHeight(JUnitDataTest.HEIGHT)
				.setWidth(JUnitDataTest.WIDTH).setTotalIterations(10).setDefaultStatus(JUnitDataTest.dead)
				.setNeighborhoodType(NeighborhoodType.MOORE).build();
		ca.init(config);

		int count = 0;
		for (int i = 0; i < ca.map.length; i++) {
			for (int j = 0; j < ca.map[0].length; j++) {
				if (ca.map[i][j].equals(new DefaultCell(JUnitDataTest.alive, i, j))) {
					count++;
				}
			}
		}

		assertTrue(count == 0,
				"There's something inside the map at the beginning, but it should be empty with the default status");

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

		_config = new CellularAutomataConfigurationBuilder().setDefaultStatus(null).build();
		assertThrows(Exception.class, () -> ca.init(_config), "An exception was expected but it didn't throw anything");

	}

	@Test
	void constructorWithConfig() throws Exception {
		CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
				.setHeight(JUnitDataTest.HEIGHT)
				.setWidth(JUnitDataTest.WIDTH)
				.setTotalIterations(10)
				.setDefaultStatus(JUnitDataTest.dead)
				.setNeighborhoodType(NeighborhoodType.MOORE)
				.build();
		
		CellularAutomata newCA = new CellularAutomata(config);
		assertTrue(newCA.getMap() != null, "Map should be initialized");
		assertTrue(newCA.getMap().length == JUnitDataTest.WIDTH, "Map width should be " + JUnitDataTest.WIDTH);
	}

	@Test
	void settersAndGetters() throws Exception {
		ca.init(_config);
		
		DefaultCell[][] newMap = new DefaultCell[5][5];
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				newMap[i][j] = new DefaultCell(JUnitDataTest.dead, i, j);
			}
		}
		
		ca.setMap(newMap);
		assertTrue(ca.getMap().length == 5, "Map should have been updated to size 5");
		
		ca.setUtilsMap(newMap);
		assertTrue(ca.getUtilsMap().length == 5, "UtilsMap should have been updated to size 5");
		
		VonNeumannNeighborhood newNeighborhood = new VonNeumannNeighborhood();
		ca.setNeighborhood(newNeighborhood);
		assertInstanceOf(VonNeumannNeighborhood.class, ca.getNeighborhood(), "Neighborhood should be VonNeumannNeighborhood");
		
		CellularAutomataConfiguration newConfig = new CellularAutomataConfigurationBuilder()
				.setWidth(20)
				.setHeight(20)
				.setTotalIterations(5)
				.setDefaultStatus(JUnitDataTest.dead)
				.setNeighborhoodType(NeighborhoodType.MOORE)
				.build();
		ca.setConfig(newConfig);
		assertTrue(ca.getConfig().getWidth() == 20, "Config width should be 20");
	}

	@Test
	void toStringTest() throws Exception {
		ca.init(_config);
		String result = ca.toString();
		assertTrue(result != null && result.length() > 0, "toString should return a non-empty string");
		assertTrue(result.contains("\n"), "toString should contain newlines");
	}

	@Test
	void initWithVonNeumannNeighborhood() throws Exception {
		CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
				.setHeight(JUnitDataTest.HEIGHT)
				.setWidth(JUnitDataTest.WIDTH)
				.setTotalIterations(10)
				.setDefaultStatus(JUnitDataTest.dead)
				.setNeighborhoodType(NeighborhoodType.VON_NEUMANN)
				.build();
		
		ca.init(config);
		assertInstanceOf(VonNeumannNeighborhood.class, ca.getNeighborhood(), "Should use VonNeumannNeighborhood");
	}

	@Test
	void initWithCustomNeighborhood() throws Exception {
		MooreNeighborhood customNeighborhood = new MooreNeighborhood();
		CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
				.setHeight(JUnitDataTest.HEIGHT)
				.setWidth(JUnitDataTest.WIDTH)
				.setTotalIterations(10)
				.setDefaultStatus(JUnitDataTest.dead)
				.setNeighborhood(customNeighborhood)
				.build();
		
		ca.init(config);
		assertTrue(ca.getNeighborhood() == customNeighborhood, "Should use custom neighborhood");
	}

	@Test
	void checkAllExceptionBranches() throws Exception {
		// Test: both neighborhood type and custom neighborhood set
		_config = new CellularAutomataConfigurationBuilder()
				.setNeighborhoodType(NeighborhoodType.MOORE)
				.setNeighborhood(new MooreNeighborhood())
				.setWidth(10)
				.setHeight(10)
				.setTotalIterations(5)
				.setDefaultStatus(JUnitDataTest.dead)
				.build();
		Exception ex = assertThrows(Exception.class, () -> ca.init(_config));
		assertTrue(ex.getMessage().contains("only one"), "Should throw exception for both neighborhood settings");

		// Test: neither neighborhood type nor custom neighborhood set
		_config = new CellularAutomataConfigurationBuilder()
				.setWidth(10)
				.setHeight(10)
				.setTotalIterations(5)
				.setDefaultStatus(JUnitDataTest.dead)
				.build();
		ex = assertThrows(Exception.class, () -> ca.init(_config));
		assertTrue(ex.getMessage().contains("neighborhood"), "Should throw exception for missing neighborhood");

		// Test: no default status
		_config = new CellularAutomataConfigurationBuilder()
				.setWidth(10)
				.setHeight(10)
				.setTotalIterations(5)
				.setNeighborhoodType(NeighborhoodType.MOORE)
				.build();
		ex = assertThrows(Exception.class, () -> ca.init(_config));
		assertTrue(ex.getMessage().contains("default status"), "Should throw exception for missing default status");

		// Test: infinite with total iterations set
		_config = new CellularAutomataConfigurationBuilder()
				.setInfinite(true)
				.setTotalIterations(10)
				.setWidth(10)
				.setHeight(10)
				.setDefaultStatus(JUnitDataTest.dead)
				.setNeighborhoodType(NeighborhoodType.MOORE)
				.build();
		ex = assertThrows(Exception.class, () -> ca.init(_config));
		assertTrue(ex.getMessage().contains("infinitely"), "Should throw exception for infinite with iterations");

		// Test: not infinite with no iterations
		_config = new CellularAutomataConfigurationBuilder()
				.setInfinite(false)
				.setTotalIterations(0)
				.setWidth(10)
				.setHeight(10)
				.setDefaultStatus(JUnitDataTest.dead)
				.setNeighborhoodType(NeighborhoodType.MOORE)
				.build();
		ex = assertThrows(Exception.class, () -> ca.init(_config));
		assertTrue(ex.getMessage().contains("interactions"), "Should throw exception for no iterations when not infinite");
	}

	@Test
	void initWithEmptyInitialState() throws Exception {
		java.util.List<DefaultCell> emptyList = new java.util.ArrayList<>();
		CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
				.setHeight(5)
				.setWidth(5)
				.setTotalIterations(1)
				.setDefaultStatus(JUnitDataTest.dead)
				.setNeighborhoodType(NeighborhoodType.MOORE)
				.setInitalState(emptyList)
				.build();
		
		ca.init(config);
		assertTrue(ca.getMap() != null, "Map should be initialized even with empty initial state");
	}

}
