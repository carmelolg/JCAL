package io.github.carmelolg.jcal.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.carmelolg.jcal.JUnitDataTest;
import io.github.carmelolg.jcal.model.DefaultCell;

import static org.junit.jupiter.api.Assertions.*;

public class CellularAutomataExecutorTest {

	GoLExecutor execTest = new GoLExecutor();
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
        assertNotNull(result, "The actual result is null, expected a map with some cell alive.");
        assertEquals(result.map[0][1].getCurrentStatus(), JUnitDataTest.alive, "The result is not as expected.");
        assertEquals(result.map[0][2].getCurrentStatus(), JUnitDataTest.alive, "The result is not as expected.");
        assertEquals(result.map[1][0].getCurrentStatus(), JUnitDataTest.alive, "The result is not as expected.");
        assertEquals(result.map[1][3].getCurrentStatus(), JUnitDataTest.alive, "The result is not as expected.");
        assertEquals(result.map[2][1].getCurrentStatus(), JUnitDataTest.alive, "The result is not as expected.");
        assertEquals(result.map[2][2].getCurrentStatus(), JUnitDataTest.alive, "The result is not as expected.");
		
	}

	@Test
	void testRefinements() throws Exception {
		GoLExecutor executor = new GoLExecutor() {
			@Override
			public DefaultCell refinements(DefaultCell cell) {
				// Override refinements to test it's being called
				return new DefaultCell(cell.currentStatus, cell.row, cell.col);
			}
		};

		result = executor.run(JUnitDataTest.ca);
        assertNotNull(result, "The result should not be null");
	}

}
