package io.github.carmelolg.jcal.core;

import java.util.List;

import io.github.carmelolg.jcal.JUnitDataTest;
import io.github.carmelolg.jcal.model.DefaultCell;

public class GoLExecutor extends CellularAutomataExecutor {

	@Override
	public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {

		Long alives = neighbors.stream().filter(item -> item.getCurrentStatus().equals(JUnitDataTest.alive)).count();

		DefaultCell toReturn = new DefaultCell(null, cell.getRow(), cell.getCol());
		
		if (cell.getCurrentStatus().equals(JUnitDataTest.dead) && alives == 3) {
			toReturn.setCurrentStatus(JUnitDataTest.alive);
		} else if (cell.getCurrentStatus().equals(JUnitDataTest.alive) && (alives == 2 || alives == 3)) {
			toReturn.setCurrentStatus(JUnitDataTest.alive);
		} else {
			toReturn.setCurrentStatus(JUnitDataTest.dead);
		}

		return toReturn;
	}

}
