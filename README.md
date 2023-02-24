# JCAL - Java Cellular Automata Library
| | Badge|
|---|---|
| **License** | ![License: CC BY-NC-SA 4.0](https://img.shields.io/badge/License-CC_BY--NC--SA_4.0-lightgrey.svg) |
| **Unit Test** | ![Test](https://github.com/carmelolg/JCAL/workflows/tests/badge.svg) |
| **Test Coverage** | ![Coverage](.github/badges/jacoco.svg) |

## What is a Cellular Automata

Here some references:
* [Wolfram - Cellular Automaton](https://mathworld.wolfram.com/CellularAutomaton.html)
* [The nature of Code by Daniel Shiffman](https://natureofcode.com/book/chapter-7-cellular-automata/)
* [Chapter 3 of my master thesis (**in italian**)](https://github.com/carmelolg/master-thesis/blob/master/Tesi/pdf/main.pdf)

#### TLDR;
A **basic** Cellular Automata is the quadruple `<Z <sup>d</sup>,S,X,o>`

`Z <sup>d</sup>` is a set of cells, a d-dimension matrix of cells

`S` is a set of status where the single cell can be in

`X` is a set of cell's neighbors (the most common neighborhood implementation are [MOORE](https://en.wikipedia.org/wiki/Moore_neighborhood) and [VON NEUMANN](https://en.wikipedia.org/wiki/Von_Neumann_neighborhood)

 `o` is the transaction function. This function implement the evolution of the natural or artificial phenomena represented by a Cellular Automata.
 
Thanks to this mathematic model, it's possibile represent a lot of natural phenomena like landslides, lava flows and so on...

## What about JCAL idea

In the past, during my master thesis's work, I contribuited to implement a library for Cellular Automata, mainly used by Physicists, Geologists and Scientists from different departments. This library was written in C++ and obviously was more complete than JCAL. 

**JCAL wants to implements the same idea but in a smaller and simpler way for Java user and developers.**

## Getting started

### Build a new Cellular Automata

Here an example for building the Game of Life configuration.

```java
int WIDTH = 10, HEIGHT = 10;
DefaultStatus dead = new DefaultStatus("dead", "0");
DefaultStatus alive = new DefaultStatus("alive", "1");
List<DefaultStatus> status = Arrays.asList(dead, alive);
	
List<DefaultCell> initalState = new ArrayList<DefaultCell>();
initalState.add(new DefaultCell(alive, 1, 1));
initalState.add(new DefaultCell(alive, 1, 2));
initalState.add(new DefaultCell(alive, 1, 3));
initalState.add(new DefaultCell(alive, 2, 1));

CellularAutomataConfigurationBuilder configBuilder = new CellularAutomataConfigurationBuilder();

CellularAutomataConfiguration config = configBuilder.setHeight(WIDTH)
                                                    .setWidth(HEIGHT)
                                                    .setTotalIterations(10)
                                                    .setStatusList(status)
                                                    .setNeighborhoodType(NeighborhoodType.MOORE)
                                                    .setInitalState(initalState)
                                                    /** ... */
                                                    .build();
                                                    
CellularAutomata ca = new CellularAutomata(config);
```

### Implement the transaction function

You need to inherit the **CellularAutmataExecutor** class and implement the `singleRun` method.

```java
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
```

### Run the cellular automata

Here an example using a classic Java Main

```java
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
                                                            .setStatusList(status)
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
```

### All settings of CellularAutomataConfiguration

`setWidth`
```java
/**
 * Set the matrix width (the number of columns)
 * Default is 100
 * @param width, the columns number expressed in integer
 * @return the builder {@link CellularAutomataConfigurationBuilder} 
 */
public CellularAutomataConfigurationBuilder setWidth(int width);
```

`setHeight`
```java
/**
 * Set the matrix height (the number of rows)
 * Default is 100
 * @param height, the rows number expressed in integer.
 * @return the builder {@link CellularAutomataConfigurationBuilder} 
 */
public CellularAutomataConfigurationBuilder setHeight(int height);
```

`setInfinite`
```java
/**
 * @param isInfinite true if you want to run infinitely, false otherwise
 * @return the builder {@link CellularAutomataConfigurationBuilder} 
 */
public CellularAutomataConfigurationBuilder setInfinite(boolean isInfinite);
```

`setTotalIterations`
```java
/**
 * Set the number of iterations of the transaction function
 * @param totalIterations the number of iteractions
 * @return the builder {@link CellularAutomataConfigurationBuilder} 
 */
public CellularAutomataConfigurationBuilder setTotalIterations(int totalIterations);
```

`setStatusList`
```java
/**
 * Set the list of status usable on the Cellular Automata.
 * @param statusList a {@link List} of {@link String}
 * @return the builder {@link CellularAutomataConfigurationBuilder} 
 */
public CellularAutomataConfigurationBuilder setStatusList(List<DefaultStatus> statusList);
```

`setInitalState`
```java
/**
 * Set the inital configuration from where starting the cellular automata. Pratically, the cells that in the starting phase have different status of empty/dead.
 * @param initalState a {@link List} of {@link DefaultCell}
 * @return the builder {@link CellularAutomataConfigurationBuilder} 
 */
public CellularAutomataConfigurationBuilder setInitalState(List<DefaultCell> initalState);
```

`setNeighborhoodType`
```java
/**
 * If you don't have a custom neighborhood you can choose one already implemented in the enum NeighborhoodType
 * @param neighborhoodType the type of neighboorhood
 * @return the builder {@link CellularAutomataConfigurationBuilder} 
 */
public CellularAutomataConfigurationBuilder setNeighborhoodType(NeighborhoodType neighborhoodType);
```

`setNeighborhood`
```java
/**
 * If you have a custom neighborhood you can set your class here.
 * The class has to inerhit the {@link DefaultNeighborhood} class.
 * @param neighborhood
 * @return the builder {@link CellularAutomataConfigurationBuilder} 
 */
public CellularAutomataConfigurationBuilder setNeighborhood(DefaultNeighborhood neighborhood);
```

## Acknowledgements
- University of Calabria for teaching me really excited things.