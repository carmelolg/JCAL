package it.carmelolg.jcal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.carmelolg.jcal.configuration.EnvironmentConfiguration.EnvironmentConfigurationBuilder;
import it.carmelolg.jcal.core.CellularAutomata;
import it.carmelolg.jcal.model.DefaultCell;
import it.carmelolg.jcal.model.Neighborhood;

public class GoLApplication {

	public static void main(String[] args) throws Exception {

		Long start = System.currentTimeMillis();

		EnvironmentConfigurationBuilder builder = new EnvironmentConfigurationBuilder();

		List<DefaultCell> initalState = new ArrayList<DefaultCell>();
		initalState.add(new DefaultCell("1", 1, 1));
		initalState.add(new DefaultCell("1", 1, 2));
		initalState.add(new DefaultCell("1", 1, 3));
		initalState.add(new DefaultCell("1", 2, 1));

		CellularAutomata ca = new CellularAutomata();
		ca.init(builder.setWidth(10).setHeight(10).setInfinite(false).setTotalIterations(6)
				.setNeighborhoodType(Neighborhood.MOORE)
//				.setNeighborhood(new VonNeumannNeighborhood())
				.setStatusList(Arrays.asList("0", "1")).setInitalState(initalState).build());

		Long end = System.currentTimeMillis();

		System.out.println("Elapsed: " + (end - start) / 1000 + " seconds.");
		GoLExecutor gol = new GoLExecutor();
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
