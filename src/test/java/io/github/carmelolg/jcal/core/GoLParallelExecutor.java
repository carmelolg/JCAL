package io.github.carmelolg.jcal.core;

import java.util.List;

import io.github.carmelolg.jcal.JUnitDataTest;
import io.github.carmelolg.jcal.core.parallel.CellularAutomataParallelExecutor;
import io.github.carmelolg.jcal.model.DefaultCell;

public class GoLParallelExecutor extends CellularAutomataParallelExecutor {

	@Override
	public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {

		Long alives = neighbors.stream().filter(item -> item.currentStatus.equals(JUnitDataTest.alive)).count();

		DefaultCell toReturn = new DefaultCell(null, cell.getRow(), cell.getCol());
		
		if (cell.currentStatus.equals(JUnitDataTest.dead) && alives == 3) {
			toReturn.currentStatus = JUnitDataTest.alive;
		} else if (cell.currentStatus.equals(JUnitDataTest.alive) && (alives == 2 || alives == 3)) {
			toReturn.currentStatus = JUnitDataTest.alive;
		} else {
			toReturn.currentStatus = JUnitDataTest.dead;
		}

		return toReturn;
	}

}
