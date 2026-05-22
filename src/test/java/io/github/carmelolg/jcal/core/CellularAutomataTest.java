package io.github.carmelolg.jcal.core;

import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.neighborhood.MooreNeighborhood;
import io.github.carmelolg.jcal.neighborhood.Neighborhood;
import io.github.carmelolg.jcal.neighborhood.NeighborhoodType;
import io.github.carmelolg.jcal.neighborhood.NDCapable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CellularAutomata")
class CellularAutomataTest {

    private static final CellState DEAD  = new CellState("dead",  "0");
    private static final CellState ALIVE = new CellState("alive", "1");

    private CellularAutomataConfiguration basicConfig() {
        return new CellularAutomataConfiguration.CellularAutomataConfigurationBuilder()
                .setWidth(5).setHeight(5)
                .setTotalIterations(1)
                .setDefaultStatus(DEAD)
                .setNeighborhoodType(NeighborhoodType.MOORE)
                .build();
    }

    // ── init & getters ─────────────────────────────────────────────────────

    @Test
    @DisplayName("constructor with config initialises grid")
    void constructorWithConfig() throws Exception {
        CellularAutomata ca = new CellularAutomata(basicConfig());
        assertNotNull(ca.getGrid());
        assertNotNull(ca.getUtilsGrid());
        assertNotNull(ca.getNeighborhood());
        assertNotNull(ca.getConfig());
    }

    @Test
    @DisplayName("no-arg constructor + init works")
    void noArgConstructorThenInit() throws Exception {
        CellularAutomata ca = new CellularAutomata();
        ca.init(basicConfig());
        assertNotNull(ca.getGrid());
    }

    @Test
    @DisplayName("grid dimensions match configuration")
    void gridMatchesConfig() throws Exception {
        CellularAutomata ca = new CellularAutomata(basicConfig());
        assertEquals(5, ca.getGrid().dimensions().getSize(0));
        assertEquals(5, ca.getGrid().dimensions().getSize(1));
    }

    @Test
    @DisplayName("initial state cells override default status")
    void initialStateCellsApplied() throws Exception {
        List<Cell> initial = List.of(new Cell(ALIVE, 2, 3));
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setWidth(5).setHeight(5)
                .setTotalIterations(1)
                .setDefaultStatus(DEAD)
                .setInitalState(initial)
                .setNeighborhoodType(NeighborhoodType.MOORE)
                .build();
        CellularAutomata ca = new CellularAutomata(cfg);
        assertEquals(ALIVE, ca.getGrid().get(2, 3).getCurrentStatus());
    }

    @Test
    @DisplayName("custom neighborhood is used when set")
    void customNeighborhoodUsed() throws Exception {
        MooreNeighborhood custom = new MooreNeighborhood();
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setWidth(5).setHeight(5)
                .setTotalIterations(1)
                .setDefaultStatus(DEAD)
                .setNeighborhood(custom)
                .build();
        CellularAutomata ca = new CellularAutomata(cfg);
        assertSame(custom, ca.getNeighborhood());
    }

    @Test
    @DisplayName("setters update fields")
    void setters() throws Exception {
        CellularAutomata ca = new CellularAutomata(basicConfig());
        CellGrid newGrid = new CellGrid(ca.getGrid().dimensions());
        ca.setGrid(newGrid);
        assertSame(newGrid, ca.getGrid());

        CellGrid newUtils = new CellGrid(ca.getUtilsGrid().dimensions());
        ca.setUtilsGrid(newUtils);
        assertSame(newUtils, ca.getUtilsGrid());
    }

    // ── toString ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("toString on 2D grid produces rows separated by newlines")
    void toString2D() throws Exception {
        CellularAutomata ca = new CellularAutomata(basicConfig());
        String s = ca.toString();
        assertNotNull(s);
        assertTrue(s.contains("\n"));
    }

