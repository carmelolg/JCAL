+++
title = "Configurations properties"
description = ""
weight = 4
+++


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
 * Set the number of iterations of the transition function
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