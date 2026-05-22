public class Main {

    public static void main(String[] args) throws Exception {

        DefaultStatus dead  = new DefaultStatus("dead",  "0");
        DefaultStatus alive = new DefaultStatus("alive", "1");

        // Set the initial live cells (a "blinker" oscillator)
        List<DefaultCell> initialState = Arrays.asList(
            new DefaultCell(alive, 5, 4),
            new DefaultCell(alive, 5, 5),
            new DefaultCell(alive, 5, 6)
        );

        CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
            .setWidth(10)
            .setHeight(10)
            .setTotalIterations(2)
            .setDefaultStatus(dead)
            .setNeighborhoodType(NeighborhoodType.MOORE)
            .setInitalState(initialState)   // note: intentional spelling in the API
            .build();

        CellularAutomata ca = new CellularAutomata(config);
        GameOfLifeExecutor executor = new GameOfLifeExecutor();
        ca = executor.run(ca);

        System.out.println(ca);
    }
}
