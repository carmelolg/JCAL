public class GameOfLifeExecutor extends CellularAutomataExecutor {

    @Override
    public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {

        DefaultStatus dead  = new DefaultStatus("dead",  "0");
        DefaultStatus alive = new DefaultStatus("alive", "1");

        long aliveCount = neighbors.stream()
                .filter(n -> n.getCurrentStatus().equals(alive))
                .count();

        boolean isAlive = cell.getCurrentStatus().equals(alive);
        DefaultCell next = new DefaultCell(dead, cell.getCol(), cell.getRow());

        if (!isAlive && aliveCount == 3) {
            next.setCurrentStatus(alive);          // dead cell with 3 live neighbors is born
        } else if (isAlive && (aliveCount == 2 || aliveCount == 3)) {
            next.setCurrentStatus(alive);          // live cell survives with 2 or 3 neighbors
        }
        // otherwise the cell stays / becomes dead

        return next;
    }
}
