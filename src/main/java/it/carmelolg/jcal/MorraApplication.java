package it.carmelolg.jcal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import it.carmelolg.jcal.configuration.EnvironmentConfiguration.EnvironmentConfigurationBuilder;
import it.carmelolg.jcal.core.CellularAutomata;
import it.carmelolg.jcal.model.DefaultCell;

public class MorraApplication {

	static String SASSO = "S", CARTA = "C", FORBICE = "F";
	static List<String> status = Arrays.asList(SASSO, CARTA, FORBICE);
	static int WIDTH = 10, HEIGHT = 10;

	public static void main(String[] args) throws Exception {

		Long start = System.currentTimeMillis();

		EnvironmentConfigurationBuilder builder = new EnvironmentConfigurationBuilder();

		List<DefaultCell> initalState = new ArrayList<DefaultCell>();

		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				initalState.add(new DefaultCell(status.get(new Random().nextInt(status.size())), i, j));
			}
		}

		CellularAutomata ca = new CellularAutomata();
		ca.init(builder.setWidth(WIDTH).setHeight(HEIGHT).setInfinite(false).setTotalIterations(1000000)
				.setNeighborhood(new MorraNeighborhood()).setStatusList(status).setInitalState(initalState).build());

		Long end = System.currentTimeMillis();

		System.out.println("Elapsed: " + (end - start) / 1000 + " seconds.");
		MorraExecutor gol = new MorraExecutor();
		System.out.println(ca.toString());
		try {
			ca = gol.run(ca);

			System.out.println(ca.toString());
			end = System.currentTimeMillis();

			System.out.println("Elapsed: " + (end - start) / 1000 + " seconds.");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
