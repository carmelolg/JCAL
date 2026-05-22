package io.github.carmelolg.jcal.grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An immutable snapshot of a {@link CellGrid} captured at a specific generation.
 *
 * <p>{@code GridSnapshot} is the bridge between the automaton engine and any external
 * rendering or persistence layer.  It stores the state of every cell (as a flat,
 * unmodifiable {@link List} of {@link CellState} values) together with the
 * {@link GridDimensions} needed to interpret the data.
 *
 * <p><b>Creating a snapshot:</b>
 * <pre>{@code
 * GridSnapshot snap = GridSnapshot.of(generation, ca.getGrid());
 * }</pre>
 *
 * <p><b>Reading cell states (2D convenience):</b>
 * <pre>{@code
 * CellState state = snap.getState(col, row);
 * }</pre>
 *
 * <p><b>Reading cell states (nD):</b>
 * <pre>{@code
 * CellState state = snap.getState(new int[]{x, y, z});
 * }</pre>
 *
 * <p>The flat list returned by {@link #getCellStates()} uses the same row-major
 * (stride) ordering as {@link CellGrid}: iterating the list visits cells in the
 * same order as {@link CellGrid#allCoordinates()}.
 *
 * @author Carmelo La Gamba
 * @see CellGrid
 * @see GridDimensions
 * @see io.github.carmelolg.jcal.core.GenerationListener
 */
public final class GridSnapshot {

    private final int generation;
    private final GridDimensions dimensions;
    private final List<CellState> states;
    private final int[] strides;

    private GridSnapshot(int generation, GridDimensions dimensions,
                         List<CellState> states, int[] strides) {
        this.generation = generation;
        this.dimensions = dimensions;
        this.states = states;
        this.strides = strides;
    }

    /**
     * Creates a {@code GridSnapshot} from the given grid at the specified generation.
     *
     * <p>The snapshot copies the {@link CellState} reference of each cell.  The states
     * themselves are not deep-copied; they are assumed to be effectively immutable value
     * objects (as {@link CellState} is designed to be).
     *
     * @param generation the generation index (0 = initial, 1 = after first step, …)
     * @param grid       the grid to snapshot
     * @return a new, immutable {@code GridSnapshot}
     */
    public static GridSnapshot of(int generation, CellGrid grid) {
        GridDimensions dims = grid.dimensions();
        List<CellState> cellStates = new ArrayList<>(dims.getTotalCells());
        for (int[] coords : grid.allCoordinates()) {
            cellStates.add(grid.get(coords).getCurrentStatus());
        }
        return new GridSnapshot(generation, dims,
                Collections.unmodifiableList(cellStates),
                dims.computeStrides());
    }

    /**
     * Returns the generation at which this snapshot was taken.
     *
     * @return the generation index
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * Returns the grid dimensions of this snapshot.
     *
     * @return the {@link GridDimensions}
     */
    public GridDimensions getDimensions() {
        return dimensions;
    }

    /**
     * Returns the flat, unmodifiable list of cell states in row-major order.
     *
     * <p>The list has {@link GridDimensions#getTotalCells()} elements and follows the
     * same ordering as {@link CellGrid#allCoordinates()}.
     *
     * @return unmodifiable list of {@link CellState}
     */
    public List<CellState> getCellStates() {
        return states;
    }

    /**
     * Returns the state of the cell at the given n-dimensional coordinates.
     *
     * @param coords one index per dimension
     * @return the {@link CellState} at those coordinates
     * @throws IndexOutOfBoundsException if the coordinates are out of range
     */
    public CellState getState(int[] coords) {
        int idx = 0;
        for (int i = 0; i < coords.length; i++) {
            idx += coords[i] * strides[i];
        }
        return states.get(idx);
    }

    /**
     * 2D convenience overload: returns the state of the cell at column {@code col},
     * row {@code row}.
     *
     * @param col the column (x-axis)
     * @param row the row (y-axis)
     * @return the {@link CellState} at that position
     */
    public CellState getState(int col, int row) {
        return getState(new int[]{col, row});
    }
}