    @Test
    @DisplayName("toString on 3D grid produces output with newlines")
    void toString3D() throws Exception {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setDimensions(3, 3, 3)
                .setTotalIterations(1)
                .setDefaultStatus(DEAD)
                .setNeighborhoodType(NeighborhoodType.MOORE)
                .build();
        CellularAutomata ca = new CellularAutomata(cfg);
        String s = ca.toString();
        assertNotNull(s);
        assertTrue(s.contains("\n"));
    }

    // ── check() validation errors ──────────────────────────────────────────

    @Test
    @DisplayName("init throws when infinite=true and totalIterations>0")
    void checkInfiniteWithIterations() {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setWidth(5).setHeight(5)
                .setInfinite(true).setTotalIterations(5)
                .setDefaultStatus(DEAD)
                .setNeighborhoodType(NeighborhoodType.MOORE)
                .build();
        assertThrows(CellularAutomataException.class, () -> new CellularAutomata(cfg));
    }

    @Test
    @DisplayName("init throws when not infinite and totalIterations < 1")
    void checkNoIterationsNoInfinite() {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setWidth(5).setHeight(5)
                .setTotalIterations(0)
                .setDefaultStatus(DEAD)
                .setNeighborhoodType(NeighborhoodType.MOORE)
                .build();
        assertThrows(CellularAutomataException.class, () -> new CellularAutomata(cfg));
    }

    @Test
    @DisplayName("init throws when no neighborhood configured")
    void checkNoNeighborhood() {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setWidth(5).setHeight(5).setTotalIterations(1)
                .setDefaultStatus(DEAD)
                .build();
        assertThrows(CellularAutomataException.class, () -> new CellularAutomata(cfg));
    }

    @Test
    @DisplayName("init throws when both neighborhoodType and neighborhood are set")
    void checkBothNeighborhoods() {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setWidth(5).setHeight(5).setTotalIterations(1)
                .setDefaultStatus(DEAD)
                .setNeighborhoodType(NeighborhoodType.MOORE)
                .setNeighborhood(new MooreNeighborhood())
                .build();
        assertThrows(CellularAutomataException.class, () -> new CellularAutomata(cfg));
    }

    @Test
    @DisplayName("init throws when defaultStatus is null")
    void checkNoDefaultStatus() {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setWidth(5).setHeight(5).setTotalIterations(1)
                .setNeighborhoodType(NeighborhoodType.MOORE)
                .build();
        assertThrows(CellularAutomataException.class, () -> new CellularAutomata(cfg));
    }

    @Test
    @DisplayName("init throws when dimensions are 1D (< 2)")
    void checkTooFewDimensions() {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setDimensions(10)
                .setTotalIterations(1)
                .setDefaultStatus(DEAD)
                .setNeighborhoodType(NeighborhoodType.MOORE)
                .build();
        assertThrows(CellularAutomataException.class, () -> new CellularAutomata(cfg));
    }

    @Test
    @DisplayName("init throws when dimensions are 5D (> 4)")
    void checkTooManyDimensions() {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setDimensions(2, 2, 2, 2, 2)
                .setTotalIterations(1)
                .setDefaultStatus(DEAD)
                .setNeighborhoodType(NeighborhoodType.MOORE)
                .build();
        assertThrows(CellularAutomataException.class, () -> new CellularAutomata(cfg));
    }

    @Test
    @DisplayName("init throws when a dimension size is <= 0")
    void checkZeroDimensionSize() {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setDimensions(0, 5)
                .setTotalIterations(1)
                .setDefaultStatus(DEAD)
                .setNeighborhoodType(NeighborhoodType.MOORE)
                .build();
        assertThrows(CellularAutomataException.class, () -> new CellularAutomata(cfg));
    }

    @Test
    @DisplayName("init throws when custom 3D neighborhood does not implement NDCapable")
    void checkNonNDCapableNeighborhoodOn3D() {
        Neighborhood nonNd = new Neighborhood() {
            @Override
            public List<Cell> getNeighbors(CellGrid grid, int[] coords) {
                return List.of();
            }
        };
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setDimensions(3, 3, 3)
                .setTotalIterations(1)
                .setDefaultStatus(DEAD)
                .setNeighborhood(nonNd)
                .build();
        assertThrows(CellularAutomataException.class, () -> new CellularAutomata(cfg));
    }

