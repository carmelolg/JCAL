package it.carmelolg.jcal.core;

import java.util.List;

import it.carmelolg.jcal.model.DefaultCell;

public class GoLExecutor extends CellularAutomataExecutor {

	@Override
	public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {

		Long alives = neighbors.stream().filter(item -> item.currentStatus.equals("1")).count();

		DefaultCell toReturn = new DefaultCell(null, cell.getRow(), cell.getCol());
		
		if (cell.currentStatus.equals("0") && alives == 3) {
			toReturn.currentStatus = "1";
		} else if (cell.currentStatus.equals("1") && (alives == 2 || alives == 3)) {
			toReturn.currentStatus = "1";
		} else {
			toReturn.currentStatus = "0";
		}

		return toReturn;
	}

}
