package io.github.carmelolg.jcal.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GridDimensions unit tests")
public class GridDimensionsTest {

	@Test
	@DisplayName("2D dimensions report correct sizes and total")
	public void twoDimensional() {
		GridDimensions dims = new GridDimensions(4, 5);
		assertEquals(2, dims.getDimensionCount());
		assertEquals(4, dims.getSize(0));
		assertEquals(5, dims.getSize(1));
		assertEquals(20, dims.getTotalCells());
	}

	@Test
	@DisplayName("3D dimensions report correct sizes, total, and strides")
	public void threeDimensional() {
		GridDimensions dims = new GridDimensions(2, 3, 4);
		assertEquals(3, dims.getDimensionCount());
		assertEquals(2, dims.getSize(0));
		assertEquals(3, dims.getSize(1));
		assertEquals(4, dims.getSize(2));
		assertEquals(24, dims.getTotalCells());

		int[] strides = dims.computeStrides();
		assertEquals(12, strides[0]);
		assertEquals(4,  strides[1]);
		assertEquals(1,  strides[2]);
	}

	@Test
	@DisplayName("4D dimensions report correct total")
	public void fourDimensional() {
		GridDimensions dims = new GridDimensions(2, 3, 4, 5);
		assertEquals(4, dims.getDimensionCount());
		assertEquals(120, dims.getTotalCells());
	}

	@Test
	@DisplayName("getSizes returns a defensive copy")
	public void getSizesDefensiveCopy() {
		GridDimensions dims = new GridDimensions(3, 4);
		int[] sizes = dims.getSizes();
		sizes[0] = 999;
		assertEquals(3, dims.getSize(0), "Mutating the returned array must not affect the original");
	}

	@Test
	@DisplayName("Illegal dimension count throws")
	public void illegalDimensionCount() {
		assertThrows(IllegalArgumentException.class, () -> new GridDimensions(5));
		assertThrows(IllegalArgumentException.class, () -> new GridDimensions(1, 2, 3, 4, 5));
	}

	@Test
	@DisplayName("Zero or negative size throws")
	public void invalidSize() {
		assertThrows(IllegalArgumentException.class, () -> new GridDimensions(0, 5));
		assertThrows(IllegalArgumentException.class, () -> new GridDimensions(3, -1));
	}
}
