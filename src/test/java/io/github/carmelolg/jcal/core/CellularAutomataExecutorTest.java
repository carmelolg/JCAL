package io.github.carmelolg.jcal.core;

import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.neighborhood.NeighborhoodType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CellularAutomataExecutor using a Game-of-Life rule as the concrete implementation.
 */
@DisplayName("CellularAutomataExecutor")
class CellularAutomataExecutorTest {

    private static final CellState DEAD  = new CellState("dead",  "0");
    private static final CellState ALIVE = new CellState("alive", "1");

    /** Minimal Game-of-Life executor for test purposes. */
    private static class GoLExecutor extends CellularAutomataExecutor {
        @Override
        public Cell singleRun(Cell cell, List<Cell> neighbors) {
            long aliveCount = neighbors.stream()
                    .filter(n -> n.getCurrentStatus().equals(ALIVE)).count();
            boolean isAlive = cell.getCurrentStatus().equals(ALIVE);
            CellState next = DEAD;
            if (!isAlive && aliveCount == 3) next = ALIVE;
            else if (isAlive && (aliveCount == 2 || aliveCount == 3)) next = ALIVE;
            return new Cell(next, cell.getCoordinates());
        }
    }

    /** Executor that overrides refinements to set every cell to ALIVE before transition. */
    private static class RefiningExecutor extends CellularAutomataExecutor {
        @Override
        public Cell singleRun(Cell cell, List<Cell> neighbors) {
            return new Cell(cell.getCurrentStatus(), cell.getCoordinates());
        }

        @Override
        public Cell refinements(Cell cell) {
            return new Cell(ALIVE, cell.getCoordinates());
        }
    }

    private CellularAutomata buildCa(int w, int h, int iters, List<Cell> initial) throws Exception {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setWidth(w).setHeight(h)
                .setTotalIterations(iters)
                .setDefaultStatus(DEAD)
                .setNeighborhoodType(NeighborhoodType.MOORE)
                .setInitalState(initial)
                .build();
        return new CellularAutomata(cfg);
    }

    // ── basic run ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("run returns the same CellularAutomata instance")
    void runReturnsSameInstance() throws Exception {
        CellularAutomata ca = buildCa(5, 5, 1, null);
        CellularAutomata result = new GoLExecutor().run(ca);
        assertSame(ca, result);
    }

    @Test
    @DisplayName("run processes the specified number of iterations")
    void runIterationsCount() throws Exception {
        // All-dead 5x5, 10 iterations — should stay all dead
        CellularAutomata ca = buildCa(5, 5, 10, null);
        new GoLExecutor().run(ca);
        for (int[] c : ca.getGrid().allCoordinates()) {
            assertEquals(DEAD, ca.getGrid().get(c).getCurrentStatus(),
                    "Expected all cells dead after 10 GoL iterations on empty grid");
        }
    }

    // ── blinker oscillator specification test ─────────────────────────────

    /**
     * Blinker: three horizontally aligned live cells in row 2 of a 5x5 grid.
     * After 1 GoL iteration they rotate to vertical; after 2 they return to horizontal.
     *
     * Initial:           After 1 step:
     *  . . . . .          . . . . .
     *  . . . . .          . . 1 . .
     *  . 1 1 1 .    →     . . 1 . .
     *  . . . . .          . . 1 . .
     *  . . . . .          . . . . .
     */
    @Test
    @DisplayName("blinker oscillator: horizontal → vertical after 1 step")
    void blinkerHorizontalToVertical() throws Exception {
        List<Cell> initial = List.of(
                new Cell(ALIVE, 2, 1),
                new Cell(ALIVE, 2, 2),
                new Cell(ALIVE, 2, 3)
        );
        CellularAutomata ca = buildCa(5, 5, 1, initial);
        new GoLExecutor().run(ca);

        assertEquals(DEAD,  ca.getGrid().get(2, 1).getCurrentStatus(), "(2,1) should be dead");
        assertEquals(DEAD,  ca.getGrid().get(2, 3).getCurrentStatus(), "(2,3) should be dead");
        assertEquals(ALIVE, ca.getGrid().get(1, 2).getCurrentStatus(), "(1,2) should be alive");
        assertEquals(ALIVE, ca.getGrid().get(2, 2).getCurrentStatus(), "(2,2) should be alive");
        assertEquals(ALIVE, ca.getGrid().get(3, 2).getCurrentStatus(), "(3,2) should be alive");
    }

    @Test
    @DisplayName("blinker oscillator: returns to horizontal after 2 steps")
    void blinkerPeriodTwo() throws Exception {
        List<Cell> initial = List.of(
                new Cell(ALIVE, 2, 1),
                new Cell(ALIVE, 2, 2),
                new Cell(ALIVE, 2, 3)
        );
        CellularAutomata ca = buildCa(5, 5, 2, initial);
        new GoLExecutor().run(ca);

        assertEquals(ALIVE, ca.getGrid().get(2, 1).getCurrentStatus(), "(2,1) should be alive");
        assertEquals(ALIVE, ca.getGrid().get(2, 2).getCurrentStatus(), "(2,2) should be alive");
        assertEquals(ALIVE, ca.getGrid().get(2, 3).getCurrentStatus(), "(2,3) should be alive");
        assertEquals(DEAD,  ca.getGrid().get(1, 2).getCurrentStatus(), "(1,2) should be dead");
        assertEquals(DEAD,  ca.getGrid().get(3, 2).getCurrentStatus(), "(3,2) should be dead");
    }

    // ── refinements ────────────────────────────────────────────────────────

    @Test
    @DisplayName("default refinements returns the cell unchanged")
    void defaultRefinementsReturnsCell() {
        Cell c = new Cell(DEAD, 0, 0);
        Cell refined = new GoLExecutor().refinements(c);
        assertSame(c, refined);
    }

    @Test
    @DisplayName("overriding refinements is applied before transition")
    void refinementsAppliedBeforeTransition() throws Exception {
        // RefiningExecutor sets every cell to ALIVE in refinements then copies status in singleRun.
        // After 1 step all cells must be ALIVE.
        CellularAutomata ca = buildCa(3, 3, 1, null);
        new RefiningExecutor().run(ca);
        for (int[] c : ca.getGrid().allCoordinates()) {
            assertEquals(ALIVE, ca.getGrid().get(c).getCurrentStatus(),
                    "All cells should be alive after refinement sets them to ALIVE");
        }
    }

    // ── infinite run / interruption ────────────────────────────────────────

    @Test
    @DisplayName("run with infinite=true stops when thread is interrupted")
    void infiniteRunStopsOnInterrupt() throws Exception {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setWidth(3).setHeight(3)
                .setInfinite(true)
                .setDefaultStatus(DEAD)
                .setNeighborhoodType(NeighborhoodType.MOORE)
                .build();
        CellularAutomata ca = new CellularAutomata(cfg);
        GoLExecutor executor = new GoLExecutor();

        Thread t = new Thread(() -> {
            try { executor.run(ca); } catch (Exception ignored) {}
        });
        t.start();
        Thread.sleep(50);
        t.interrupt();
        t.join(2000);
        assertFalse(t.isAlive(), "Thread should have terminated after interrupt");
    }
}
