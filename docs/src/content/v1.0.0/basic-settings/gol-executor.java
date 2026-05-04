public class GOLExecutor extends CellularAutomataExecutor {

	@Override
	public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {
		
       	DefaultStatus dead = new DefaultStatus("dead", "0");
       	DefaultStatus alive = new DefaultStatus("alive", "1");
		Long alives = neighbors.stream().filter(item -> item.currentStatus.equals(alive)).count();	
		DefaultCell toReturn = new DefaultCell(null, cell.getRow(), cell.getCol());	
		if (cell.currentStatus.equals(dead) && alives == 3) {
			toReturn.currentStatus = alive;
		} else if (cell.currentStatus.equals(alive) && (alives == 2 || alives == 3)) {
			toReturn.currentStatus = alive;
		} else {
			toReturn.currentStatus = dead;
		}
		return toReturn;
	}
}