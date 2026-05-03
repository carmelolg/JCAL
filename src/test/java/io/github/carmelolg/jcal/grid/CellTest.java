package io.github.carmelolg.jcal.grid;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cell")
class CellTest {

    private static final CellState ALIVE = new CellState("alive", "1");
    private static final CellState DEAD  = new CellState("dead",  "0");

    @Test
    @DisplayName("2D constructor stores col and row")
    void twoDConstructor() {
        Cell c = new Cell(ALIVE, 3, 7);
        assertEquals(ALIVE, c.getCurrentStatus());
        assertEquals(3, c.getCol());
        assertEquals(7, c.getRow());
    }

    @Test
    @DisplayName("nD constructor (3 coords) stores all coordinates")
    void ndConstructorThreeCoords() {
        Cell c = new Cell(ALIVE, 1, 2, 3);
        assertArrayEquals(new int[]{1, 2, 3}, c.getCoordinates());
    }

    @Test
    @DisplayName("getCoordinates returns a defensive copy")
    void getCoordinatesDefensiveCopy() {
        Cell c = new Cell(ALIVE, 2, 4);
        int[] coords = c.getCoordinates();
        coords[0] = 99;
        assertEquals(2, c.getCoordinates()[0]);
    }

    @Test
    @DisplayName("setCurrentStatus changes the status")
    void setCurrentStatus() {
        Cell c = new Cell(ALIVE, 0, 0);
        c.setCurrentStatus(DEAD);
        assertEquals(DEAD, c.getCurrentStatus());
    }

    @Test
    @DisplayName("toString returns status + space")
    void toStringDelegatesToStatus() {
        Cell c = new Cell(ALIVE, 0, 0);
        assertEquals(ALIVE + " ", c.toString());
    }

    @Test
    @DisplayName("clone produces equal but distinct instance")
    void cloneProducesEqualInstance() throws CloneNotSupportedException {
        Cell original = new Cell(ALIVE, 2, 5);
        Cell copy = original.clone();
        assertNotSame(original, copy);
        assertEquals(original, copy);
    }

    @Test
    @DisplayName("equals: same reference")
    void equalsSameReference() {
        Cell c = new Cell(ALIVE, 1, 1);
        assertEquals(c, c);
    }

    @Test
    @DisplayName("equals: structurally equal instances")
    void equalsStructurallyEqual() {
        assertEquals(new Cell(ALIVE, 3, 4), new Cell(ALIVE, 3, 4));
    }

    @Test
    @DisplayName("equals: different coordinates → not equal")
    void equalsDifferentCoords() {
        assertNotEquals(new Cell(ALIVE, 1, 2), new Cell(ALIVE, 1, 3));
    }

    @Test
    @DisplayName("equals: different status → not equal")
    void equalsDifferentStatus() {
        assertNotEquals(new Cell(ALIVE, 1, 1), new Cell(DEAD, 1, 1));
    }

    @Test
    @DisplayName("equals: null → not equal")
    void equalsNull() {
        assertNotEquals(null, new Cell(ALIVE, 0, 0));
    }

    @Test
    @DisplayName("equals: different type → not equal")
    void equalsDifferentType() {
        Cell c = new Cell(ALIVE, 0, 0);
        assertNotEquals(c, "not a cell");
    }

    @Test
    @DisplayName("hashCode: equal objects have same hash")
    void hashCodeConsistency() {
        Cell a = new Cell(ALIVE, 1, 2);
        Cell b = new Cell(ALIVE, 1, 2);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
