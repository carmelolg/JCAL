---
title: "JCAL Documentation"
---

**JCAL** (Java Cellular Automata Library) is a lightweight Java library for building and
simulating [Cellular Automata](https://mathworld.wolfram.com/CellularAutomaton.html) with
minimal boilerplate. Define your grid, states, neighborhood strategy, and transition rule —
then let JCAL handle the rest.

## Formal Model

A Cellular Automaton is the quadruple **`<Z^d, S, X, σ>`**:

| Symbol | Meaning | JCAL type |
|--------|---------|-----------|
| **Z^d** | *d*-dimensional grid of cells | `CellGrid` → `CellGrid2D` / `CellGridFlat` |
| **S**  | Set of possible cell states | `DefaultStatus` |
| **X**  | Neighborhood strategy | `DefaultNeighborhood` subclass |
| **σ**  | Transition function | `CellularAutomataExecutor` subclass |

## Key Features

| Feature | Description |
|---------|-------------|
| ☕ **Idiomatic Java** | Fluent builder API, abstract base classes, standard collections |
| 📐 **Multi-dimensional** | 2D, 3D, and 4D grids with matching built-in neighborhoods |
| 🏘️ **Built-in neighborhoods** | Moore and Von Neumann for 2D, 3D, and 4D |
| 🔌 **Extensible** | Custom states, neighborhoods, and rules with minimal code |
| ⚙️ **Complex CA support** | Refinement hook enables rich multi-value simulations |
| ⚡ **Parallel execution** | `CellularAutomataParallelExecutor` — same API, more threads |

## Quick Example

Conway's Game of Life in a few lines:

```java
DefaultStatus dead  = new DefaultStatus("dead",  "0");
DefaultStatus alive = new DefaultStatus("alive", "1");

List<DefaultCell> seed = Arrays.asList(
    new DefaultCell(alive, 5, 4),
    new DefaultCell(alive, 5, 5),
    new DefaultCell(alive, 5, 6)  // vertical blinker
);

CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
    .setWidth(10).setHeight(10)
    .setTotalIterations(2)
    .setDefaultStatus(dead)
    .setNeighborhoodType(NeighborhoodType.MOORE)
    .setInitalState(seed)
    .build();

CellularAutomata ca = new CellularAutomata(config);
ca = new GameOfLifeExecutor().run(ca);
System.out.println(ca);
```

## Acknowledgements

Inspired by research carried out at the [University of Calabria](https://www.unical.it/)
on Cellular Automata models for natural-phenomena simulation.
