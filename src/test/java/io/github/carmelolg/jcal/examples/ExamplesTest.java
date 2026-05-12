package io.github.carmelolg.jcal.examples;

import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the JCAL example programs: smoke tests + branch coverage.
 */
@DisplayName("Examples tests")
class ExamplesTest {

    // ── constructors (coverage of default <init>) ─────────────────────────

    @Test
    @DisplayName("GameOfLifeExample can be instantiated")
    void gameOfLifeExampleConstructor() {
        assertNotNull(new GameOfLifeExample());
    }

    @Test
    @DisplayName("CustomStateExample can be instantiated")
    void customStateExampleConstructor() {
        assertNotNull(new CustomStateExample());
    }

    @Test
    @DisplayName("GameOfLife3DExample can be instantiated")
    void gameOfLife3DExampleConstructor() {
        assertNotNull(new GameOfLife3DExample());
    }

    // ── main() smoke tests ────────────────────────────────────────────────

    @Test
    @DisplayName("GameOfLifeExample.main() completes without exception")
    void gameOfLifeExampleRuns() throws Exception {
        withSuppressedOutput(() -> GameOfLifeExample.main(new String[]{}));
    }

    @Test
    @DisplayName("CustomStateExample.main() completes without exception")
    void customStateExampleRuns() throws Exception {
        withSuppressedOutput(() -> CustomStateExample.main(new String[]{}));
    }

    @Test
    @DisplayName("GameOfLife3DExample.main() completes without exception")
    void gameOfLife3DExampleRuns() throws Exception {
        withSuppressedOutput(() -> GameOfLife3DExample.main(new String[]{}));
    }

    // ── HeatDiffusionRule branch coverage ─────────────────────────────────

    @Test
    @DisplayName("HeatDiffusionRule: HOT cell stays HOT")
    void heatRuleHotStaysHot() {
        Cell hot = new Cell(CustomStateExample.HOT, 1, 1);
        Cell result = new CustomStateExample.HeatDiffusionRule().transition(hot, List.of());
        assertEquals(CustomStateExample.HOT, result.getCurrentStatus());
    }

    @Test
    @DisplayName("HeatDiffusionRule: WARM cell with HOT neighbour becomes HOT")
    void heatRuleWarmBecomesHot() {
        Cell warm = new Cell(CustomStateExample.WARM, 1, 1);
        Cell hotNeighbor = new Cell(CustomStateExample.HOT, 1, 2);
        Cell result = new CustomStateExample.HeatDiffusionRule().transition(warm, List.of(hotNeighbor));
        assertEquals(CustomStateExample.HOT, result.getCurrentStatus());
    }

    @Test
    @DisplayName("HeatDiffusionRule: WARM cell with no neighbours cools to COLD")
    void heatRuleWarmCoolsToCold() {
        Cell warm = new Cell(CustomStateExample.WARM, 1, 1);
        Cell result = new CustomStateExample.HeatDiffusionRule().transition(warm, List.of());
        assertEquals(CustomStateExample.COLD, result.getCurrentStatus());
    }

    @Test
    @DisplayName("HeatDiffusionRule: WARM cell with one WARM neighbour stays WARM")
    void heatRuleWarmStaysWarm() {
        Cell warm = new Cell(CustomStateExample.WARM, 1, 1);
        Cell warmNeighbor = new Cell(CustomStateExample.WARM, 1, 2);
        Cell result = new CustomStateExample.HeatDiffusionRule().transition(warm, List.of(warmNeighbor));
        assertEquals(CustomStateExample.WARM, result.getCurrentStatus());
    }

    @Test
    @DisplayName("HeatDiffusionRule: COLD cell with HOT neighbour becomes WARM")
    void heatRuleColdBecomesWarmFromHot() {
        Cell cold = new Cell(CustomStateExample.COLD, 1, 1);
        Cell hotNeighbor = new Cell(CustomStateExample.HOT, 1, 2);
        Cell result = new CustomStateExample.HeatDiffusionRule().transition(cold, List.of(hotNeighbor));
        assertEquals(CustomStateExample.WARM, result.getCurrentStatus());
    }

    @Test
    @DisplayName("HeatDiffusionRule: COLD cell with two WARM neighbours becomes WARM")
    void heatRuleColdBecomesWarmFromTwoWarm() {
        Cell cold = new Cell(CustomStateExample.COLD, 1, 1);
        List<Cell> neighbors = List.of(
                new Cell(CustomStateExample.WARM, 0, 1),
                new Cell(CustomStateExample.WARM, 2, 1));
        Cell result = new CustomStateExample.HeatDiffusionRule().transition(cold, neighbors);
        assertEquals(CustomStateExample.WARM, result.getCurrentStatus());
    }

    @Test
    @DisplayName("HeatDiffusionRule: COLD cell with no hot/warm neighbours stays COLD")
    void heatRuleColdStaysCold() {
        Cell cold = new Cell(CustomStateExample.COLD, 1, 1);
        Cell result = new CustomStateExample.HeatDiffusionRule().transition(cold, List.of());
        assertEquals(CustomStateExample.COLD, result.getCurrentStatus());
    }

