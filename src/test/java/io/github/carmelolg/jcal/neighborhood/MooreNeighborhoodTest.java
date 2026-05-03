package io.github.carmelolg.jcal.neighborhood;

import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.grid.GridDimensions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MooreNeighborhood")
class MooreNeighborhoodTest {

    private static final CellState S = new CellState("s", "0");

    private CellGrid filled(int rows, int cols) {
        CellGrid g = new CellGrid(new GridDimensions(rows, cols));
        for (int[] c : g.allCoordinates()) g.set(c, new Cell(S, c));
        return g;
    }

    @Test
    @DisplayName("center cell of 3x3 grid has 8 neighbors")
    void centerCellHasEightNeighbors() {
        CellGrid g = filled(3, 3);
        List<Cell> n = new MooreNeighborhood().getNeighbors(g, new int[]{1, 1});
        assertEquals(8, n.size());
    }

    @Test
    @DisplayName("corner cell (0,0) of 3x3 grid has 3 neighbors")
    void cornerCellHasThreeNeighbors() {
        CellGrid g = filled(3, 3);
        List<Cell> n = new MooreNeighborhood().getNeighbors(g, new int[]{0, 0});
        assertEquals(3, n.size());
    }

    @Test
    @DisplayName("edge cell (0,1) of 3x3 grid has 5 neighbors")
    void edgeCellHasFiveNeighbors() {
        CellGrid g = filled(3, 3);
        List<Cell> n = new MooreNeighborhood().getNeighbors(g, new int[]{0, 1});
        assertEquals(5, n.size());
    }

    @Test
    @DisplayName("throws UnsupportedOperationException on 3D grid")
    void throwsOn3DGrid() {
        CellGrid g = new CellGrid(new GridDimensions(3, 3, 3));
        assertThrows(UnsupportedOperationException.class,
                () -> new MooreNeighborhood().getNeighbors(g, new int[]{1, 1, 1}));
    }
}
