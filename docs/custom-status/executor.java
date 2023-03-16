public class GoLDSExecutor extends CellularAutomataExecutor {

	@Override
	public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {

		Long alives = neighbors.stream().filter(item -> ((GoLStatus) item.currentStatus).isAlive).count();
		
		boolean isAlive = ((GoLStatus)cell.currentStatus).isAlive;
		
		DefaultCell toReturn = new DefaultCell(null, cell.getRow(), cell.getCol());
		if (!isAlive && alives == 3) {
			toReturn.currentStatus = new GoLStatus(true);
		} else if (isAlive && (alives == 2 || alives == 3)) {
			toReturn.currentStatus = new GoLStatus(true);
		} else {
			toReturn.currentStatus = new GoLStatus(false);
		}

		return toReturn;
	}

}
