package it.carmelolagamba.jcal.core;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.carmelolagamba.jcal.JUnitDataTest;
import it.carmelolagamba.jcal.model.DefaultCell;

public class CellularAutomataParallelExecutorTest {

	GoLParallelExecutor execTest = new GoLParallelExecutor();
	CellularAutomata result = null; 
	
	@BeforeAll
	static void setup() {
		JUnitDataTest.init();
	}

	@BeforeEach
	void beforeEach() {
		result = new CellularAutomata();
	}

	@Test
	void run() throws Exception {

		result = execTest.run(JUnitDataTest.ca);
		assertTrue(result != null, "The actual result is null, expected a map with some cell alive.");
		assertTrue(result.map[0][1].getCurrentStatus().equals(JUnitDataTest.alive), "The result is not as expected.");
		assertTrue(result.map[0][2].getCurrentStatus().equals(JUnitDataTest.alive), "The result is not as expected.");
		assertTrue(result.map[1][0].getCurrentStatus().equals(JUnitDataTest.alive), "The result is not as expected.");
		assertTrue(result.map[1][3].getCurrentStatus().equals(JUnitDataTest.alive), "The result is not as expected.");
		assertTrue(result.map[2][1].getCurrentStatus().equals(JUnitDataTest.alive), "The result is not as expected.");
		assertTrue(result.map[2][2].getCurrentStatus().equals(JUnitDataTest.alive), "The result is not as expected.");
		
	}

	@Test
	void testRefinements() throws Exception {
		GoLParallelExecutor executor = new GoLParallelExecutor() {
			@Override
			public DefaultCell refinements(DefaultCell cell) {
				// Override refinements to test it's being called
				DefaultCell refined = new DefaultCell(cell.currentStatus, cell.row, cell.col);
				return refined;
			}
		};
		
		result = executor.run(JUnitDataTest.ca);
		assertTrue(result != null, "The result should not be null");
	}

}
