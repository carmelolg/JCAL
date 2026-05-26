package io.github.carmelolg.jcal.grid;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CellGrid")
class CellGridTest {

    private static final CellState ALIVE = new CellState("alive", "1");
    private static final CellState DEAD  = new CellState("dead",  "0");

    private Cell[][] make2DMatrix(int rows, int cols) {
        Cell[][] m = new Cell[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                m[i][j] = new Cell(DEAD, i, j);
        return m;
    }

    // ── 2D matrix constructor ──────────────────────────────────────────────

    @Test
    @DisplayName("2D matrix constructor: dimensions match")
    void matrixConstructorDimensions() {
        CellGrid g = new CellGrid(make2DMatrix(3, 4));
        assertEquals(3, g.dimensions().getSize(0));
        assertEquals(4, g.dimensions().getSize(1));
    }

    @Test
    @DisplayName("2D matrix constructor: is2D returns true")
    void matrixConstructorIs2D() {
        assertTrue(new CellGrid(make2DMatrix(5, 5)).is2D());
    }

    @Test
    @DisplayName("2D matrix constructor: cells accessible via get(row, col)")
    void matrixConstructorCellsAccessible() {
        Cell[][] m = make2DMatrix(2, 3);
        m[1][2] = new Cell(ALIVE, 1, 2);
        CellGrid g = new CellGrid(m);
        assertEquals(ALIVE, g.get(1, 2).getCurrentStatus());
    }

    // ── nD GridDimensions constructor ──────────────────────────────────────

    @Test
    @DisplayName("3D GridDimensions constructor: is2D returns false")
    void ndConstructorIs2DFalse() {
        CellGrid g = new CellGrid(new GridDimensions(3, 3, 3));
        assertFalse(g.is2D());
    }

    // ── get / set ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("set then get round-trips the cell")
    void setAndGet() {
        CellGrid g = new CellGrid(new GridDimensions(4, 4));
        Cell c = new Cell(ALIVE, 2, 3);
        g.set(new int[]{2, 3}, c);
        assertSame(c, g.get(2, 3));
    }

    // ── allCoordinates ─────────────────────────────────────────────────────

    @Test
    @DisplayName("allCoordinates: 2x3 grid produces 6 coordinates")
    void allCoordinatesCount() {
        CellGrid g = new CellGrid(new GridDimensions(2, 3));
        List<int[]> coords = g.allCoordinates();
        assertEquals(6, coords.size());
    }

    @Test
    @DisplayName("allCoordinates: second call returns cached list")
    void allCoordinatesCached() {
        CellGrid g = new CellGrid(new GridDimensions(3, 3));
        assertSame(g.allCoordinates(), g.allCoordinates());
    }

    @Test
    @DisplayName("allCoordinates: list is unmodifiable")
    void allCoordinatesUnmodifiable() {
        CellGrid g = new CellGrid(new GridDimensions(2, 2));
        assertThrows(UnsupportedOperationException.class,
                () -> g.allCoordinates().add(new int[]{0, 0}));
    }

    @Test
    @DisplayName("allCoordinates: 3D grid enumerates all positions")
    void allCoordinates3D() {
        CellGrid g = new CellGrid(new GridDimensions(2, 2, 2));
        assertEquals(8, g.allCoordinates().size());
    }

    // ── dimensions ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("dimensions: returns the GridDimensions provided at construction")
    void dimensionsReturned() {
        GridDimensions dims = new GridDimensions(5, 6);
        CellGrid g = new CellGrid(dims);
        assertSame(dims, g.dimensions());
    }

    @Test
    @DisplayName("2D matrix constructor: null matrix throws IllegalArgumentException")
    void matrixConstructorNullThrows() {
        Cell[][] nullMatrix = null;
        assertThrows(IllegalArgumentException.class, () -> new CellGrid(nullMatrix));
    }

    @Test
    @DisplayName("2D matrix constructor: empty matrix throws IllegalArgumentException")
    void matrixConstructorEmptyThrows() {
        assertThrows(IllegalArgumentException.class, () -> new CellGrid((Cell[][]) new Cell[0][]));
    }

    @Test
    @DisplayName("2D matrix constructor: jagged matrix throws IllegalArgumentException")
    void matrixConstructorJaggedThrows() {
        Cell[][] jagged = new Cell[2][];
        jagged[0] = new Cell[]{new Cell(DEAD, 0, 0), new Cell(DEAD, 0, 1)};
        jagged[1] = new Cell[]{new Cell(DEAD, 1, 0)};
        assertThrows(IllegalArgumentException.class, () -> new CellGrid(jagged));
    }
}
