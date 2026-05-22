package io.github.carmelolg.jcal.core.parallel;

import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.core.CellularAutomataConfiguration;
import io.github.carmelolg.jcal.core.GenerationListener;
import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.neighborhood.NeighborhoodType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CellularAutomataParallelRule using a simple identity rule.
 */
@DisplayName("CellularAutomataParallelRule")
class CellularAutomataParallelRuleTest {

    private static final CellState DEAD  = new CellState("dead",  "0");
    private static final CellState ALIVE = new CellState("alive", "1");

    /** Identity executor: every cell keeps its current state. */
    private static class IdentityParallelExecutor extends CellularAutomataParallelRule {
        @Override
        public Cell transition(Cell cell, List<Cell> neighbors) {
            return new Cell(cell.getCurrentStatus(), cell.getCoordinates());
        }
    }

    /** Game-of-Life parallel executor. */
    private static class GoLParallelExecutor extends CellularAutomataParallelRule {
        @Override
        public Cell transition(Cell cell, List<Cell> neighbors) {
            long aliveCount = neighbors.stream()
                    .filter(n -> n.getCurrentStatus().equals(ALIVE)).count();
            boolean isAlive = cell.getCurrentStatus().equals(ALIVE);
            CellState next = DEAD;
            if (!isAlive && aliveCount == 3) next = ALIVE;
            else if (isAlive && (aliveCount == 2 || aliveCount == 3)) next = ALIVE;
            return new Cell(next, cell.getCoordinates());
        }
    }

    /** Parallel executor that overrides refinements. */
    private static class RefiningParallelExecutor extends CellularAutomataParallelRule {
        @Override
        public Cell transition(Cell cell, List<Cell> neighbors) {
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

    @Test
    @DisplayName("run returns the same CellularAutomata instance")
    void runReturnsSameInstance() throws Exception {
        CellularAutomata ca = buildCa(5, 5, 1, null);
        CellularAutomata result = new IdentityParallelExecutor().run(ca);
        assertSame(ca, result);
    }

    @Test
    @DisplayName("identity executor leaves all cells unchanged after N iterations")
    void identityExecutorPreservesState() throws Exception {
        CellularAutomata ca = buildCa(5, 5, 3, null);
        new IdentityParallelExecutor().run(ca);
        for (int[] c : ca.getGrid().allCoordinates()) {
            assertEquals(DEAD, ca.getGrid().get(c).getCurrentStatus());
        }
    }

    @Test
    @DisplayName("blinker oscillator (parallel): returns to horizontal after 2 steps")
    void blinkerPeriodTwo() throws Exception {
        List<Cell> initial = List.of(
                new Cell(ALIVE, 2, 1),
                new Cell(ALIVE, 2, 2),
                new Cell(ALIVE, 2, 3)
        );
        CellularAutomata ca = buildCa(5, 5, 2, initial);
        new GoLParallelExecutor().run(ca);

        assertEquals(ALIVE, ca.getGrid().get(2, 1).getCurrentStatus());
        assertEquals(ALIVE, ca.getGrid().get(2, 2).getCurrentStatus());
        assertEquals(ALIVE, ca.getGrid().get(2, 3).getCurrentStatus());
    }

    @Test
    @DisplayName("default refinements returns the cell unchanged")
    void defaultRefinementsReturnsCell() {
        Cell c = new Cell(DEAD, 0, 0);
        Cell refined = new IdentityParallelExecutor().refinements(c);
        assertSame(c, refined);
    }

    @Test
    @DisplayName("overriding refinements is applied before transition")
    void refinementsAppliedBeforeTransition() throws Exception {
        CellularAutomata ca = buildCa(3, 3, 1, null);
        new RefiningParallelExecutor().run(ca);
        for (int[] c : ca.getGrid().allCoordinates()) {
            assertEquals(ALIVE, ca.getGrid().get(c).getCurrentStatus());
        }
    }

    @Test
    @DisplayName("exception in refinements propagates as RuntimeException")
    void refinementsExceptionPropagatesAsRuntimeException() throws Exception {
        CellularAutomata ca = buildCa(3, 3, 1, null);
        CellularAutomataParallelRule throwing = new CellularAutomataParallelRule() {
            @Override
            public Cell transition(Cell cell, List<Cell> neighbors) { return cell; }
            @Override
            public Cell refinements(Cell cell) { throw new RuntimeException("intentional refinement error"); }
        };
        assertThrows(RuntimeException.class, () -> throwing.run(ca));
    }

    @Test
    @DisplayName("exception in transition propagates as RuntimeException")
    void transitionExceptionPropagatesAsRuntimeException() throws Exception {
        CellularAutomata ca = buildCa(3, 3, 1, null);
        CellularAutomataParallelRule throwing = new CellularAutomataParallelRule() {
            @Override
            public Cell transition(Cell cell, List<Cell> neighbors) {
                throw new RuntimeException("intentional transition error");
            }
        };
        assertThrows(RuntimeException.class, () -> throwing.run(ca));
    }

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
        IdentityParallelExecutor executor = new IdentityParallelExecutor();

        Thread t = new Thread(() -> {
            try { executor.run(ca); } catch (Exception ignored) {}
        });
        t.start();
        Thread.sleep(50);
        t.interrupt();
        t.join(2000);
        assertFalse(t.isAlive(), "Thread should have terminated after interrupt");
    }

    @Test
    @DisplayName("GenerationListener is called exactly N times for N iterations")
    void generationListenerCalledNTimes() throws Exception {
        int iterations = 5;
        CellularAutomata ca = buildCa(4, 4, iterations, null);
        IdentityParallelExecutor executor = new IdentityParallelExecutor();

        List<Integer> generationsSeen = new ArrayList<>();
        executor.addGenerationListener((gen, snap) -> generationsSeen.add(gen));

        executor.run(ca);

        assertEquals(iterations, generationsSeen.size(), "Listener should be called once per iteration");
        for (int i = 0; i < iterations; i++) {
            assertEquals(i + 1, generationsSeen.get(i), "Generation index should be 1-based");
        }
    }

    @Test
    @DisplayName("multiple GenerationListeners are all notified")
    void multipleListenersAllNotified() throws Exception {
        CellularAutomata ca = buildCa(3, 3, 3, null);
        IdentityParallelExecutor executor = new IdentityParallelExecutor();

        List<Integer> calls1 = new ArrayList<>();
        List<Integer> calls2 = new ArrayList<>();
        executor.addGenerationListener((gen, snap) -> calls1.add(gen));
        executor.addGenerationListener((gen, snap) -> calls2.add(gen));

        executor.run(ca);

        assertEquals(3, calls1.size());
        assertEquals(3, calls2.size());
    }
}
