public class Main {

	public static int WIDTH = 10, HEIGHT = 10;

	public static DefaultStatus dead = new DefaultStatus("dead", "0");
	public static DefaultStatus alive = new DefaultStatus("alive", "1");
	public static List<DefaultStatus> status = Arrays.asList(dead, alive);
	public static List<DefaultCell> initalState = new ArrayList<DefaultCell>();
	
	public static void main(String[] args) throws Exception {

		initalState.add(new DefaultCell(alive, 1, 1));
		initalState.add(new DefaultCell(alive, 1, 2));
		initalState.add(new DefaultCell(alive, 1, 3));
		initalState.add(new DefaultCell(alive, 2, 1));
		
	    
        CellularAutomataConfigurationBuilder configBuilder = new CellularAutomataConfigurationBuilder();
        CellularAutomataConfiguration config = configBuilder.setHeight(WIDTH)
                                                            .setWidth(HEIGHT)
                                                            .setTotalIterations(10)
                                                            .setDefaultStatus(Main.dead)
                                                            .setNeighborhoodType(NeighborhoodType.MOORE)
                                                            .setInitalState(initalState)
                                                            /** ... */
                                                            .build();
	    
		CellularAutomata ca = new CellularAutomata(config);
		GOLExecutor executor = new GOLExecutor();
		ca = executor.run(ca);
		System.out.println(ca);
	}
}