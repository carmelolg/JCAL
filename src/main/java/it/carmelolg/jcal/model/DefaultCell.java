package it.carmelolg.jcal.model;

public class DefaultCell implements Cloneable {

	public String currentStatus;
	public int col, row;

	public DefaultCell(String currentStatus, int i, int j) {
		super();
		this.currentStatus = currentStatus;
		this.col = i;
		this.row = j;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
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