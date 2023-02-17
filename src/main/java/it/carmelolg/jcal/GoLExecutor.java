package it.carmelolg.jcal;

import java.util.List;

import it.carmelolg.jcal.core.CellularAutomataExecutor;
import it.carmelolg.jcal.model.DefaultCell;

public class GoLExecutor extends CellularAutomataExecutor {

	@Override
	public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) throws CloneNotSupportedException {

		Long alives = neighbors.stream().filter(item -> item.currentStatus.equals("1")).count();

		DefaultCell toReturn = cell.clone();
		
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