    @Test
    @DisplayName("init throws when initial state cell coord count mismatches dimension count")
    void checkInitialStateWrongCoordCount() {
        List<Cell> initial = List.of(new Cell(ALIVE, 1, 2, 3));  // 3D cell in 2D grid
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setWidth(5).setHeight(5).setTotalIterations(1)
                .setDefaultStatus(DEAD)
                .setNeighborhoodType(NeighborhoodType.MOORE)
                .setInitalState(initial)
                .build();
        assertThrows(CellularAutomataException.class, () -> new CellularAutomata(cfg));
    }

    @Test
    @DisplayName("init throws when initial state cell coordinate is out of bounds")
    void checkInitialStateOutOfBounds() {
        List<Cell> initial = List.of(new Cell(ALIVE, 10, 10));  // outside 5x5
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setWidth(5).setHeight(5).setTotalIterations(1)
                .setDefaultStatus(DEAD)
                .setNeighborhoodType(NeighborhoodType.MOORE)
                .setInitalState(initial)
                .build();
        assertThrows(CellularAutomataException.class, () -> new CellularAutomata(cfg));
    }

    @Test
    @DisplayName("resolveNeighborhood default branch throws for unsupported dimension count")
    void resolveNeighborhoodDefaultThrows() throws Exception {
        CellularAutomata ca = new CellularAutomata();
        java.lang.reflect.Method m = CellularAutomata.class
                .getDeclaredMethod("resolveNeighborhood", NeighborhoodType.class, int.class);
        m.setAccessible(true);
        java.lang.reflect.InvocationTargetException ex = assertThrows(
                java.lang.reflect.InvocationTargetException.class,
                () -> m.invoke(ca, NeighborhoodType.MOORE, 5));
        assertInstanceOf(IllegalArgumentException.class, ex.getCause());
    }

    // ── 3D / 4D neighborhood resolution ──────────────────────────────────

    @Test
    @DisplayName("3D grid with MOORE type resolves Moore3DNeighborhood")
    void resolve3DMoore() throws Exception {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setDimensions(3, 3, 3).setTotalIterations(1)
                .setDefaultStatus(DEAD)
                .setNeighborhoodType(NeighborhoodType.MOORE)
                .build();
        CellularAutomata ca = new CellularAutomata(cfg);
        assertNotNull(ca.getNeighborhood());
    }

    @Test
    @DisplayName("3D grid with VON_NEUMANN type resolves VonNeumann3DNeighborhood")
    void resolve3DVonNeumann() throws Exception {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setDimensions(3, 3, 3).setTotalIterations(1)
                .setDefaultStatus(DEAD)
                .setNeighborhoodType(NeighborhoodType.VON_NEUMANN)
                .build();
        CellularAutomata ca = new CellularAutomata(cfg);
        assertNotNull(ca.getNeighborhood());
    }

    @Test
    @DisplayName("4D grid with MOORE type resolves Moore4DNeighborhood")
    void resolve4DMoore() throws Exception {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setDimensions(3, 3, 3, 3).setTotalIterations(1)
                .setDefaultStatus(DEAD)
                .setNeighborhoodType(NeighborhoodType.MOORE)
                .build();
        CellularAutomata ca = new CellularAutomata(cfg);
        assertNotNull(ca.getNeighborhood());
    }

    @Test
    @DisplayName("4D grid with VON_NEUMANN type resolves VonNeumann4DNeighborhood")
    void resolve4DVonNeumann() throws Exception {
        CellularAutomataConfiguration cfg = new CellularAutomataConfiguration
                .CellularAutomataConfigurationBuilder()
                .setDimensions(3, 3, 3, 3).setTotalIterations(1)
                .setDefaultStatus(DEAD)
                .setNeighborhoodType(NeighborhoodType.VON_NEUMANN)
                .build();
        CellularAutomata ca = new CellularAutomata(cfg);
        assertNotNull(ca.getNeighborhood());
    }
}
