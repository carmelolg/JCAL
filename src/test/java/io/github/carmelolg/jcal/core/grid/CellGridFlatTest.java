package io.github.carmelolg.jcal.core.grid;

import io.github.carmelolg.jcal.model.DefaultCell;
import io.github.carmelolg.jcal.model.DefaultStatus;
import io.github.carmelolg.jcal.model.GridDimensions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CellGridFlat unit tests")
public class CellGridFlatTest {

	private static final DefaultStatus DEAD  = new DefaultStatus("dead",  "0");
	private static final DefaultStatus ALIVE = new DefaultStatus("alive", "1");

	@Test
	@DisplayName("get/set work correctly in 3D")
	public void getSetThreeD() {
		GridDimensions dims = new GridDimensions(3, 4, 5);
		CellGridFlat grid = new CellGridFlat(dims);

		DefaultCell cell = new DefaultCell(ALIVE, 1, 2, 3);
		grid.set(new int[]{1, 2, 3}, cell);

		assertSame(cell, grid.get(1, 2, 3), "get should return the same cell that was set");
	}

	@Test
	@DisplayName("allCoordinates returns all cells in 3D")
	public void allCoordinatesThreeD() {
		GridDimensions dims = new GridDimensions(2, 3, 4);
		CellGridFlat grid = new CellGridFlat(dims);

		List<int[]> coords = grid.allCoordinates();
		assertEquals(24, coords.size(), "2x3x4 grid should have 24 coordinates");
	}

	@Test
	@DisplayName("dimensions() returns the GridDimensions passed at construction")
	public void dimensionsAccessor() {
		GridDimensions dims = new GridDimensions(5, 6, 7);
		CellGridFlat grid = new CellGridFlat(dims);
		assertSame(dims, grid.dimensions());
	}

	@Test
	@DisplayName("getCells returns the underlying array")
	public void getCells() {
		GridDimensions dims = new GridDimensions(2, 2, 2);
		CellGridFlat grid = new CellGridFlat(dims);
		assertNotNull(grid.getCells());
		assertEquals(8, grid.getCells().length);
	}

	@Test
	@DisplayName("Different cells do not overwrite each other (stride correctness)")
	public void strideCorrectness() {
		GridDimensions dims = new GridDimensions(3, 3, 3);
		CellGridFlat grid = new CellGridFlat(dims);

		DefaultCell a = new DefaultCell(ALIVE, 0, 0, 0);
		DefaultCell b = new DefaultCell(ALIVE, 1, 1, 1);
		DefaultCell c = new DefaultCell(ALIVE, 2, 2, 2);

		grid.set(new int[]{0, 0, 0}, a);
		grid.set(new int[]{1, 1, 1}, b);
		grid.set(new int[]{2, 2, 2}, c);

		assertSame(a, grid.get(0, 0, 0));
		assertSame(b, grid.get(1, 1, 1));
		assertSame(c, grid.get(2, 2, 2));
	}
}
