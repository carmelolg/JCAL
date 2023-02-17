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
		assertTrue(JUnitDataTest.map[0][0].getCurrentStatus().equals("0"),
				"Get method doesn't work - currentStatus's not setted properly");
	}

	@Test
	public void setCurrentStatus() {

		DefaultCell cell = new DefaultCell("0", 0, 0);
		cell.setCurrentStatus("1");
		assertTrue(cell.getCurrentStatus().equals("1"),
				"Set method doesn't work - currentStatus's not setted properly ");

	}

	@Test
	public void getCol() {
		assertTrue(JUnitDataTest.map[0][0].getCol() == 0,
				"Get method doesn't work - col is not setted properly");
	}

	@Test
	public void getRow() {
		assertTrue(JUnitDataTest.map[0][0].getRow() == 0,
				"Get method doesn't work - row is not setted properly");
	}

	@Test
	public void equalsTest() {
		
		try {
			DefaultCell cell = JUnitDataTest.map[0][0].clone();
			assertTrue(cell.equals(JUnitDataTest.map[0][0]),
					"Equals method doesn't work - two different DefaultCell are not equals");
			
		} catch (CloneNotSupportedException e) {
			assertTrue(e.getMessage() != null, () -> "CloneNotSupportedException triggered.");
		}
	}
		
}
