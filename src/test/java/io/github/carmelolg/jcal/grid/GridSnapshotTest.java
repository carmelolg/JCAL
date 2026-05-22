package io.github.carmelolg.jcal.grid;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link GridSnapshot}.
 */
@DisplayName("GridSnapshot")
class GridSnapshotTest {

    private static final CellState DEAD  = new CellState("dead",  "0");
    private static final CellState ALIVE = new CellState("alive", "1");

    private CellGrid buildGrid2D(int cols, int rows) {
        GridDimensions dims = new GridDimensions(cols, rows);
        CellGrid grid = new CellGrid(dims);
        for (int[] c : grid.allCoordinates()) {
            grid.set(c, new Cell(DEAD, c));
        }
        return grid;
    }

    // ── factory ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("of() stores the correct generation number")
    void storesGeneration() {
        CellGrid grid = buildGrid2D(3, 3);
        GridSnapshot snap = GridSnapshot.of(7, grid);
        assertEquals(7, snap.getGeneration());
    }

    @Test
    @DisplayName("of() stores the correct dimensions")
    void storesDimensions() {
        CellGrid grid = buildGrid2D(4, 5);
        GridSnapshot snap = GridSnapshot.of(0, grid);
        assertArrayEquals(new int[]{4, 5}, snap.getDimensions().sizes());
    }

    @Test
    @DisplayName("getCellStates() has one entry per cell")
    void cellStatesSize() {
        CellGrid grid = buildGrid2D(3, 4);
        GridSnapshot snap = GridSnapshot.of(0, grid);
        assertEquals(12, snap.getCellStates().size());
    }

    @Test
    @DisplayName("getCellStates() is unmodifiable")
    void cellStatesIsUnmodifiable() {
        CellGrid grid = buildGrid2D(2, 2);
        GridSnapshot snap = GridSnapshot.of(0, grid);
        List<CellState> states = snap.getCellStates();
        assertThrows(UnsupportedOperationException.class,
                () -> states.add(ALIVE));
    }

    // ── getState (nD) ─────────────────────────────────────────────────────

    @Test
    @DisplayName("getState(int[]) returns DEAD for an all-dead grid")
    void getStateNdAllDead() {
        CellGrid grid = buildGrid2D(3, 3);
        GridSnapshot snap = GridSnapshot.of(1, grid);
        assertEquals(DEAD, snap.getState(new int[]{1, 1}));
    }

    @Test
    @DisplayName("getState(int[]) reflects the state at capture time")
    void getStateNdReflectsGrid() {
        CellGrid grid = buildGrid2D(3, 3);
        grid.set(new int[]{2, 1}, new Cell(ALIVE, 2, 1));
        GridSnapshot snap = GridSnapshot.of(1, grid);
        assertEquals(ALIVE, snap.getState(new int[]{2, 1}));
        assertEquals(DEAD,  snap.getState(new int[]{0, 0}));
    }

    // ── getState (2D convenience) ─────────────────────────────────────────

    @Test
    @DisplayName("getState(col, row) is equivalent to getState(int[])")
    void getState2DEquivalent() {
        CellGrid grid = buildGrid2D(4, 4);
        grid.set(new int[]{3, 2}, new Cell(ALIVE, 3, 2));
        GridSnapshot snap = GridSnapshot.of(0, grid);
        assertEquals(snap.getState(new int[]{3, 2}), snap.getState(3, 2));
    }

    // ── snapshot isolation ────────────────────────────────────────────────

    @Test
    @DisplayName("mutating the grid after snapshot does not change the snapshot")
    void snapshotIsIsolatedFromGridMutations() {
        CellGrid grid = buildGrid2D(3, 3);
        GridSnapshot snap = GridSnapshot.of(1, grid);
        // mutate the grid
        grid.set(new int[]{0, 0}, new Cell(ALIVE, 0, 0));
        // snapshot must still show DEAD at (0,0)
        assertEquals(DEAD, snap.getState(0, 0),
                "Snapshot must not reflect mutations made after capture");
    }

    // ── 3D snapshot ────────────────────────────────────────────────────────

    @Test
    @DisplayName("of() works correctly on a 3D grid")
    void snapshot3D() {
        GridDimensions dims = new GridDimensions(2, 2, 2);
        CellGrid grid = new CellGrid(dims);
        for (int[] c : grid.allCoordinates()) {
            grid.set(c, new Cell(DEAD, c));
        }
        grid.set(new int[]{1, 1, 1}, new Cell(ALIVE, 1, 1, 1));

        GridSnapshot snap = GridSnapshot.of(3, grid);
        assertEquals(3, snap.getGeneration());
        assertEquals(8, snap.getCellStates().size());
        assertEquals(ALIVE, snap.getState(new int[]{1, 1, 1}));
        assertEquals(DEAD,  snap.getState(new int[]{0, 0, 0}));
    }
}
