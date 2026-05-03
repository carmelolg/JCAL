package io.github.carmelolg.jcal.neighborhood;

import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.grid.GridDimensions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VonNeumann3DNeighborhood")
class VonNeumann3DNeighborhoodTest {

    private static final CellState S = new CellState("s", "0");

    private CellGrid filled(int x, int y, int z) {
        CellGrid g = new CellGrid(new GridDimensions(x, y, z));
        for (int[] c : g.allCoordinates()) g.set(c, new Cell(S, c));
        return g;
    }

    @Test
    @DisplayName("center cell of 3x3x3 grid has 6 neighbors")
    void centerCellHasSixNeighbors() {
        CellGrid g = filled(3, 3, 3);
        List<Cell> n = new VonNeumann3DNeighborhood().getNeighbors(g, new int[]{1, 1, 1});
        assertEquals(6, n.size());
    }

    @Test
    @DisplayName("corner cell (0,0,0) has 3 neighbors")
    void cornerCellHasThreeNeighbors() {
        CellGrid g = filled(3, 3, 3);
        List<Cell> n = new VonNeumann3DNeighborhood().getNeighbors(g, new int[]{0, 0, 0});
        assertEquals(3, n.size());
    }

    @Test
    @DisplayName("face-center edge cell (0,1,1) has 5 neighbors")
    void faceCenterHasFiveNeighbors() {
        CellGrid g = filled(3, 3, 3);
        List<Cell> n = new VonNeumann3DNeighborhood().getNeighbors(g, new int[]{0, 1, 1});
        assertEquals(5, n.size());
    }
}
