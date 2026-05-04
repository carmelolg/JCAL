---
title: "JCAL Documentation"
menu: "main"
---

## 📚 Choose Your Version

<div class="row g-3 mb-5">
  <div class="col-md-6">
    <div class="card h-100 border-primary">
      <div class="card-body">
        <h5 class="card-title">✨ v2.0.0-rc2 (Current)</h5>
        <p class="card-text text-muted">
          Latest release with n-dimensional support (2D/3D/4D), production-ready logging, and enhanced features.
        </p>
        <a href="/v2.0.0-rc2/" class="btn btn-primary btn-sm">View Documentation</a>
      </div>
    </div>
  </div>
  
  <div class="col-md-6">
    <div class="card h-100 border-secondary">
      <div class="card-body">
        <h5 class="card-title">🏛️ v1.0.0 (Stable)</h5>
        <p class="card-text text-muted">
          First stable release - foundation for cellular automata modeling. Well-tested and production-ready.
        </p>
        <a href="/v1.0.0/" class="btn btn-secondary btn-sm">View Documentation</a>
      </div>
    </div>
  </div>
</div>

---

## What JCAL Is

**JCAL** (Java Cellular Automata Library) is a lightweight Java library for building and
simulating [Cellular Automata](https://mathworld.wolfram.com/CellularAutomaton.html) with
minimal boilerplate. Define your grid, states, neighborhood strategy, and transition rule —
then let JCAL handle the rest.

## Formal Model

A Cellular Automaton is the quadruple **`<Z^d, S, X, σ>`**:

| Symbol | Meaning | JCAL type |
|--------|---------|-----------|
| **Z^d** | *d*-dimensional grid of cells | `CellGrid` (2D–4D, unified) |
| **S**  | Set of possible cell states | `CellState` |
| **X**  | Neighborhood strategy | `Neighborhood` subclass |
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
CellState dead  = new CellState("dead",  "0");
CellState alive = new CellState("alive", "1");

List<Cell> seed = Arrays.asList(
    new Cell(alive, 5, 4),
    new Cell(alive, 5, 5),
    new Cell(alive, 5, 6)  // vertical blinker
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
