package io.github.carmelolg.jcal.core;

import io.github.carmelolg.jcal.core.grid.CellGridFlat;
import io.github.carmelolg.jcal.model.DefaultCell;
import io.github.carmelolg.jcal.model.DefaultStatus;
import io.github.carmelolg.jcal.model.GridDimensions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Moore3DNeighborhood unit tests")
public class Moore3DNeighborhoodTest {

	private static final DefaultStatus DEAD  = new DefaultStatus("dead",  "0");

	private CellGridFlat grid;
	private Moore3DNeighborhood neighborhood;

	@BeforeEach
	void setUp() {
		GridDimensions dims = new GridDimensions(5, 5, 5);
		grid = new CellGridFlat(dims);
		for (int[] coords : grid.allCoordinates()) {
			grid.set(coords, new DefaultCell(DEAD, coords));
		}
		neighborhood = new Moore3DNeighborhood();
	}

	@Test
	@DisplayName("Interior cell has 26 Moore neighbors in 3D")
	public void interiorCellHas26Neighbors() {
		List<DefaultCell> neighbors = neighborhood.getNeighbors(grid, new int[]{2, 2, 2});
		assertEquals(26, neighbors.size(), "An interior 3D Moore cell should have 26 neighbors");
	}

	@Test
	@DisplayName("Corner cell has 7 Moore neighbors in 3D")
	public void cornerCellNeighborCount() {
		List<DefaultCell> neighbors = neighborhood.getNeighbors(grid, new int[]{0, 0, 0});
		assertEquals(7, neighbors.size(), "A 3D corner cell should have 7 Moore neighbors");
	}

	@Test
	@DisplayName("Edge cell has correct count of Moore neighbors in 3D")
	public void edgeCellNeighborCount() {
		// Edge cell at (0, 0, 2) has 11 neighbors: 3x3x2 - 1 = 17... let's count manually
		// Deltas for x: {0,1}, for y: {0,1}, for z: {1,2,3} → actually let me compute:
		// Center is at (0,0,2). dx in {-1,0,1} → valid: {0,1}, dy in {-1,0,1} → valid: {0,1}, dz in {-1,0,1} → valid: {1,2,3}
		// Count = 2*2*3 - 1 = 11
		List<DefaultCell> neighbors = neighborhood.getNeighbors(grid, new int[]{0, 0, 2});
		assertEquals(11, neighbors.size(), "Edge cell (0,0,2) in 5x5x5 should have 11 Moore neighbors");
	}

	@Test
	@DisplayName("Neighbors do not include the center cell")
	public void centerNotIncluded() {
		DefaultCell center = grid.get(2, 2, 2);
		List<DefaultCell> neighbors = neighborhood.getNeighbors(grid, new int[]{2, 2, 2});
		assertFalse(neighbors.contains(center), "Center cell must not appear in its own neighborhood");
	}
}
