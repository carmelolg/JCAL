package io.github.carmelolg.jcal.model;

/**
 * Represents a single cell in the Cellular Automata grid.
 *
 * <p>Each cell has:
 * <ul>
 *   <li>a {@link DefaultStatus} holding the cell's current state (e.g. dead/alive,
 *       a temperature level, etc.)</li>
 *   <li>grid coordinates ({@code col}, {@code row}) that identify its position in the matrix</li>
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

	public DefaultStatus currentStatus;
	public int col, row;

	public DefaultCell(DefaultStatus currentStatus, int i, int j) {
		super();
		this.currentStatus = currentStatus;
		this.col = i;
		this.row = j;
	}

	public DefaultStatus getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(DefaultStatus currentStatus) {
		this.currentStatus = currentStatus;
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}

	@Override
	public String toString() {
		return currentStatus + " ";
	}

	@Override
	public DefaultCell clone() throws CloneNotSupportedException {
		return new DefaultCell(currentStatus, row, col);
	}

	@Override
	public boolean equals(Object object) {
		DefaultCell dc = (DefaultCell) object;
		return dc.col == this.col && dc.row == this.row && dc.currentStatus == this.currentStatus;

	}
}