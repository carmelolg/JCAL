package io.github.carmelolg.jcal.core;

import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.neighborhood.MooreNeighborhood;
import io.github.carmelolg.jcal.neighborhood.NeighborhoodType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CellularAutomataConfiguration")
class CellularAutomataConfigurationTest {

    private static final CellState DEAD = new CellState("dead", "0");

    @Test
    @DisplayName("default dimensions are 100x100")
    void defaultDimensions() {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder().build();
        assertEquals(100, cfg.getWidth());
        assertEquals(100, cfg.getHeight());
        assertArrayEquals(new int[]{100, 100}, cfg.getDimensions());
    }

    @Test
    @DisplayName("setWidth and setHeight override defaults")
    void setWidthAndHeight() {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setWidth(20).setHeight(30).build();
        assertEquals(20, cfg.getWidth());
        assertEquals(30, cfg.getHeight());
    }

    @Test
    @DisplayName("setDimensions stores 3D sizes")
    void setDimensions3D() {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setDimensions(4, 5, 6).build();
        assertArrayEquals(new int[]{4, 5, 6}, cfg.getDimensions());
    }

    @Test
    @DisplayName("getDimensions returns a defensive copy")
    void getDimensionsDefensiveCopy() {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setWidth(10).setHeight(10).build();
        int[] d = cfg.getDimensions();
        d[0] = 999;
        assertEquals(10, cfg.getWidth());
    }

    @Test
    @DisplayName("setTotalIterations stored correctly")
    void setTotalIterations() {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setTotalIterations(5).build();
        assertEquals(5, cfg.getTotalIterations());
    }

    @Test
    @DisplayName("setInfinite stored correctly")
    void setInfinite() {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setInfinite(true).build();
        assertTrue(cfg.isInfinite());
    }

    @Test
    @DisplayName("setDefaultStatus stored correctly")
    void setDefaultStatus() {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setDefaultStatus(DEAD).build();
        assertEquals(DEAD, cfg.getDefaultStatus());
    }

    @Test
    @DisplayName("setInitialState stores the list")
    void setInitialState() {
        List<Cell> cells = List.of(new Cell(DEAD, 0, 0));
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setInitialState(cells).build();
        assertEquals(cells, cfg.getInitialState());
    }

    @Test
    @DisplayName("setNeighborhoodType stored correctly")
    void setNeighborhoodType() {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setNeighborhoodType(NeighborhoodType.MOORE).build();
        assertEquals(NeighborhoodType.MOORE, cfg.getNeighborhoodType());
    }

    @Test
    @DisplayName("setNeighborhood (custom) stored correctly")
    void setCustomNeighborhood() {
        MooreNeighborhood custom = new MooreNeighborhood();
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setNeighborhood(custom).build();
        assertSame(custom, cfg.getNeighborhood());
        assertNull(cfg.getNeighborhoodType());
    }

    @Test
    @DisplayName("setActiveCells (deprecated) stored correctly")
    void setActiveCells() {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setActiveCells(true).build();
        assertTrue(cfg.getActiveCells());
    }
}
