package io.github.carmelolg.jcal.core;

import io.github.carmelolg.jcal.configuration.CellularAutomataConfiguration;
import io.github.carmelolg.jcal.configuration.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder;
import io.github.carmelolg.jcal.core.grid.CellGrid;
import io.github.carmelolg.jcal.model.DefaultCell;
import io.github.carmelolg.jcal.model.DefaultStatus;
import io.github.carmelolg.jcal.model.NeighborhoodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Specification tests for {@link Moore4DNeighborhood}.
 *
 * <p>Verifies neighbor counts at key boundary positions within a 5×5×5×5 grid:
 * interior (all 3^4−1 = 80), corner (2^4−1 = 15), and face (2×3^3−1 = 53).
 */
@DisplayName("Moore4DNeighborhood — neighbor cardinality")
public class Moore4DNeighborhoodTest {

	private static final DefaultStatus DEAD = new DefaultStatus("dead", "0");

	private CellGrid grid;

	@BeforeEach
	void setUp() throws Exception {
		CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
				.setDimensions(5, 5, 5, 5)
				.setTotalIterations(1)
				.setNeighborhoodType(NeighborhoodType.MOORE)
				.setDefaultStatus(DEAD)
				.build();
		grid = new CellularAutomata(config).getGrid();
	}

	@Test
	@DisplayName("Interior cell (2,2,2,2) has 80 Moore-4D neighbors (3^4 - 1)")
	void interiorCellHas80Neighbors() {
		Moore4DNeighborhood nb = new Moore4DNeighborhood();
		List<DefaultCell> neighbors = nb.getNeighbors(grid, new int[]{2, 2, 2, 2});
		assertEquals(80, neighbors.size(),
				"Interior cell in 4D Moore neighborhood must have 80 neighbors");
	}

	@Test
	@DisplayName("Corner cell (0,0,0,0) has 15 Moore-4D neighbors (2^4 - 1)")
	void cornerCellHas15Neighbors() {
		Moore4DNeighborhood nb = new Moore4DNeighborhood();
		List<DefaultCell> neighbors = nb.getNeighbors(grid, new int[]{0, 0, 0, 0});
		assertEquals(15, neighbors.size(),
				"Corner cell in 4D Moore neighborhood must have 15 neighbors");
	}

	@Test
	@DisplayName("Face cell (0,2,2,2) has 53 Moore-4D neighbors (2×3^3 - 1)")
	void faceCellHas53Neighbors() {
		Moore4DNeighborhood nb = new Moore4DNeighborhood();
		List<DefaultCell> neighbors = nb.getNeighbors(grid, new int[]{0, 2, 2, 2});
		assertEquals(53, neighbors.size(),
				"Face cell (one dim at boundary) in 4D Moore neighborhood must have 53 neighbors");
	}
}
