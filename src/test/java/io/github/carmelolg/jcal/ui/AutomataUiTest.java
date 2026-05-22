package io.github.carmelolg.jcal.ui;

import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.core.CellularAutomataConfiguration;
import io.github.carmelolg.jcal.core.CellularAutomataRule;
import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.grid.GridSnapshot;
import io.github.carmelolg.jcal.neighborhood.NeighborhoodType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@code ui} package.
 *
 * <p>Tests focus on the logic layer ({@link CellRenderer}, {@link AutomataListener},
 * {@link CellularAutomataUIRunner}) using {@link GridDisplay} stubs — no Swing window is
 * instantiated, so the suite runs in any environment.
 */
@DisplayName("UI package")
class AutomataUiTest {

    private static final CellState DEAD  = new CellState("dead",  "0");
    private static final CellState ALIVE = new CellState("alive", "1");

    // ── CellRenderer ─────────────────────────────────────────────────────

    @Test
    @DisplayName("CellRenderer lambda maps states to colours correctly")
    void cellRendererMapsStates() {
        CellRenderer renderer = state -> state.equals(ALIVE) ? Color.GREEN : Color.BLACK;
        assertEquals(Color.GREEN, renderer.getColor(ALIVE));
        assertEquals(Color.BLACK, renderer.getColor(DEAD));
    }

    // ── AutomataListener ─────────────────────────────────────────────────

    @Test
    @DisplayName("AutomataListener forwards snapshots to the display in order")
    void listenerForwardsSnapshots() throws Exception {
        List<Integer> received = new ArrayList<>();

        // Stub display — no Swing involved
        GridDisplay stub = snap -> received.add(snap.getGeneration());
        AutomataListener listener = new AutomataListener(stub, 0);

        CellularAutomata ca = buildCa(3, 3, 3);

        // Simulate direct calls to the listener
        for (int i = 1; i <= 3; i++) {
            listener.onGeneration(i, GridSnapshot.of(i, ca.getGrid()));
        }

        assertEquals(List.of(1, 2, 3), received,
                "Listener must forward snapshots in generation order");
    }

    @Test
    @DisplayName("AutomataListener constructor rejects null display")
    void listenerRejectsNullWindow() {
        assertThrows(NullPointerException.class, () -> new AutomataListener((GridDisplay) null, 0));
    }

    @Test
    @DisplayName("AutomataListener constructor rejects negative delay")
    void listenerRejectsNegativeDelay() {
        GridDisplay stub = snap -> {};
        assertThrows(IllegalArgumentException.class, () -> new AutomataListener(stub, -1));
    }

    // ── CellularAutomataUIRunner ──────────────────────────────────────────

    @Test
    @DisplayName("CellularAutomataUIRunner.create() rejects null ca")
    void runnerRejectsNullCa() {
        assertThrows(NullPointerException.class,
                () -> CellularAutomataUIRunner.create(null, buildRule()));
    }

    @Test
    @DisplayName("CellularAutomataUIRunner.create() rejects null rule")
    void runnerRejectsNullRule() throws Exception {
        assertThrows(NullPointerException.class,
                () -> CellularAutomataUIRunner.create(buildCa(3, 3, 1), null));
    }

    @Test
    @DisplayName("CellularAutomataUIRunner.start() throws when no renderer is set")
    void runnerStartThrowsWithoutRenderer() throws Exception {
        CellularAutomata ca = buildCa(3, 3, 1);
        assertThrows(IllegalStateException.class,
                () -> CellularAutomataUIRunner.create(ca, buildRule()).start());
    }

    @Test
    @DisplayName("CellularAutomataUIRunner.cellSize() rejects non-positive values")
    void runnerRejectsNonPositiveCellSize() throws Exception {
        CellularAutomata ca = buildCa(3, 3, 1);
        CellularAutomataUIRunner runner = CellularAutomataUIRunner.create(ca, buildRule());
        assertThrows(IllegalArgumentException.class, () -> runner.cellSize(0));
        assertThrows(IllegalArgumentException.class, () -> runner.cellSize(-5));
    }

    @Test
    @DisplayName("CellularAutomataUIRunner.delay() rejects negative values")
    void runnerRejectsNegativeDelay() throws Exception {
        CellularAutomata ca = buildCa(3, 3, 1);
        CellularAutomataUIRunner runner = CellularAutomataUIRunner.create(ca, buildRule());
        assertThrows(IllegalArgumentException.class, () -> runner.delay(-1));
    }

    // ── helpers ───────────────────────────────────────────────────────────

    private CellularAutomata buildCa(int w, int h, int iters) throws Exception {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setWidth(w).setHeight(h)
                .setTotalIterations(iters)
                .setDefaultStatus(DEAD)
                .setNeighborhoodType(NeighborhoodType.MOORE)
                .build();
        return new CellularAutomata(cfg);
    }

    private CellularAutomataRule buildRule() {
        return new CellularAutomataRule() {
            @Override
            public Cell transition(Cell cell, List<Cell> neighbors) {
                return new Cell(cell.getCurrentStatus(), cell.getCoordinates());
            }
        };
    }
}
