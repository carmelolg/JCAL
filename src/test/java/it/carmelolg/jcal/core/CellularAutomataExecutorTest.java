package it.carmelolg.jcal.core;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.carmelolg.jcal.JUnitDataTest;

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
		assertTrue(result != null, "The actual result is null, expected a map with some cell alive.");
		assertTrue(result.map[0][1].getCurrentStatus().equals("1"), "The result is not as expected.");
		assertTrue(result.map[0][2].getCurrentStatus().equals("1"), "The result is not as expected.");
		assertTrue(result.map[1][0].getCurrentStatus().equals("1"), "The result is not as expected.");
		assertTrue(result.map[1][3].getCurrentStatus().equals("1"), "The result is not as expected.");
		assertTrue(result.map[2][1].getCurrentStatus().equals("1"), "The result is not as expected.");
		assertTrue(result.map[2][2].getCurrentStatus().equals("1"), "The result is not as expected.");
		
	}

}