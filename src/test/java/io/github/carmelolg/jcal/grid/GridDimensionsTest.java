package io.github.carmelolg.jcal.grid;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GridDimensions")
class GridDimensionsTest {

    @Test
    @DisplayName("2D grid: basic construction succeeds")
    void twoDConstruction() {
        GridDimensions d = new GridDimensions(5, 10);
        assertEquals(2, d.getDimensionCount());
        assertEquals(5, d.getSize(0));
        assertEquals(10, d.getSize(1));
    }

    @Test
    @DisplayName("3D grid: construction succeeds")
    void threeDConstruction() {
        GridDimensions d = new GridDimensions(3, 4, 5);
        assertEquals(3, d.getDimensionCount());
        assertEquals(60, d.getTotalCells());
    }

    @Test
    @DisplayName("4D grid: construction succeeds")
    void fourDConstruction() {
        GridDimensions d = new GridDimensions(2, 3, 4, 5);
        assertEquals(4, d.getDimensionCount());
        assertEquals(120, d.getTotalCells());
    }

    @Test
    @DisplayName("1D grid: throws IllegalArgumentException")
    void oneDThrows() {
        assertThrows(IllegalArgumentException.class, () -> new GridDimensions(10));
    }

    @Test
    @DisplayName("5D grid: throws IllegalArgumentException")
    void fiveDThrows() {
        assertThrows(IllegalArgumentException.class, () -> new GridDimensions(2, 2, 2, 2, 2));
    }

    @Test
    @DisplayName("zero dimension size: throws IllegalArgumentException")
    void zeroSizeThrows() {
        assertThrows(IllegalArgumentException.class, () -> new GridDimensions(0, 5));
    }

    @Test
    @DisplayName("negative dimension size: throws IllegalArgumentException")
    void negativeSizeThrows() {
        assertThrows(IllegalArgumentException.class, () -> new GridDimensions(5, -1));
    }

    @Test
    @DisplayName("sizes() returns a defensive copy")
    void sizesDefensiveCopy() {
        GridDimensions d = new GridDimensions(3, 4);
        int[] s = d.sizes();
        s[0] = 99;
        assertEquals(3, d.getSize(0));
    }

    @Test
    @DisplayName("getTotalCells: 2D grid is width*height")
    void getTotalCells2D() {
        assertEquals(20, new GridDimensions(4, 5).getTotalCells());
    }

    @Test
    @DisplayName("computeStrides: 2D row-major strides")
    void computeStrides2D() {
        GridDimensions d = new GridDimensions(4, 5);
        int[] strides = d.computeStrides();
        assertArrayEquals(new int[]{5, 1}, strides);
    }

    @Test
    @DisplayName("computeStrides: 3D row-major strides")
    void computeStrides3D() {
        GridDimensions d = new GridDimensions(2, 3, 4);
        int[] strides = d.computeStrides();
        assertArrayEquals(new int[]{12, 4, 1}, strides);
    }
}
