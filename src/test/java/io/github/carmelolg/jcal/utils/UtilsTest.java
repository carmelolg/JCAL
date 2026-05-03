package io.github.carmelolg.jcal.utils;

import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.grid.GridDimensions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Utils")
class UtilsTest {

    private static final CellState DEAD = new CellState("dead", "0");

    private Cell[][] make(int rows, int cols) {
        Cell[][] m = new Cell[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                m[i][j] = new Cell(DEAD, i, j);
        return m;
    }

    @Test
    @DisplayName("Utils can be instantiated (default constructor coverage)")
    void utilsConstructor() {
        assertNotNull(new Utils());
    }

    // ── isInside(Cell[][], int, int) ───────────────────────────────────────

    @Test
    @DisplayName("isInside(matrix): interior position returns true")
    void isInsideMatrixInterior() {
        assertTrue(Utils.isInside(make(5, 5), 2, 3));
    }

    @Test
    @DisplayName("isInside(matrix): position at 0,0 returns true")
    void isInsideMatrixOrigin() {
        assertTrue(Utils.isInside(make(5, 5), 0, 0));
    }

    @Test
    @DisplayName("isInside(matrix): negative col returns false")
    void isInsideMatrixNegativeCol() {
        assertFalse(Utils.isInside(make(5, 5), -1, 2));
    }

    @Test
    @DisplayName("isInside(matrix): negative row returns false")
    void isInsideMatrixNegativeRow() {
        assertFalse(Utils.isInside(make(5, 5), 2, -1));
    }

    @Test
    @DisplayName("isInside(matrix): col == length returns false")
    void isInsideMatrixColAtLength() {
        assertFalse(Utils.isInside(make(5, 5), 5, 2));
    }

    @Test
    @DisplayName("isInside(matrix): row == width returns false")
    void isInsideMatrixRowAtWidth() {
        assertFalse(Utils.isInside(make(5, 5), 2, 5));
    }

    // ── isInside(int[], int[]) ─────────────────────────────────────────────

    @Test
    @DisplayName("isInside(sizes, coords): all dimensions in range returns true")
    void isInsideSizesInRange() {
        assertTrue(Utils.isInside(new int[]{3, 4, 5}, new int[]{1, 2, 3}));
    }

    @Test
    @DisplayName("isInside(sizes, coords): one dimension out of range returns false")
    void isInsideSizesOutOfRange() {
        assertFalse(Utils.isInside(new int[]{3, 4}, new int[]{3, 0}));
    }

    @Test
    @DisplayName("isInside(sizes, coords): negative coordinate returns false")
    void isInsideSizesNegative() {
        assertFalse(Utils.isInside(new int[]{3, 4}, new int[]{-1, 0}));
    }

    @Test
    @DisplayName("isInside(sizes, coords): length mismatch returns false")
    void isInsideSizesLengthMismatch() {
        assertFalse(Utils.isInside(new int[]{3, 4}, new int[]{1, 2, 0}));
    }

    // ── cloneGrid ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("cloneGrid: produces a deep copy with same dimensions")
    void cloneGridDimensions() throws CloneNotSupportedException {
        CellGrid original = new CellGrid(new GridDimensions(3, 3));
        for (int[] c : original.allCoordinates())
            original.set(c, new Cell(DEAD, c));
        CellGrid copy = Utils.cloneGrid(original);
        assertEquals(original.dimensions().getDimensionCount(),
                copy.dimensions().getDimensionCount());
        assertEquals(original.dimensions().getTotalCells(),
                copy.dimensions().getTotalCells());
    }

    @Test
    @DisplayName("cloneGrid: mutating a cell in the copy does not affect original")
    void cloneGridIsolation() throws CloneNotSupportedException {
        CellGrid original = new CellGrid(new GridDimensions(2, 2));
        CellState alive = new CellState("alive", "1");
        for (int[] c : original.allCoordinates())
            original.set(c, new Cell(DEAD, c));

        CellGrid copy = Utils.cloneGrid(original);
        copy.get(0, 0).setCurrentStatus(alive);

        assertEquals(DEAD, original.get(0, 0).getCurrentStatus());
    }
}
