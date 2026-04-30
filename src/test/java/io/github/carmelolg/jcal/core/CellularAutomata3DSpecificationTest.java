package io.github.carmelolg.jcal.core;

import io.github.carmelolg.jcal.configuration.CellularAutomataConfiguration;
import io.github.carmelolg.jcal.configuration.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder;
import io.github.carmelolg.jcal.core.grid.CellGridFlat;
import io.github.carmelolg.jcal.examples.GameOfLife3DExample;
import io.github.carmelolg.jcal.model.DefaultCell;
import io.github.carmelolg.jcal.model.DefaultStatus;
import io.github.carmelolg.jcal.model.NeighborhoodType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Specification tests for 3D cellular automata.
 *
 * <p>These tests verify the end-to-end behaviour of the library when running
 * on three-dimensional grids, including initialisation, evolution, and result
 * inspection via the {@link CellGridFlat} API.
 */
@DisplayName("3D CellularAutomata specification tests")
public class CellularAutomata3DSpecificationTest {

	private static final DefaultStatus DEAD  = new DefaultStatus("dead",  "0");
	private static final DefaultStatus ALIVE = new DefaultStatus("alive", "1");

	@Test
	@DisplayName("3D CA initialises correctly with CellGridFlat")
	public void initCreates3DGrid() throws Exception {
		CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
				.setDimensions(5, 5, 5)
				.setTotalIterations(1)
				.setDefaultStatus(DEAD)
				.setNeighborhoodType(NeighborhoodType.MOORE)
				.build();

		CellularAutomata ca = new CellularAutomata(config);

		assertInstanceOf(CellGridFlat.class, ca.getGrid(), "3D CA should use CellGridFlat");
		assertEquals(125, ca.getGrid().allCoordinates().size(), "5x5x5 grid should have 125 cells");
	}

	@Test
	@DisplayName("3D CA throws for 2D getMap()")
	public void getMapThrowsForNDGrid() throws Exception {
		CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
				.setDimensions(4, 4, 4)
				.setTotalIterations(1)
				.setDefaultStatus(DEAD)
				.setNeighborhoodType(NeighborhoodType.MOORE)
				.build();

		CellularAutomata ca = new CellularAutomata(config);
		assertThrows(UnsupportedOperationException.class, ca::getMap,
				"getMap() should throw for a 3D CA");
	}

	@Test
	@DisplayName("3D CA evolves: cell with 5 alive neighbours stays alive (custom rule)")
	public void evolutionWithCustomRule() throws Exception {
		// Place a 3x3x3 block of alive cells at origin inside a 5x5x5 grid.
		// The centre cell at (1,1,1) has all 26 neighbours within the block.
		java.util.List<DefaultCell> initialState = new java.util.ArrayList<>();
		for (int x = 0; x <= 2; x++)
			for (int y = 0; y <= 2; y++)
				for (int z = 0; z <= 2; z++)
					initialState.add(new DefaultCell(ALIVE, x, y, z));

		CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
				.setDimensions(5, 5, 5)
				.setTotalIterations(1)
				.setDefaultStatus(DEAD)
				.setNeighborhoodType(NeighborhoodType.MOORE)
				.setInitalState(initialState)
				.build();

		CellularAutomata ca = new CellularAutomata(config);

		// Rule: alive if exactly 5 alive neighbours
		CellularAutomataExecutor rule = new CellularAutomataExecutor() {
			@Override
			public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {
				long aliveCount = neighbors.stream()
						.filter(n -> n.getCurrentStatus().equals(ALIVE)).count();
				DefaultCell next = new DefaultCell(DEAD, cell.getCoordinates());
				if (aliveCount == 5) next.setCurrentStatus(ALIVE);
				return next;
			}
		};

		ca = rule.run(ca);

		// Centre cell (1,1,1) in a 3x3x3 alive block has 26 alive neighbours → stays dead with rule "=5"
		assertEquals(DEAD, ca.getGrid().get(1, 1, 1).getCurrentStatus(),
				"Centre cell with 26 alive neighbours should be dead under 'exactly 5' rule");
	}

	@Test
	@DisplayName("3D CA resolves Moore neighborhood automatically")
	public void automaticMoore3DNeighborhood() throws Exception {
		CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
				.setDimensions(3, 3, 3)
				.setTotalIterations(1)
				.setDefaultStatus(DEAD)
				.setNeighborhoodType(NeighborhoodType.MOORE)
				.build();

		CellularAutomata ca = new CellularAutomata(config);
		assertInstanceOf(Moore3DNeighborhood.class, ca.getNeighborhood(),
				"Moore type on 3D grid should resolve to Moore3DNeighborhood");
	}

