package io.github.carmelolg.jcal.utils;

import io.github.carmelolg.jcal.grid.CellGrid;
import io.github.carmelolg.jcal.grid.Cell;

/**
 * Internal utility methods used by the JCAL framework.
 *
 * <p>These helpers are used throughout the library but are not part of the
 * public extension API.  Callers may use them when implementing custom
 * {@link io.github.carmelolg.jcal.neighborhood.Neighborhood} classes.
 *
 * @author Carmelo La Gamba
 */
public final class Utils {

    private Utils() {}

    /**
     * Checks if the given coordinates are inside a grid with the given sizes.
     *
     * @param sizes  the sizes of each dimension
     * @param coords the coordinates to check
     * @return {@code true} if all {@code coords[i]} are in {@code [0, sizes[i])}
     */
    public static boolean isInside(int[] sizes, int[] coords) {
        if (coords.length != sizes.length) return false;
        for (int i = 0; i < coords.length; i++)
            if (coords[i] < 0 || coords[i] >= sizes[i]) return false;
        return true;
    }

    /**
     * Clones a {@link CellGrid} by deep-copying all cells.
     *
     * @param grid the grid to clone
     * @return a deep copy of the grid
     * @throws CloneNotSupportedException if any cell cannot be cloned
     */
    public static CellGrid cloneGrid(CellGrid grid) throws CloneNotSupportedException {
        CellGrid copy = new CellGrid(grid.dimensions());
        for (int[] coords : grid.allCoordinates())
            copy.set(coords, grid.get(coords).clone());
        return copy;
    }
}
