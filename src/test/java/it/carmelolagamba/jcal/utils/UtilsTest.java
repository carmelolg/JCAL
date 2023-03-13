package it.carmelolagamba.jcal.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.carmelolagamba.jcal.JUnitDataTest;
import it.carmelolagamba.jcal.model.DefaultCell;
import it.carmelolagamba.jcal.utils.Utils;

public class UtilsTest {

	@BeforeAll
	static void setup() {
		JUnitDataTest.init();
	}

	@BeforeEach
	void beforeEach() {
	}

	@Test
	public void isInsideTest() {
		assertTrue(Utils.isInside(JUnitDataTest.map, 15, 1) == false, () -> "(15,1) is outside the map");
		assertTrue(Utils.isInside(JUnitDataTest.map, -1, 1) == false, () -> "(-1,1) is outside the map");
		assertTrue(Utils.isInside(JUnitDataTest.map, 5, 5) == true, () -> "(5,5) is inside the map");
		assertTrue(Utils.isInside(JUnitDataTest.map, 1, 15) == false, () -> "(1,15) is outside the map");
		assertTrue(Utils.isInside(JUnitDataTest.map, 1, -1) == false, () -> "(1,-1) is outside the map");
	}

	@Test
	public void cloneMaps() {
		try {
			DefaultCell[][] cloned = Utils.cloneMaps(JUnitDataTest.map);

			assertTrue(cloned.length == JUnitDataTest.map.length, () -> "Cloned map has a different col length");
			assertTrue(cloned[0].length == JUnitDataTest.map[0].length, () -> "Cloned map has a different row length");
			assertTrue(cloned[0][0].equals(JUnitDataTest.map[0][0]),
					() -> "First cell of cloned map is different then the original map");

		} catch (CloneNotSupportedException e) {
			assertTrue(e.getMessage() != null, () -> "CloneNotSupportedException triggered.");
		}

	}
}
