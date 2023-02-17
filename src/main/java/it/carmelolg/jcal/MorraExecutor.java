package it.carmelolg.jcal;

import java.util.List;

import it.carmelolg.jcal.core.CellularAutomataExecutor;
import it.carmelolg.jcal.model.DefaultCell;

public class MorraExecutor extends CellularAutomataExecutor {

	@Override
	public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) throws CloneNotSupportedException {

		Long countS = neighbors.stream().filter(item -> item.currentStatus.equals(MorraApplication.SASSO)).count();
		Long countF = neighbors.stream().filter(item -> item.currentStatus.equals(MorraApplication.FORBICE)).count();
		Long countC = neighbors.stream().filter(item -> item.currentStatus.equals(MorraApplication.CARTA)).count();
		String currentStatus = cell.getCurrentStatus();

		DefaultCell toReturn = cell.clone();

		if (currentStatus.equals(MorraApplication.SASSO) && countC >= countF && countC > countS) {
			toReturn.setCurrentStatus(MorraApplication.CARTA);
		} else if (currentStatus.equals(MorraApplication.FORBICE) && countS >= countC && countS > countF) {
			toReturn.setCurrentStatus(MorraApplication.SASSO);
		} else if (currentStatus.equals(MorraApplication.CARTA) && countF >= countS && countF > countC) {
			toReturn.setCurrentStatus(MorraApplication.FORBICE);
		}

		return toReturn;
	}

}
