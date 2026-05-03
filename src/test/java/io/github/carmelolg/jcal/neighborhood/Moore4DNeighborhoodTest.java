package io.github.carmelolg.jcal.neighborhood;

import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.grid.GridDimensions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Moore4DNeighborhood")
class Moore4DNeighborhoodTest {

    private static final CellState S = new CellState("s", "0");

    private CellGrid filled(int a, int b, int c, int d) {
        CellGrid g = new CellGrid(new GridDimensions(a, b, c, d));
        for (int[] coord : g.allCoordinates()) g.set(coord, new Cell(S, coord));
        return g;
    }

    @Test
    @DisplayName("center cell of 3x3x3x3 grid has 80 neighbors")
    void centerCellHas80Neighbors() {
        CellGrid g = filled(3, 3, 3, 3);
        List<Cell> n = new Moore4DNeighborhood().getNeighbors(g, new int[]{1, 1, 1, 1});
        assertEquals(80, n.size());
    }

    @Test
    @DisplayName("corner cell (0,0,0,0) has 15 neighbors")
    void cornerCellHas15Neighbors() {
        CellGrid g = filled(3, 3, 3, 3);
        List<Cell> n = new Moore4DNeighborhood().getNeighbors(g, new int[]{0, 0, 0, 0});
        assertEquals(15, n.size());
    }
}
