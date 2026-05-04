
public class GoLDSApplication {

	public static GoLStatus status = new GoLStatus(false);
	public static int totalInteraction = 1, width = 10, height = 10;

	public static void main(String[] args) throws Exception {

		Long start = System.currentTimeMillis();

		CellularAutomataConfigurationBuilder builder = new CellularAutomataConfigurationBuilder();

		List<DefaultCell> initalState = new ArrayList<DefaultCell>();

		initalState.add(new DefaultCell(new GoLStatus(true), 1, 1));
		initalState.add(new DefaultCell(new GoLStatus(true), 1, 2));
		initalState.add(new DefaultCell(new GoLStatus(true), 1, 3));
		initalState.add(new DefaultCell(new GoLStatus(true), 2, 1));

		CellularAutomata ca = new CellularAutomata();
		ca.init(builder.setWidth(width)
						.setHeight(height)
						.setInfinite(false)
						.setTotalIterations(totalInteraction)
						.setNeighborhoodType(NeighborhoodType.MOORE)
						.setDefaultStatus(status)
						.setInitalState(initalState)
				.build());

		Long end = System.currentTimeMillis();

		System.out.println(ca.toString());
		System.out.println("Init elapsed: " + (end - start) / 1000 + " seconds.");
		System.out.println();

		GoLDSExecutor gol = new GoLDSExecutor();

		try {
			start = System.currentTimeMillis();
			ca = gol.run(ca);
			System.out.println(ca.toString());
			end = System.currentTimeMillis();
			System.out.println("Single thread --> Elapsed: " + (end - start) + " seconds.");
			System.out.println();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
