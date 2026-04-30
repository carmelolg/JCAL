package io.github.carmelolg.jcal.core;

import io.github.carmelolg.jcal.configuration.CellularAutomataConfiguration;
import io.github.carmelolg.jcal.configuration.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder;
import io.github.carmelolg.jcal.core.grid.CellGrid;
import io.github.carmelolg.jcal.model.DefaultCell;
import io.github.carmelolg.jcal.model.DefaultStatus;
import io.github.carmelolg.jcal.model.NeighborhoodType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end specification tests for 4D cellular automata.
 *
 * <p>Verifies grid initialisation, CA execution, and known rule outcomes in a
 * 5×5×5×5 hypergrid using both Moore and von Neumann 4D neighborhoods.
 */
@DisplayName("4D Cellular Automata — specification tests")
public class CellularAutomata4DSpecificationTest {

	private static final DefaultStatus DEAD  = new DefaultStatus("dead",  "0");
	private static final DefaultStatus ALIVE = new DefaultStatus("alive", "1");

	// -------------------------------------------------------------------------
	// Helper: count alive cells in a grid
	// -------------------------------------------------------------------------
	private long countAlive(CellGrid grid) {
		return grid.allCoordinates().stream()
				.filter(c -> grid.get(c).getCurrentStatus().equals(ALIVE))
				.count();
	}

	// -------------------------------------------------------------------------
	// Helper: GoL B3/S23 executor (works in any dimension via generic neighbor list)
	// -------------------------------------------------------------------------
	private CellularAutomataExecutor golExecutor() {
		return new CellularAutomataExecutor() {
			@Override
			public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {
				long aliveCount = neighbors.stream()
						.filter(n -> n.getCurrentStatus().equals(ALIVE)).count();
				boolean isAlive = cell.getCurrentStatus().equals(ALIVE);
				DefaultStatus next = (!isAlive && aliveCount == 3) ? ALIVE
						: (isAlive && (aliveCount == 2 || aliveCount == 3)) ? ALIVE
						: DEAD;
				return new DefaultCell(next, cell.getCoordinates());
			}
		};
	}

	// -------------------------------------------------------------------------
	// Helper: identity executor — cells never change
	// -------------------------------------------------------------------------
	private CellularAutomataExecutor identityExecutor() {
		return new CellularAutomataExecutor() {
			@Override
			public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {
				return new DefaultCell(cell.getCurrentStatus(), cell.getCoordinates());
			}
		};
	}

	// -------------------------------------------------------------------------
	// Tests
	// -------------------------------------------------------------------------

	@Test
	@DisplayName("5×5×5×5 grid initialises with all 625 cells in the default (DEAD) status")
	void gridInitialisesWithAllDefaultStatus() throws Exception {
		CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
				.setDimensions(5, 5, 5, 5)
				.setTotalIterations(1)
				.setNeighborhoodType(NeighborhoodType.MOORE)
				.setDefaultStatus(DEAD)
				.build();
		CellularAutomata ca = new CellularAutomata(config);

		long aliveCount = countAlive(ca.getGrid());
		assertEquals(0, aliveCount,
				"Freshly initialised 4D grid (5^4=625 cells) must have 0 alive cells");
		assertEquals(625, ca.getGrid().allCoordinates().size(),
				"5×5×5×5 grid must have exactly 625 coordinate entries");
	}

	@Test
	@DisplayName("A single alive cell in a 4D Moore CA dies alone (GoL B3/S23, 1 generation)")
	void singleAliveCellDiesAloneIn4D() throws Exception {
		List<DefaultCell> initial = Arrays.asList(
				new DefaultCell(ALIVE, 2, 2, 2, 2)   // center of 5×5×5×5
		);
		CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
				.setDimensions(5, 5, 5, 5)
				.setTotalIterations(1)
				.setNeighborhoodType(NeighborhoodType.MOORE)
				.setDefaultStatus(DEAD)
				.setInitalState(initial)
				.build();
		CellularAutomata ca = new CellularAutomata(config);

		assertEquals(1, countAlive(ca.getGrid()), "Should start with 1 alive cell");

		ca = golExecutor().run(ca);

		// Center has 0 alive neighbors → dies; no dead cell has 3 alive neighbors → no births
		assertEquals(0, countAlive(ca.getGrid()),
				"Single alive cell with no alive neighbors must die after 1 generation");
	}

	@Test
	@DisplayName("Identity rule: alive cells remain alive after 3 iterations in 4D")
	void identityRulePreservesStateIn4D() throws Exception {
		List<DefaultCell> initial = Arrays.asList(
				new DefaultCell(ALIVE, 1, 1, 1, 1),
				new DefaultCell(ALIVE, 2, 2, 2, 2),
				new DefaultCell(ALIVE, 3, 3, 3, 3)
		);
		CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
				.setDimensions(5, 5, 5, 5)
				.setTotalIterations(3)
				.setNeighborhoodType(NeighborhoodType.MOORE)
				.setDefaultStatus(DEAD)
				.setInitalState(initial)
				.build();
		CellularAutomata ca = new CellularAutomata(config);

		assertEquals(3, countAlive(ca.getGrid()), "Should start with 3 alive cells");

		ca = identityExecutor().run(ca);

		assertEquals(3, countAlive(ca.getGrid()),
				"Identity rule must preserve exactly 3 alive cells after 3 iterations");
		assertTrue(ca.getGrid().get(new int[]{1, 1, 1, 1}).getCurrentStatus().equals(ALIVE));
		assertTrue(ca.getGrid().get(new int[]{2, 2, 2, 2}).getCurrentStatus().equals(ALIVE));
		assertTrue(ca.getGrid().get(new int[]{3, 3, 3, 3}).getCurrentStatus().equals(ALIVE));
	}

	@Test
	@DisplayName("VonNeumann-4D: single alive cell dies alone (GoL B3/S23, 1 generation)")
	void singleAliveCellDiesAloneWithVonNeumann4D() throws Exception {
		List<DefaultCell> initial = Arrays.asList(
				new DefaultCell(ALIVE, 2, 2, 2, 2)
		);
		CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
				.setDimensions(5, 5, 5, 5)
				.setTotalIterations(1)
				.setNeighborhoodType(NeighborhoodType.VON_NEUMANN)
				.setDefaultStatus(DEAD)
				.setInitalState(initial)
				.build();
		CellularAutomata ca = new CellularAutomata(config);

		ca = golExecutor().run(ca);

		assertEquals(0, countAlive(ca.getGrid()),
				"Single alive cell in VN-4D with 0 alive neighbors must die after 1 generation");
	}
}
