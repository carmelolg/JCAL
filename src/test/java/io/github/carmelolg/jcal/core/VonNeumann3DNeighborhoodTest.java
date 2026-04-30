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

@DisplayName("VonNeumann3DNeighborhood unit tests")
public class VonNeumann3DNeighborhoodTest {

	private static final DefaultStatus DEAD  = new DefaultStatus("dead",  "0");

	private CellGridFlat grid;
	private VonNeumann3DNeighborhood neighborhood;

	@BeforeEach
	void setUp() {
		GridDimensions dims = new GridDimensions(5, 5, 5);
		grid = new CellGridFlat(dims);
		for (int[] coords : grid.allCoordinates()) {
			grid.set(coords, new DefaultCell(DEAD, coords));
		}
		neighborhood = new VonNeumann3DNeighborhood();
	}

	@Test
	@DisplayName("Interior cell has 6 Von Neumann neighbors in 3D")
	public void interiorCellHas6Neighbors() {
		List<DefaultCell> neighbors = neighborhood.getNeighbors(grid, new int[]{2, 2, 2});
		assertEquals(6, neighbors.size(), "An interior 3D Von Neumann cell should have 6 neighbors");
	}

	@Test
	@DisplayName("Corner cell has 3 Von Neumann neighbors in 3D")
	public void cornerCellHas3Neighbors() {
		List<DefaultCell> neighbors = neighborhood.getNeighbors(grid, new int[]{0, 0, 0});
		assertEquals(3, neighbors.size(), "A 3D corner cell should have 3 Von Neumann neighbors");
	}

	@Test
	@DisplayName("Face center has 5 Von Neumann neighbors in 3D")
	public void faceCenterHas5Neighbors() {
		// (0,2,2) — on one face, all neighbors valid except (-1,2,2)
		List<DefaultCell> neighbors = neighborhood.getNeighbors(grid, new int[]{0, 2, 2});
		assertEquals(5, neighbors.size(), "Face center of 5x5x5 should have 5 VN neighbors");
	}

	@Test
	@DisplayName("Neighbors do not include the center cell")
	public void centerNotIncluded() {
		DefaultCell center = grid.get(2, 2, 2);
		List<DefaultCell> neighbors = neighborhood.getNeighbors(grid, new int[]{2, 2, 2});
		assertFalse(neighbors.contains(center), "Center cell must not appear in its own Von Neumann neighborhood");
	}
}
