package it.carmelolg.jcal;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.carmelolg.jcal.model.DefaultCell;

public class JUnitDataTest {

	private static final Logger log = Logger.getLogger(JUnitDataTest.class.getName());
	public static int WIDTH = 10, HEIGHT = 10;
	public static DefaultCell[][] map = new DefaultCell[WIDTH][HEIGHT];
	public static List<String> status = Arrays.asList("0", "1");

	@BeforeAll
	static void setup() {
		log.warning("@BeforeAll - executes once before all test methods in this class");
		init();
	}

	@BeforeEach
	void beforeEach() {
		log.warning("@BeforeEach - executes before each test method in this class");
	}

	public static void init() {

		List<DefaultCell> initalState = new ArrayList<DefaultCell>();
		initalState.add(new DefaultCell("1", 1, 1));
		initalState.add(new DefaultCell("1", 1, 2));
		initalState.add(new DefaultCell("1", 1, 3));
		initalState.add(new DefaultCell("1", 2, 1));

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

	}

	@Test
	public void trigger() {
		List<Integer> numbers = Arrays.asList(1, 2, 3);
		assertTrue(numbers.stream().mapToInt(Integer::intValue).sum() > 5, () -> "Sum should be greater than 5");
	}

}