    @Test
    @DisplayName("HeatDiffusionRule: unexpected state throws IllegalStateException")
    void heatRuleUnknownStateThrows() {
        Cell unknown = new Cell(new CellState("plasma", 99), 1, 1);
        assertThrows(IllegalStateException.class,
                () -> new CustomStateExample.HeatDiffusionRule().transition(unknown, List.of()));
    }

    // ── Carter3DLifeRule branch coverage ──────────────────────────────────

    @Test
    @DisplayName("Carter3DLifeRule: dead cell with exactly 5 alive neighbours is born")
    void carter3DDeadCellBorn() {
        Cell dead = new Cell(GameOfLife3DExample.DEAD, 0, 0, 0);
        List<Cell> neighbors = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            neighbors.add(new Cell(GameOfLife3DExample.ALIVE, i, 1, 0));
        Cell result = new GameOfLife3DExample.Carter3DLifeRule().transition(dead, neighbors);
        assertEquals(GameOfLife3DExample.ALIVE, result.getCurrentStatus());
    }

    @Test
    @DisplayName("Carter3DLifeRule: alive cell with 5 alive neighbours survives")
    void carter3DAliveWithFiveSurvives() {
        Cell alive = new Cell(GameOfLife3DExample.ALIVE, 0, 0, 0);
        List<Cell> neighbors = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            neighbors.add(new Cell(GameOfLife3DExample.ALIVE, i, 1, 0));
        Cell result = new GameOfLife3DExample.Carter3DLifeRule().transition(alive, neighbors);
        assertEquals(GameOfLife3DExample.ALIVE, result.getCurrentStatus());
    }

    @Test
    @DisplayName("Carter3DLifeRule: alive cell with 6 alive neighbours survives")
    void carter3DAliveWithSixSurvives() {
        Cell alive = new Cell(GameOfLife3DExample.ALIVE, 0, 0, 0);
        List<Cell> neighbors = new ArrayList<>();
        for (int i = 0; i < 6; i++)
            neighbors.add(new Cell(GameOfLife3DExample.ALIVE, i, 1, 0));
        Cell result = new GameOfLife3DExample.Carter3DLifeRule().transition(alive, neighbors);
        assertEquals(GameOfLife3DExample.ALIVE, result.getCurrentStatus());
    }

    @Test
    @DisplayName("Carter3DLifeRule: alive cell with wrong neighbour count dies")
    void carter3DAliveWithWrongCountDies() {
        Cell alive = new Cell(GameOfLife3DExample.ALIVE, 0, 0, 0);
        Cell result = new GameOfLife3DExample.Carter3DLifeRule().transition(alive, List.of());
        assertEquals(GameOfLife3DExample.DEAD, result.getCurrentStatus());
    }

    @Test
    @DisplayName("GameOfLife3DExample: still-life pattern survives 5 iterations unchanged")
    void stillLifeSurvivesUnchanged() throws Exception {
        io.github.carmelolg.jcal.core.CellularAutomata ca = buildStillLife(5);
        for (int[] coord : GameOfLife3DExample.STILL_LIFE_COORDS) {
            assertEquals(GameOfLife3DExample.ALIVE,
                    ca.getGrid().get(coord).getCurrentStatus(),
                    "Cell " + java.util.Arrays.toString(coord) + " should still be alive");
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────

    private void withSuppressedOutput(RunnableEx task) throws Exception {
        PrintStream original = System.out;
        try {
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            task.run();
        } finally {
            System.setOut(original);
        }
    }

    @FunctionalInterface
    interface RunnableEx { void run() throws Exception; }

    private io.github.carmelolg.jcal.core.CellularAutomata buildStillLife(int iters) throws Exception {
        java.util.List<Cell> initial = new java.util.ArrayList<>();
        for (int[] c : GameOfLife3DExample.STILL_LIFE_COORDS)
            initial.add(new Cell(GameOfLife3DExample.ALIVE, c[0], c[1], c[2]));

        io.github.carmelolg.jcal.core.CellularAutomataConfiguration cfg =
                new io.github.carmelolg.jcal.core.CellularAutomataConfiguration
                        .CellularAutomataConfigurationBuilder()
                        .setDimensions(7, 7, 7)
                        .setTotalIterations(iters)
                        .setDefaultStatus(GameOfLife3DExample.DEAD)
                        .setNeighborhoodType(io.github.carmelolg.jcal.neighborhood.NeighborhoodType.MOORE)
                        .setInitalState(initial)
                        .build();

        io.github.carmelolg.jcal.core.CellularAutomata ca =
                new io.github.carmelolg.jcal.core.CellularAutomata(cfg);
        new GameOfLife3DExample.Carter3DLifeRule().run(ca);
        return ca;
    }
}
