package it.carmelolagamba.jcal.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.carmelolagamba.jcal.JUnitDataTest;

public class DefaultStatusTest {

	@BeforeAll
	static void setup() {
		JUnitDataTest.init();
	}

	@BeforeEach
	void beforeEach() {
	}

	@Test
	public void constructor() {

		DefaultStatus alive = new DefaultStatus("key", true);
		assertTrue(alive.getKey().equals("key"), "Constructor doesn't create the key");
		assertTrue(alive.getValue().equals(true), "Constructor doesn't create the value");

	}

	@Test
	public void equalsTest() {
		DefaultStatus alive = new DefaultStatus("alive", "1");
		assertTrue(alive.equals(JUnitDataTest.alive),
				"Equals method doesn't work - two different DefaultStatus are not equals but should be");
	}
	
	@Test
	public void toStringTest() {
		DefaultStatus alive = new DefaultStatus("alive", "1");
		assertTrue(alive.toString().equals("1 "),
				"toString() of DefaultStatus doesn't work");
	}

}
