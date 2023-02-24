package it.carmelolg.jcal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import it.carmelolg.jcal.configuration.EnvironmentConfiguration;
import it.carmelolg.jcal.configuration.EnvironmentConfiguration.EnvironmentConfigurationBuilder;
import it.carmelolg.jcal.core.CellularAutomata;
import it.carmelolg.jcal.model.DefaultCell;
import it.carmelolg.jcal.model.DefaultStatus;
import it.carmelolg.jcal.model.NeighborhoodType;

public class JUnitDataTest {

	private static final Logger log = Logger.getLogger(JUnitDataTest.class.getName());
	public static int WIDTH = 10, HEIGHT = 10;
	public static DefaultCell[][] map = new DefaultCell[WIDTH][HEIGHT];
	
	public static DefaultStatus dead = new DefaultStatus("dead", "0");
	public static DefaultStatus alive = new DefaultStatus("alive", "1");
	public static List<DefaultStatus> status = Arrays.asList(dead, alive);

	public static EnvironmentConfiguration config = null;
	public static EnvironmentConfigurationBuilder configBuilder = new EnvironmentConfigurationBuilder();
	public static CellularAutomata ca = null;

	@BeforeAll
	static void setup() {
		init();
	}

	@BeforeEach
	void beforeEach() {
	}

	public static void init() {
		
		
		List<DefaultCell> initalState = new ArrayList<DefaultCell>();
		initalState.add(new DefaultCell(alive, 1, 1));
		initalState.add(new DefaultCell(alive, 1, 2));
		initalState.add(new DefaultCell(alive, 1, 3));
		initalState.add(new DefaultCell(alive, 2, 1));

		// Init cells
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				map[i][j] = new DefaultCell(status.stream().findFirst().get(), i, j);
			}
		}

		// Init initial state
		for (DefaultCell settedCell : initalState) {
			map[settedCell.col][settedCell.row] = settedCell;
		}

		config = configBuilder.setWidth(10).setHeight(10).setInfinite(false).setTotalIterations(6)
				.setNeighborhoodType(NeighborhoodType.MOORE).setStatusList(status).setInitalState(initalState).build();

		ca = new CellularAutomata();
		try {
			ca.init(config);
		} catch (Exception e) {
			log.severe(e.getMessage());
		}

	}

}
