# 🧬 JCAL — Java Cellular Automata Library

<img src="https://img.shields.io/badge/License-CC_BY--NC--SA_4.0-lightgrey.svg" alt="License"/> <img src=".github/badges/jacoco.svg" alt="Test Coverage"/> <img src="https://img.shields.io/badge/Java-16-orange.svg" alt="Java 16"/> <img src="https://img.shields.io/badge/build-Maven-blue.svg" alt="Maven"/>

> **Model natural and artificial phenomena** — lava flows, heat diffusion, Conway's Game of Life and more — with a
> clean, extensible Java API. 2D, 3D, and 4D grids out of the box.

## 🧩 What is a Cellular Automaton?

A Cellular Automaton (CA) is formally defined as the quadruple **`<Z^d, S, X, σ>`**:

| Symbol | Meaning                                                         |
|--------|-----------------------------------------------------------------|
| `Z^d`  | A d-dimensional grid of cells                                   |
| `S`    | The finite set of states a cell can be in                       |
| `X`    | The neighbourhood — which cells are considered "neighbours"     |
| `σ`    | The transition function — how each cell evolves each generation |

With this model you can simulate a surprising range of natural phenomena: landslides, lava flows, epidemic spreading,
crystal growth, and more.

📖 More reading:

- [Wolfram — Cellular Automaton](https://mathworld.wolfram.com/CellularAutomaton.html)
- [The Nature of Code — Chapter 7](https://natureofcode.com/book/chapter-7-cellular-automata/) by Daniel Shiffman
- [Master thesis (Italian)](https://github.com/carmelolg/master-thesis/blob/master/Tesi/pdf/main.pdf) — the research
  that inspired JCAL

---

## ✨ Features

- 🗺️ **Multi-dimensional grids** — 2D, 3D, and 4D cellular automata with a single unified `CellGrid` API
- ⚡ **Sequential & parallel execution** — choose `CellularAutomataExecutor` or `CellularAutomataParallelExecutor`
- 🔲 **Built-in neighbourhoods** — Moore and Von Neumann in 2D, 3D, and 4D
- 🔌 **Fully extensible** — plug in any custom state, neighbourhood, or transition rule
- ✅ **100% test coverage** — 148 JUnit 5 tests, JaCoCo verified

---

## 🚀 Quick Start

### 1. Define your states

```java
CellState DEAD = new CellState("dead", "0");
CellState ALIVE = new CellState("alive", "1");
```

### 2. Configure the grid

```java
CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
        .setWidth(10)
        .setHeight(10)
        .setTotalIterations(5)
        .setDefaultStatus(DEAD)
        .setInitalState(List.of(new Cell(ALIVE, 5, 4), new Cell(ALIVE, 5, 5), new Cell(ALIVE, 5, 6)))
        .setNeighborhoodType(NeighborhoodType.MOORE)
        .build();
```

### 3. Implement your rule

```java
class GameOfLifeRule extends CellularAutomataExecutor {
    @Override
    public Cell singleRun(Cell cell, List<Cell> neighbors) {
        long alive = neighbors.stream()
                .filter(n -> n.getCurrentStatus().equals(ALIVE)).count();
        Cell next = new Cell(DEAD, cell.getCol(), cell.getRow());
        boolean isAlive = cell.getCurrentStatus().equals(ALIVE);
        if ((!isAlive && alive == 3) || (isAlive && (alive == 2 || alive == 3)))
            next.setCurrentStatus(ALIVE);
        return next;
    }
}
```

### 4. Run it

```java
CellularAutomata ca = new CellularAutomata(config);
ca =new

GameOfLifeRule().

run(ca);
System.out.

println(ca);
```

---

## 🧊 3D Example — Carter Bays' Life

Going beyond 2D is just one builder call away:

```java
CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
        .setDimensions(10, 10, 10)              // 3D grid
        .setTotalIterations(5)
        .setDefaultStatus(DEAD)
        .setNeighborhoodType(NeighborhoodType.MOORE)   // resolves to Moore3DNeighborhood
        .setInitalState(initialCells)
        .build();

CellularAutomata ca = new CellularAutomata(config);
new

Carter3DLifeRule().

run(ca);
```

Access cells by coordinate array:

```java
CellGrid grid = ca.getGrid();
Cell cell = grid.get(new int[]{x, y, z});
```

---

## 🔌 Extension Points

| What to customise            | How                                                                        |
|------------------------------|----------------------------------------------------------------------------|
| Transition rule (sequential) | Extend `CellularAutomataExecutor`, implement `singleRun(Cell, List<Cell>)` |
| Transition rule (parallel)   | Extend `CellularAutomataParallelExecutor` instead                          |
| Cell state                   | Any `Object` as `value` in `CellState` (int, enum, Map, POJO…)             |
| Neighbourhood (2D)           | Extend `Neighborhood`, implement `getNeighbors(CellGrid, int[])`           |
| Neighbourhood (3D/4D)        | Extend `Neighborhood` **+** implement marker interface `NDCapable`         |
| CCA pre-processing           | Override `refinements(Cell)` in your executor                              |

---

## 📦 Built-in Neighbourhoods

| Class                      | Dimensions | Neighbours |
|----------------------------|------------|------------|
| `MooreNeighborhood`        | 2D         | 8          |
| `VonNeumannNeighborhood`   | 2D         | 4          |
| `Moore3DNeighborhood`      | 3D         | 26         |
| `VonNeumann3DNeighborhood` | 3D         | 6          |
| `Moore4DNeighborhood`      | 4D         | 80         |
| `VonNeumann4DNeighborhood` | 4D         | 8          |

---


## 📖 Documentation

Full API docs and guides at **[carmelolg.github.io/JCAL](https://carmelolg.github.io/JCAL/)**.

---

## 📄 License

Released under [CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/) — free for non-commercial use with
attribution.
