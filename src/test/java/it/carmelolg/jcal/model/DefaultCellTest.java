package it.carmelolg.jcal.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.carmelolg.jcal.JUnitDataTest;

public class DefaultCellTest {

	@BeforeAll
	static void setup() {
		JUnitDataTest.init();
	}

	@BeforeEach
	void beforeEach() {
	}

	@Test
	public void constructor() {

		DefaultCell cell = new DefaultCell("0", 0, 0);
		assertTrue(cell.getCurrentStatus().equals("0"), "Constructor doesn't create the current status");
		assertTrue(cell.getCol() == 0, "Constructor doesn't create the col value");
		assertTrue(cell.getRow() == 0, "Constructor doesn't create the row value");
		
	}

	@Test
	public void getCurrentStatus() {
		assertTrue(JUnitDataTest.map[0][0].getCurrentStatus().equals("0"), "currentStatus not setted properly");
	}
	
	@Test
	public void setCurrentStatus() {

	}
	@Test

	public void getCol() {

	}
	
	@Test
	public void getRow() {

	}

}
