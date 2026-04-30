package io.github.carmelolg.jcal.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.carmelolg.jcal.JUnitDataTest;

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

		DefaultCell cell = new DefaultCell(JUnitDataTest.dead, 0, 0);
		assertTrue(cell.getCurrentStatus().equals(JUnitDataTest.dead), "Constructor doesn't create the current status");
		assertTrue(cell.getCol() == 0, "Constructor doesn't create the col value");
		assertTrue(cell.getRow() == 0, "Constructor doesn't create the row value");

	}

	@Test
	public void getCurrentStatus() {
		assertTrue(JUnitDataTest.map[0][0].getCurrentStatus().equals(JUnitDataTest.dead),
				"Get method doesn't work - currentStatus's not setted properly");
	}

	@Test
	public void setCurrentStatus() {

		DefaultCell cell = new DefaultCell(JUnitDataTest.dead, 0, 0);
		cell.setCurrentStatus(JUnitDataTest.alive);
		assertTrue(cell.getCurrentStatus().equals(JUnitDataTest.alive),
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

	@Test
	public void toStringTest() {
		DefaultCell cell = new DefaultCell(JUnitDataTest.dead, 0, 0);
		String result = cell.toString();
		assertTrue(result != null && result.length() > 0, "toString should return a non-empty string");
		assertTrue(result.contains("0 "), "toString should contain the status value");
	}

	@Test
	public void equalsColDiffersTest() {
		DefaultCell cell1 = new DefaultCell(JUnitDataTest.dead, 0, 0);
		DefaultCell cell2 = new DefaultCell(JUnitDataTest.dead, 1, 0);
		assertFalse(cell1.equals(cell2), "Cells with different col values must not be equal");
	}

	@Test
	public void equalsRowDiffersTest() {
		DefaultCell cell1 = new DefaultCell(JUnitDataTest.dead, 0, 0);
		DefaultCell cell2 = new DefaultCell(JUnitDataTest.dead, 0, 1);
		assertFalse(cell1.equals(cell2), "Cells with different row values must not be equal");
	}

	@Test
	public void equalsStatusDiffersTest() {
		DefaultCell cell1 = new DefaultCell(JUnitDataTest.dead, 0, 0);
		DefaultCell cell2 = new DefaultCell(JUnitDataTest.alive, 0, 0);
		assertFalse(cell1.equals(cell2), "Cells with different statuses must not be equal");
	}

	@Test
	public void getCoordinates() {
		DefaultCell cell = new DefaultCell(JUnitDataTest.dead, 3, 7);
		int[] coords = cell.getCoordinates();
		assertTrue(coords[0] == 3, "getCoordinates()[0] should be col=3");
		assertTrue(coords[1] == 7, "getCoordinates()[1] should be row=7");
	}

	@Test
	public void ndConstructor() {
		DefaultCell cell = new DefaultCell(JUnitDataTest.alive, 1, 2, 3);
		int[] coords = cell.getCoordinates();
		assertTrue(coords[0] == 1 && coords[1] == 2 && coords[2] == 3, "3D constructor should store all coordinates");
		assertTrue(cell.getCurrentStatus().equals(JUnitDataTest.alive), "3D constructor should set status");
	}

	@Test
	public void hashCodeConsistencyTest() {
		DefaultCell cell1 = new DefaultCell(JUnitDataTest.dead, 2, 5);
		DefaultCell cell2 = new DefaultCell(JUnitDataTest.dead, 2, 5);
		assertTrue(cell1.hashCode() == cell2.hashCode(), "Equal cells must have equal hash codes");
	}

	@Test
	public void equalsWithSelfAndNull() {
		DefaultCell cell = new DefaultCell(JUnitDataTest.dead, 0, 0);
		assertTrue(cell.equals(cell), "A cell must be equal to itself");
		assertFalse(cell.equals(null), "A cell must not equal null");
		assertFalse(cell.equals("not a cell"), "A cell must not equal a non-cell object");
	}
		
}
