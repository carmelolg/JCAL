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
 * Specification tests for {@link VonNeumann4DNeighborhood}.
 *
 * <p>Verifies neighbor counts at key boundary positions within a 5×5×5×5 grid:
 * interior (2×4 = 8) and corner (4, one per each of the 4 positive axes).
 */
@DisplayName("VonNeumann4DNeighborhood — neighbor cardinality")
public class VonNeumann4DNeighborhoodTest {

	private static final DefaultStatus DEAD = new DefaultStatus("dead", "0");

	private CellGrid grid;

	@BeforeEach
	void setUp() throws Exception {
		CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
				.setDimensions(5, 5, 5, 5)
				.setTotalIterations(1)
				.setNeighborhoodType(NeighborhoodType.VON_NEUMANN)
				.setDefaultStatus(DEAD)
				.build();
		grid = new CellularAutomata(config).getGrid();
	}

	@Test
	@DisplayName("Interior cell (2,2,2,2) has 8 VN-4D neighbors (2 per axis × 4 axes)")
	void interiorCellHas8Neighbors() {
		VonNeumann4DNeighborhood nb = new VonNeumann4DNeighborhood();
		List<DefaultCell> neighbors = nb.getNeighbors(grid, new int[]{2, 2, 2, 2});
		assertEquals(8, neighbors.size(),
				"Interior cell in 4D von Neumann neighborhood must have 8 neighbors");
	}

	@Test
	@DisplayName("Corner cell (0,0,0,0) has 4 VN-4D neighbors (only positive axes accessible)")
	void cornerCellHas4Neighbors() {
		VonNeumann4DNeighborhood nb = new VonNeumann4DNeighborhood();
		List<DefaultCell> neighbors = nb.getNeighbors(grid, new int[]{0, 0, 0, 0});
		assertEquals(4, neighbors.size(),
				"Corner cell in 4D von Neumann neighborhood must have 4 neighbors");
	}

	@Test
	@DisplayName("Edge cell (0,2,2,2) has 7 VN-4D neighbors (one axis clipped)")
	void edgeCellHas7Neighbors() {
		VonNeumann4DNeighborhood nb = new VonNeumann4DNeighborhood();
		List<DefaultCell> neighbors = nb.getNeighbors(grid, new int[]{0, 2, 2, 2});
		assertEquals(7, neighbors.size(),
				"Edge cell (one dim at 0) in 4D von Neumann neighborhood must have 7 neighbors");
	}
}
