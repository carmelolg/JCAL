package io.github.carmelolg.jcal.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.carmelolg.jcal.JUnitDataTest;
import io.github.carmelolg.jcal.model.DefaultCell;

public class MooreNeighorhoodTest {

	private DefaultNeighborhood mn = null;
	
	@BeforeAll
	static void setup() {
		JUnitDataTest.init();
	}

	@BeforeEach
	void beforeEach() {
		mn = new MooreNeighborhood();
	}

	@Test
	void getNeighborhood() {
		
		List<DefaultCell> neighbors = mn.getNeighbors(JUnitDataTest.map, 5, 5);
		assertEquals(8, neighbors.size());
	}
	
}
