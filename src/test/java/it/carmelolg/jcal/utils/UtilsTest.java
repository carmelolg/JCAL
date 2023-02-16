package it.carmelolg.jcal.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UtilsTest {

	private static final Logger log = Logger.getLogger(UtilsTest.class.getName());

	@BeforeAll
	static void setup() {
		log.info("@BeforeAll - executes once before all test methods in this class");
	}

	@BeforeEach
	void init() {
		log.info("@BeforeEach - executes before each test method in this class");
	}

	@Test
	public void isInsideTest() {
		List<Integer> numbers = Arrays.asList(1, 2, 3);
		assertTrue(numbers.stream().mapToInt(Integer::intValue).sum() > 5, () -> "Sum should be greater than 5");
	}

}
