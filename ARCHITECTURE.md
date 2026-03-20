# JCAL Architecture Overview

> This document is optimised for both human readers and AI code assistants.
> It describes the library's main concepts, component map, and extension points.

---

## 1. What JCAL Is

**JCAL** (Java Cellular Automata Library) is a lightweight Java library for building and
simulating [Cellular Automata](https://mathworld.wolfram.com/CellularAutomaton.html) (CA)
with minimal boilerplate.

A Cellular Automaton is formally described as the quadruple **`<Zd, S, X, σ>`**:

| Symbol | Meaning | JCAL type |
|--------|---------|-----------|
| **Zd** | d-dimensional grid of cells | `DefaultCell[][]` (inside `CellularAutomata`) |
| **S**  | Set of possible cell states | `DefaultStatus` |
| **X**  | Neighborhood (which cells are "neighbours") | `DefaultNeighborhood` subclass |
| **σ**  | Transition function (one step of evolution) | `CellularAutomataExecutor` subclass |

---

## 2. Component Map

```
io.github.carmelolg.jcal
├── configuration/
│   └── CellularAutomataConfiguration      ← immutable config; use its inner Builder
│       └── CellularAutomataConfigurationBuilder
│
├── core/
│   ├── CellularAutomata                   ← the grid; holds map[][], neighborhood, config
│   ├── CellularAutomataExecutor           ← abstract; extend to define the transition rule
│   ├── DefaultNeighborhood                ← abstract; extend to define a custom neighborhood
│   ├── MooreNeighborhood                  ← built-in: 8 surrounding cells
│   ├── VonNeumannNeighborhood             ← built-in: 4 orthogonal cells
│   └── parallel/
│       ├── CellularAutomataParallelExecutor   ← parallel variant of the executor
│       ├── CellularAutomataRunner             ← internal – Callable for parallel transition
│       └── CellularAutomataRefinementRunner   ← internal – Callable for parallel refinement
│
├── model/
│   ├── DefaultCell                        ← one cell; has a DefaultStatus + (col, row)
│   ├── DefaultStatus                      ← a cell's state; key + arbitrary value Object
│   └── NeighborhoodType                   ← enum: MOORE | VON_NEUMANN
│
├── utils/
│   └── Utils                              ← internal helpers (isInside, cloneMaps)
│
└── examples/
    ├── GameOfLifeExample                  ← minimal runnable example (copy-paste start)
    └── CustomStateExample                 ← advanced example with multi-value state
```

### What is internal vs. public API?

| Class | Status | Notes |
|-------|--------|-------|
| `CellularAutomata` | **Public API** | Core grid object; create via constructor |
| `CellularAutomataConfiguration` | **Public API** | Always build via inner `Builder` |
| `CellularAutomataExecutor` | **Extension point** | Subclass to define your rule |
| `DefaultNeighborhood` | **Extension point** | Subclass for a custom neighborhood |
| `DefaultCell` | **Public API** | Returned by the grid; construct for initial state |
| `DefaultStatus` | **Public API** | Create instances for each state your CA needs |
| `NeighborhoodType` | **Public API** | Pass to builder when using a built-in neighborhood |
| `MooreNeighborhood` | Public (use via `NeighborhoodType.MOORE`) | Rarely instantiated directly |
| `VonNeumannNeighborhood` | Public (use via `NeighborhoodType.VON_NEUMANN`) | Rarely instantiated directly |
| `CellularAutomataParallelExecutor` | **Extension point** | Parallel variant of executor |
| `CellularAutomataRunner` | **Internal** | Do not use directly |
| `CellularAutomataRefinementRunner` | **Internal** | Do not use directly |
| `Utils` | Internal helper | May be used by custom neighborhoods |

---

## 3. Quick-Start Flow

```java
// 1. Define states
DefaultStatus dead  = new DefaultStatus("dead",  "0");
DefaultStatus alive = new DefaultStatus("alive", "1");

// 2. Define initial live cells
List<DefaultCell> initialState = Arrays.asList(
    new DefaultCell(alive, 5, 4),
    new DefaultCell(alive, 5, 5),
    new DefaultCell(alive, 5, 6)
);

// 3. Build configuration
CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
    .setWidth(10).setHeight(10)
    .setInfinite(false).setTotalIterations(2)
    .setDefaultStatus(dead)
    .setInitalState(initialState)
    .setNeighborhoodType(NeighborhoodType.MOORE)
    .build();

// 4. Implement the transition rule
CellularAutomataExecutor rule = new CellularAutomataExecutor() {
    public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {
        long aliveCount = neighbors.stream()
            .filter(n -> n.getCurrentStatus().equals(alive)).count();
        DefaultCell next = new DefaultCell(dead, cell.getCol(), cell.getRow());
        boolean isAlive = cell.getCurrentStatus().equals(alive);
        if (!isAlive && aliveCount == 3) next.setCurrentStatus(alive);
        else if (isAlive && (aliveCount == 2 || aliveCount == 3)) next.setCurrentStatus(alive);
        return next;
    }
};

// 5. Run
CellularAutomata ca = new CellularAutomata(config);
ca = rule.run(ca);
System.out.println(ca);
```

---

## 4. Extension Points

### 4.1 Custom Transition Rule (most common)

Extend `CellularAutomataExecutor` (sequential) or `CellularAutomataParallelExecutor` (parallel):

```java
public class MyRule extends CellularAutomataExecutor {
    @Override
    public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {
        // Inspect cell.getCurrentStatus() and neighbors
        // Return a new DefaultCell with the next state
    }
}
```

The method is called **once per cell per generation**.  You only describe what happens to
a single cell; the framework applies it to every cell.

### 4.2 Custom State

`DefaultStatus` accepts any `Object` as its `value`.  Use this to store rich per-cell data:

```java
// Integer heat level
DefaultStatus hot  = new DefaultStatus("hot",  2);
DefaultStatus warm = new DefaultStatus("warm", 1);
DefaultStatus cold = new DefaultStatus("cold", 0);

// Or even a Map / POJO
DefaultStatus complex = new DefaultStatus("complex", Map.of("temp", 100, "pressure", 3));
```

Two `DefaultStatus` values are equal when both `key` **and** `value` are equal.

### 4.3 Custom Neighborhood

Extend `DefaultNeighborhood`:

```java
public class DiagonalNeighborhood extends DefaultNeighborhood {
    @Override
    public List<DefaultCell> getNeighbors(DefaultCell[][] matrix, int i, int j) {
        List<DefaultCell> result = new ArrayList<>();
        int[][] diagonals = {{-1,-1},{-1,1},{1,-1},{1,1}};
        for (int[] d : diagonals) {
            if (Utils.isInside(matrix, i+d[0], j+d[1]))
                result.add(matrix[i+d[0]][j+d[1]]);
        }
        return result;
    }
}
```

Pass your instance via `.setNeighborhood(new DiagonalNeighborhood())` in the builder
(cannot combine with `setNeighborhoodType`).

### 4.4 Complex Cellular Automata (CCA) – Refinements

Override `refinements(DefaultCell cell)` in your executor to pre-process cell state
before the transition function reads neighbours:

```java
@Override
public DefaultCell refinements(DefaultCell cell) {
    // Adjust cell state before neighbours are evaluated
    return cell;
}
```

This is the recommended hook for CCA models where each cell needs internal bookkeeping
between steps (e.g. accumulating flows, decrementing counters).

---

## 5. Data Flow per Generation

```
for each generation:
  1. refinements(cell)  →  applied to every cell   [optional CCA hook]
  2. clone map          →  snapshot before mutation
  3. singleRun(cell, neighbors)  →  per cell, reads snapshot, writes result
  4. copy result back to main map
```

---

## 6. Configuration Reference

| Builder method | Default | Description |
|----------------|---------|-------------|
| `setWidth(int)` | 100 | Number of columns |
| `setHeight(int)` | 100 | Number of rows |
| `setTotalIterations(int)` | 0 | Steps to run (when not infinite) |
| `setInfinite(boolean)` | false | Run until interrupted (cannot combine with `setTotalIterations > 0`) |
| `setDefaultStatus(DefaultStatus)` | — | **Required.** Initial state of every cell |
| `setInitalState(List<DefaultCell>)` | empty | Cells that start with a non-default state |
| `setNeighborhoodType(NeighborhoodType)` | — | Use a built-in neighborhood |
| `setNeighborhood(DefaultNeighborhood)` | — | Use a custom neighborhood instance |

> **Exactly one of** `setNeighborhoodType` / `setNeighborhood` must be set.
> `setTotalIterations` and `setInfinite(true)` are mutually exclusive.

---

## 7. Parallel Execution

For large grids, replace `CellularAutomataExecutor` with `CellularAutomataParallelExecutor`.
The API is identical; the framework splits rows across threads automatically using Java
parallel streams.

```java
public class MyParallelRule extends CellularAutomataParallelExecutor {
    @Override
    public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {
        // Same as the sequential version
    }
}
```

---

## 8. Examples

| File | What it shows |
|------|---------------|
| `examples/GameOfLifeExample.java` | Minimal two-state CA; recommended first read |
| `examples/CustomStateExample.java` | Three-state heat diffusion; shows custom `DefaultStatus` values |

---

## 9. Tests as Specification

| Test class | What it verifies |
|------------|-----------------|
| `GameOfLifeSpecificationTest` | Blinker oscillation, block still life, underpopulation death |
| `CustomStateSpecificationTest` | Custom status equality, HOT persistence, heat diffusion propagation |

Existing unit tests cover configuration validation, neighborhood geometry, grid
initialization, and both sequential and parallel execution paths.
