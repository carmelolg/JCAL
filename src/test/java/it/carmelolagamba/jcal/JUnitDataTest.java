package it.carmelolagamba.jcal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import it.carmelolagamba.jcal.configuration.CellularAutomataConfiguration;
import it.carmelolagamba.jcal.configuration.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder;
import it.carmelolagamba.jcal.core.CellularAutomata;
import it.carmelolagamba.jcal.model.DefaultCell;
import it.carmelolagamba.jcal.model.DefaultStatus;
import it.carmelolagamba.jcal.model.NeighborhoodType;

public class JUnitDataTest {

	private static final Logger log = Logger.getLogger(JUnitDataTest.class.getName());
	public static int WIDTH = 10, HEIGHT = 10;
	public static DefaultCell[][] map = new DefaultCell[WIDTH][HEIGHT];
	
	public static DefaultStatus dead = new DefaultStatus("dead", "0");
	public static DefaultStatus alive = new DefaultStatus("alive", "1");
	public static List<DefaultStatus> status = Arrays.asList(dead, alive);

	public static CellularAutomataConfiguration config = null;
	public static CellularAutomataConfigurationBuilder configBuilder = new CellularAutomataConfigurationBuilder();
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
				map[i][j] = new DefaultCell(dead, i, j);
			}
		}

		// Init initial state
		for (DefaultCell settedCell : initalState) {
			map[settedCell.col][settedCell.row] = settedCell;
		}

		config = configBuilder.setWidth(10).setHeight(10).setInfinite(false).setTotalIterations(6)
				.setNeighborhoodType(NeighborhoodType.MOORE).setDefaultStatus(dead).setInitalState(initalState).build();

		ca = new CellularAutomata();
		try {
			ca.init(config);
		} catch (Exception e) {
			log.severe(e.getMessage());
		}

	}

}