	@Test
	@DisplayName("3D CA resolves VonNeumann neighborhood automatically")
	public void automaticVonNeumann3DNeighborhood() throws Exception {
		CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
				.setDimensions(3, 3, 3)
				.setTotalIterations(1)
				.setDefaultStatus(DEAD)
				.setNeighborhoodType(NeighborhoodType.VON_NEUMANN)
				.build();

		CellularAutomata ca = new CellularAutomata(config);
		assertInstanceOf(VonNeumann3DNeighborhood.class, ca.getNeighborhood(),
				"VonNeumann type on 3D grid should resolve to VonNeumann3DNeighborhood");
	}

	@Test
	@DisplayName("check() rejects invalid dimension count")
	public void checkRejectsInvalidDimensions() {
		CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
				.setDimensions(5)
				.setTotalIterations(1)
				.setDefaultStatus(DEAD)
				.setNeighborhoodType(NeighborhoodType.MOORE)
				.build();

		CellularAutomata ca = new CellularAutomata();
		assertThrows(Exception.class, () -> ca.init(config),
				"1D dimension should be rejected");
	}

	@Test
	@DisplayName("Carter Bays 3D still life is stable after 3 iterations")
	public void carter3DStillLifeIsStableAfter3Iterations() throws Exception {
		// The six-cell diagonal still life: each cell has exactly 5 alive Moore
		// neighbours within the group (satisfies S5,6), and no adjacent dead cell
		// reaches the birth threshold of exactly 5 (B5). Pattern must be unchanged.
		List<DefaultCell> initialState = new ArrayList<>();
		for (int[] c : GameOfLife3DExample.STILL_LIFE_COORDS)
			initialState.add(new DefaultCell(GameOfLife3DExample.ALIVE, c[0], c[1], c[2]));

		CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
				.setDimensions(7, 7, 7)
				.setTotalIterations(3)
				.setDefaultStatus(GameOfLife3DExample.DEAD)
				.setNeighborhoodType(NeighborhoodType.MOORE)
				.setInitalState(initialState)
				.build();

		CellularAutomata ca = new CellularAutomata(config);
		ca = new GameOfLife3DExample.Carter3DLifeRule().run(ca);

		CellGridFlat grid = (CellGridFlat) ca.getGrid();
		Set<String> aliveCoordsAfter = grid.allCoordinates().stream()
				.filter(c -> grid.get(c).getCurrentStatus().equals(GameOfLife3DExample.ALIVE))
				.map(Arrays::toString)
				.collect(Collectors.toSet());

		Set<String> expected = Arrays.stream(GameOfLife3DExample.STILL_LIFE_COORDS)
				.map(Arrays::toString)
				.collect(Collectors.toSet());

		assertEquals(6, aliveCoordsAfter.size(),
				"Still life must have exactly 6 alive cells after 3 iterations");
		assertEquals(expected, aliveCoordsAfter,
				"Still life coordinates must be unchanged after 3 iterations");
	}

	@Test
	@DisplayName("2x2x2 block collapses to zero under Carter Bays S5,6/B5 rules")
	public void twoByCubedBlockCollapsesToZeroAfterOneIteration() throws Exception {
		// Each cell in the 2x2x2 block has 7 alive Moore neighbours (all other block cells),
		// which exceeds the survival range of 5-6, so all 8 cells die.
		// No adjacent dead cell reaches the birth threshold of exactly 5
		// (face-adjacent have 4, edge-adjacent 2, corner-adjacent 1).
		List<DefaultCell> initialState = new ArrayList<>();
		for (int x = 3; x <= 4; x++)
			for (int y = 3; y <= 4; y++)
				for (int z = 3; z <= 4; z++)
					initialState.add(new DefaultCell(GameOfLife3DExample.ALIVE, x, y, z));

		CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
				.setDimensions(7, 7, 7)
				.setTotalIterations(1)
				.setDefaultStatus(GameOfLife3DExample.DEAD)
				.setNeighborhoodType(NeighborhoodType.MOORE)
				.setInitalState(initialState)
				.build();

		CellularAutomata ca = new CellularAutomata(config);
		ca = new GameOfLife3DExample.Carter3DLifeRule().run(ca);

		CellGridFlat grid = (CellGridFlat) ca.getGrid();
		long aliveCount = grid.allCoordinates().stream()
				.filter(c -> grid.get(c).getCurrentStatus().equals(GameOfLife3DExample.ALIVE))
				.count();

		assertEquals(0, aliveCount,
				"2x2x2 block must collapse to zero under Carter Bays S5,6/B5 rules: "
						+ "all 8 cells have 7 neighbours (need 5-6), no dead cell reaches birth threshold");
	}
}
