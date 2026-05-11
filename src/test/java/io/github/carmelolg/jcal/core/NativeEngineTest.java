package io.github.carmelolg.jcal.core;

import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.neighborhood.NeighborhoodType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the native Rust execution path via {@link NativeEngine} / {@link NativeRule}.
 *
 * <p>All tests are guarded by {@code NativeEngine.isAvailable()}.  When the native
 * library is not on the path the tests pass vacuously (they are informational only).
 */
@DisplayName("NativeEngine")
class NativeEngineTest {

    private static final CellState DEAD  = new CellState("dead",  "0");
    private static final CellState ALIVE = new CellState("alive", "1");

    /** Minimal executor that delegates to the native path. */
    private static class NativeGoLExecutor extends CellularAutomataExecutor {
        @Override
        public Cell singleRun(Cell cell, List<Cell> neighbors) {
            // Never called on the native path; required by abstract contract.
            return cell;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Library loading
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("isAvailable() returns a definite boolean without throwing")
    void isAvailableNeverThrows() {
        assertDoesNotThrow(NativeEngine::isAvailable);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Block still life — should be stable after N steps
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GoL 2-D block (2x2) is a still life — native path")
    void blockStillLifeNative() throws Exception {
        if (!NativeEngine.isAvailable()) return;

        // 4x4 grid, 2x2 block in the centre
        List<Cell> initial = Arrays.asList(
                new Cell(ALIVE, new int[]{1, 1}),
                new Cell(ALIVE, new int[]{1, 2}),
                new Cell(ALIVE, new int[]{2, 1}),
                new Cell(ALIVE, new int[]{2, 2})
        );

        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setWidth(4).setHeight(4)
                .setTotalIterations(5)
                .setDefaultStatus(DEAD)
                .setNeighborhoodType(NeighborhoodType.MOORE)
                .setInitalState(initial)
                .useNativeRule(NativeRule.GAME_OF_LIFE_2D)
                .build();

        CellularAutomata ca = new CellularAutomata(cfg);
        new NativeGoLExecutor().run(ca);

        // Centre 2x2 must remain alive
        assertEquals(ALIVE, ca.getGrid().get(new int[]{1, 1}).getCurrentStatus(), "(1,1) must stay alive");
        assertEquals(ALIVE, ca.getGrid().get(new int[]{1, 2}).getCurrentStatus(), "(1,2) must stay alive");
        assertEquals(ALIVE, ca.getGrid().get(new int[]{2, 1}).getCurrentStatus(), "(2,1) must stay alive");
        assertEquals(ALIVE, ca.getGrid().get(new int[]{2, 2}).getCurrentStatus(), "(2,2) must stay alive");
        // Corners must remain dead
        assertEquals(DEAD, ca.getGrid().get(new int[]{0, 0}).getCurrentStatus(), "(0,0) must stay dead");
        assertEquals(DEAD, ca.getGrid().get(new int[]{3, 3}).getCurrentStatus(), "(3,3) must stay dead");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Blinker oscillator — period 2
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GoL 2-D blinker returns to original after 2 steps — native path")
    void blinkerOscillatesNative() throws Exception {
        if (!NativeEngine.isAvailable()) return;

        // 5x5 grid, horizontal blinker row=2 cols 1-3
        List<Cell> initial = Arrays.asList(
                new Cell(ALIVE, new int[]{2, 1}),
                new Cell(ALIVE, new int[]{2, 2}),
                new Cell(ALIVE, new int[]{2, 3})
        );

        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setWidth(5).setHeight(5)
                .setTotalIterations(2)
                .setDefaultStatus(DEAD)
                .setNeighborhoodType(NeighborhoodType.MOORE)
                .setInitalState(initial)
                .useNativeRule(NativeRule.GAME_OF_LIFE_2D)
                .build();

        CellularAutomata ca = new CellularAutomata(cfg);
        new NativeGoLExecutor().run(ca);

        // After 2 steps the horizontal blinker is back
        assertEquals(ALIVE, ca.getGrid().get(new int[]{2, 1}).getCurrentStatus(), "(2,1) must be alive");
        assertEquals(ALIVE, ca.getGrid().get(new int[]{2, 2}).getCurrentStatus(), "(2,2) must be alive");
        assertEquals(ALIVE, ca.getGrid().get(new int[]{2, 3}).getCurrentStatus(), "(2,3) must be alive");
        assertEquals(DEAD,  ca.getGrid().get(new int[]{2, 0}).getCurrentStatus(), "(2,0) must be dead");
        assertEquals(DEAD,  ca.getGrid().get(new int[]{2, 4}).getCurrentStatus(), "(2,4) must be dead");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // NativeRule enum contract
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("NativeRule IDs are positive and unique")
    void nativeRuleIdsAreUniqueAndPositive() {
        NativeRule[] rules = NativeRule.values();
        long distinctIds = Arrays.stream(rules).mapToInt(NativeRule::getId).distinct().count();
        assertEquals(rules.length, distinctIds, "NativeRule IDs must be unique");
        for (NativeRule r : rules) {
            assertTrue(r.getId() > 0, "NativeRule ID must be > 0 for " + r);
        }
    }
}
