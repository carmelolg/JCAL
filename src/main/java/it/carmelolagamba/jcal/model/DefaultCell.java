package it.carmelolagamba.jcal.model;

/**
 * @author Carmelo La Gamba
 * 
 * © 2023 is licensed under CC BY-NC-SA 4.0 
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