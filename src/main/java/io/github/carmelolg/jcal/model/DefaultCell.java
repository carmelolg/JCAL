package io.github.carmelolg.jcal.model;

/**
 * Represents a single cell in the Cellular Automata grid.
 *
 * <p>Each cell has:
 * <ul>
 *   <li>a {@link DefaultStatus} holding the cell's current state (e.g. dead/alive,
 *       a temperature level, etc.)</li>
 *   <li>grid coordinates stored as an {@code int[]} array that identify its position
 *       in the matrix (supports 2D, 3D, and 4D grids)</li>
 * </ul>
 *
 * <p>{@code DefaultCell} implements {@link Cloneable} so the library can take safe snapshots
 * of the grid before applying the transition function.
 *
 * <p><b>Extending cell state:</b> if you need richer per-cell data, create a custom
 * {@link DefaultStatus} subclass and store it in {@link #currentStatus}.  You do not
 * need to subclass {@code DefaultCell} itself.
 *
 * @author Carmelo La Gamba
 * @see DefaultStatus
 * @see io.github.carmelolg.jcal.core.CellularAutomata
 */
public class DefaultCell implements Cloneable {

	private DefaultStatus currentStatus;
	private final int[] coordinates;

	/**
	 * Backward-compatible 2D constructor.
	 *
	 * @param currentStatus the cell's initial status
	 * @param col the column coordinate (x-axis)
	 * @param row the row coordinate (y-axis)
	 */
	public DefaultCell(DefaultStatus currentStatus, int col, int row) {
		super();
		this.currentStatus = currentStatus;
		this.coordinates = new int[]{col, row};
	}

	/**
	 * N-dimensional constructor. Use for 3D+ cells:
	 * {@code new DefaultCell(status, x, y, z)}.
	 *
	 * @param currentStatus the cell's initial status
	 * @param coords the coordinates for each dimension
	 */
	public DefaultCell(DefaultStatus currentStatus, int... coords) {
		super();
		this.currentStatus = currentStatus;
		this.coordinates = coords.clone();
	}

	/**
	 * Returns the current status of this cell.
	 * @return the {@link DefaultStatus}
	 */
	public DefaultStatus getCurrentStatus() {
		return currentStatus;
	}

	/**
	 * Sets the current status of this cell.
	 * @param currentStatus the new {@link DefaultStatus}
	 */
	public void setCurrentStatus(DefaultStatus currentStatus) {
		this.currentStatus = currentStatus;
	}

	/**
	 * Returns the column coordinate (dimension 0).
	 * @return the column index
	 */
	public int getCol() {
		return coordinates[0];
	}

	/**
	 * Returns the row coordinate (dimension 1).
	 * @return the row index
	 */
	public int getRow() {
		return coordinates[1];
	}

	/**
	 * Returns a copy of the full coordinates array.
	 * @return a defensive copy of the coordinates
	 */
	public int[] getCoordinates() {
		return coordinates.clone();
	}

	@Override
	public String toString() {
		return currentStatus + " ";
	}

	@Override
	public DefaultCell clone() throws CloneNotSupportedException {
		return new DefaultCell(currentStatus, coordinates.clone());
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof DefaultCell dc)) return false;
		return java.util.Arrays.equals(dc.coordinates, this.coordinates)
				&& java.util.Objects.equals(dc.currentStatus, this.currentStatus);
	}

	@Override
	public int hashCode() {
		return 31 * java.util.Arrays.hashCode(coordinates)
				+ java.util.Objects.hashCode(currentStatus);
	}
}