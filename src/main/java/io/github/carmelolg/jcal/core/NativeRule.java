package io.github.carmelolg.jcal.core;

/**
 * Enumeration of built-in cellular automata rules implemented in the Rust core engine.
 *
 * <p>These rules bypass the Java transition loop and delegate to the native library
 * for maximum performance on large grids.  Only binary (two-state) automata are
 * supported; automata with custom multi-state rules fall back to the Java path
 * automatically.
 *
 * <p>Usage example:
 * <pre>{@code
 * CellularAutomataConfiguration cfg = new CellularAutomataConfigurationBuilder()
 *     .setWidth(1000).setHeight(1000)
 *     .setTotalIterations(100)
 *     .setDefaultStatus(DEAD)
 *     .setNeighborhoodType(NeighborhoodType.MOORE)
 *     .useNativeRule(NativeRule.GAME_OF_LIFE_2D)
 *     .build();
 * }</pre>
 *
 * @author Carmelo La Gamba
 * @see NativeEngine
 * @see NativeAutomaton
 */
public enum NativeRule {

    /** Conway's Game of Life on a 2-D Moore-neighbourhood grid. */
    GAME_OF_LIFE_2D(1, 2),

    /** Bays' Game of Life variant on a 3-D Moore-neighbourhood grid. */
    GAME_OF_LIFE_3D(2, 3),

    /**
     * Majority-vote rule: a cell is alive if more than half its neighbours are alive.
     * Works on 2-D, 3-D and 4-D grids — the engine selects the correct variant
     * based on the grid dimensionality.
     */
    VOTE(3, -1);

    /** Numeric ID passed to the native layer ({@code rule_id} in C ABI). */
    final int id;

    /** Expected number of dimensions, or {@code -1} if dimension-agnostic. */
    final int dimensions;

    NativeRule(int id, int dimensions) {
        this.id = id;
        this.dimensions = dimensions;
    }

    /**
     * Returns the numeric rule identifier used by the native engine.
     *
     * @return rule id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the expected number of grid dimensions, or {@code -1} if
     * the rule is valid for any dimensionality.
     *
     * @return expected dimensions
     */
    public int getDimensions() {
        return dimensions;
    }
}
