package io.github.carmelolg.jcal.neighborhood;

import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.grid.GridDimensions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VonNeumannNeighborhood")
class VonNeumannNeighborhoodTest {

    private static final CellState S = new CellState("s", "0");

    private CellGrid filled(int rows, int cols) {
        CellGrid g = new CellGrid(new GridDimensions(rows, cols));
        for (int[] c : g.allCoordinates()) g.set(c, new Cell(S, c));
        return g;
    }

    @Test
    @DisplayName("center cell of 3x3 grid has 4 neighbors")
    void centerCellHasFourNeighbors() {
        CellGrid g = filled(3, 3);
        List<Cell> n = new VonNeumannNeighborhood().getNeighbors(g, new int[]{1, 1});
        assertEquals(4, n.size());
    }

    @Test
    @DisplayName("corner cell (0,0) has 2 neighbors")
    void cornerCellHasTwoNeighbors() {
        CellGrid g = filled(3, 3);
        List<Cell> n = new VonNeumannNeighborhood().getNeighbors(g, new int[]{0, 0});
        assertEquals(2, n.size());
    }

    @Test
    @DisplayName("edge cell (0,1) has 3 neighbors")
    void edgeCellHasThreeNeighbors() {
        CellGrid g = filled(3, 3);
        List<Cell> n = new VonNeumannNeighborhood().getNeighbors(g, new int[]{0, 1});
        assertEquals(3, n.size());
    }

    @Test
    @DisplayName("throws UnsupportedOperationException on 3D grid")
    void throwsOn3DGrid() {
        CellGrid g = new CellGrid(new GridDimensions(3, 3, 3));
        assertThrows(UnsupportedOperationException.class,
                () -> new VonNeumannNeighborhood().getNeighbors(g, new int[]{1, 1, 1}));
    }
}
