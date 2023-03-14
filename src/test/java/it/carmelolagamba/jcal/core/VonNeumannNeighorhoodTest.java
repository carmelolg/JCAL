package it.carmelolagamba.jcal.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.carmelolagamba.jcal.JUnitDataTest;
import it.carmelolagamba.jcal.model.DefaultCell;

public class VonNeumannNeighorhoodTest {

	private DefaultNeighborhood mn = null;
	
	@BeforeAll
	static void setup() {
		JUnitDataTest.init();
	}

	@BeforeEach
	void beforeEach() {
		mn = new VonNeumannNeighborhood();
	}

	@Test
	void getNeighborhood() {
		
		List<DefaultCell> neighbors = mn.getNeighbors(JUnitDataTest.map, 5, 5);
		assertEquals(4, neighbors.size());
	}
	
}
