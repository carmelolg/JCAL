package io.github.carmelolg.jcal.core;

/**
 * Thin Java wrapper around an opaque Rust automaton handle.
 *
 * <p>The handle is allocated by the native engine via one of the
 * {@code jcalCreate*d} JNI methods and must be released exactly once
 * through {@link #close()} (or an equivalent {@code jcalFree*d} call).
 * Implements {@link AutoCloseable} so it can be used in try-with-resources:
 *
 * <pre>{@code
 * try (NativeAutomaton a = NativeEngine.create2d(rows, cols, nbhd, ruleId)) {
 *     a.initCells(states);
 *     a.run(steps);
 *     a.getGrid(out);
 * }
 * }</pre>
 *
 * <p>This class is <em>not</em> thread-safe.  All operations on the same
 * handle must be performed from the same thread.
 *
 * @author Carmelo La Gamba
 * @see NativeEngine
 */
public final class NativeAutomaton implements AutoCloseable {

    /** Opaque pointer value returned by the native layer. */
    private final long handle;

    /** Number of dimensions of the underlying grid (2, 3 or 4). */
    private final int dimensions;

    /** Total number of cells in the flat grid array (product of all dim sizes). */
    private final int cellCount;

    /** Whether the handle has already been freed. */
    private boolean closed = false;

    /**
     * Creates a new wrapper.  Called only from {@link NativeEngine}.
     *
     * @param handle    opaque Rust pointer, cast to {@code long}
     * @param dimensions grid dimensionality (2, 3 or 4)
     * @param cellCount  total number of cells ({@code d0 * d1 * …})
     */
    NativeAutomaton(long handle, int dimensions, int cellCount) {
        this.handle = handle;
        this.dimensions = dimensions;
        this.cellCount = cellCount;
    }

    /**
     * Initialises all cells from a flat state array.
     *
     * @param states flat {@code int[]} where {@code 0} = dead, {@code 1} = alive;
     *               length must equal {@link #getCellCount()}
     */
    public void initCells(int[] states) {
        ensureOpen();
        NativeEngine.initCells(this, states);
    }

    /**
     * Advances the automaton by the given number of steps.
     *
     * @param steps number of generations to compute (must be &gt; 0)
     */
    public void run(int steps) {
        ensureOpen();
        NativeEngine.run(this, steps);
    }

    /**
     * Copies the current grid state into the provided array.
     *
     * @param out destination array; length must equal {@link #getCellCount()}
     */
    public void getGrid(int[] out) {
        ensureOpen();
        NativeEngine.getGrid(this, out);
    }

    /**
     * Returns the opaque native handle.  For internal use by {@link NativeEngine} only.
     *
     * @return raw handle value
     */
    long getHandle() {
        return handle;
    }

    /**
     * Returns the grid dimensionality.
     *
     * @return 2, 3 or 4
     */
    public int getDimensions() {
        return dimensions;
    }

    /**
     * Returns the total number of cells in the flat grid.
     *
     * @return cell count
     */
    public int getCellCount() {
        return cellCount;
    }

    /**
     * Frees the underlying Rust automaton.  Idempotent — subsequent calls are no-ops.
     */
    @Override
    public void close() {
        if (!closed) {
            closed = true;
            NativeEngine.free(this);
        }
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("NativeAutomaton has already been closed");
        }
    }
}
