package io.github.carmelolg.jcal.grid;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CellState")
class CellStateTest {

    @Test
    @DisplayName("constructor stores key and value")
    void constructorStoresKeyAndValue() {
        CellState s = new CellState("alive", "1");
        assertEquals("alive", s.getKey());
        assertEquals("1", s.getValue());
    }

    @Test
    @DisplayName("value can be any object")
    void valueCanBeAnyObject() {
        Object payload = java.util.Map.of("temp", 42);
        CellState s = new CellState("hot", payload);
        assertSame(payload, s.getValue());
    }

    @Test
    @DisplayName("equals: same reference")
    void equalsSameReference() {
        CellState s = new CellState("dead", "0");
        assertEquals(s, s);
    }

    @Test
    @DisplayName("equals: structurally equal instances")
    void equalsStructurallyEqual() {
        CellState a = new CellState("alive", "1");
        CellState b = new CellState("alive", "1");
        assertEquals(a, b);
    }

    @Test
    @DisplayName("equals: different key → not equal")
    void equalsDifferentKey() {
        assertNotEquals(new CellState("alive", "1"), new CellState("dead", "1"));
    }

    @Test
    @DisplayName("equals: different value → not equal")
    void equalsDifferentValue() {
        assertNotEquals(new CellState("alive", "1"), new CellState("alive", "0"));
    }

    @Test
    @DisplayName("equals: null → not equal")
    void equalsNull() {
        assertNotEquals(null, new CellState("alive", "1"));
    }

    @Test
    @DisplayName("equals: different type → not equal")
    void equalsDifferentType() {
        CellState s = new CellState("alive", "1");
        assertNotEquals(s, "alive");
    }

    @Test
    @DisplayName("hashCode: equal objects have equal hash codes")
    void hashCodeConsistency() {
        CellState a = new CellState("alive", "1");
        CellState b = new CellState("alive", "1");
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("toString returns value + space")
    void toStringReturnsValueAndSpace() {
        CellState s = new CellState("alive", "1");
        assertEquals("1 ", s.toString());
    }

    @Test
    @DisplayName("clone produces an equal but not same instance")
    void cloneProducesEqualInstance() throws CloneNotSupportedException {
        CellState s = new CellState("alive", "1");
        CellState copy = s.clone();
        assertNotSame(s, copy);
        assertEquals(s, copy);
    }
}
