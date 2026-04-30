---
title: "Architecture"
date: 2025-01-01
draft: false
summary: "Package structure, component roles, data flow, and extension points."
weight: 10
toc: true
tags: ["architecture", "internals"]
---

## What JCAL Is

**JCAL** (Java Cellular Automata Library) is a lightweight Java library for building and
simulating [Cellular Automata](https://mathworld.wolfram.com/CellularAutomaton.html)
with minimal boilerplate.

A Cellular Automaton is formally described as the quadruple **`<Z^d, S, X, σ>`**:

| Symbol | Meaning | JCAL type |
|--------|---------|-----------|
| **Z^d** | d-dimensional grid of cells | `CellGrid` (`CellGrid2D` for 2D, `CellGridFlat` for 3D/4D) |
| **S**  | Set of possible cell states | `DefaultStatus` |
| **X**  | Neighborhood strategy | `DefaultNeighborhood` subclass |
| **σ**  | Transition function | `CellularAutomataExecutor` subclass |

---

## Package Structure

```
io.github.carmelolg.jcal
├── configuration/
│   └── CellularAutomataConfiguration      ← immutable config; use inner Builder
│       └── CellularAutomataConfigurationBuilder
│
├── core/
│   ├── CellularAutomata                   ← the grid (CellGrid); holds neighborhood + config
│   ├── CellularAutomataExecutor           ← abstract; extend to define the transition rule
│   ├── DefaultNeighborhood                ← abstract; extend for a custom 2D neighborhood
│   ├── DefaultNeighborhoodND              ← abstract; extend for 3D/4D neighborhoods
│   ├── MooreNeighborhood                  ← built-in 2D: 8 surrounding cells
│   ├── VonNeumannNeighborhood             ← built-in 2D: 4 orthogonal cells
│   ├── Moore3DNeighborhood                ← built-in 3D: 26 surrounding cells
│   ├── VonNeumann3DNeighborhood           ← built-in 3D: 6 orthogonal cells
│   ├── Moore4DNeighborhood                ← built-in 4D: 80 surrounding cells
│   ├── VonNeumann4DNeighborhood           ← built-in 4D: 8 orthogonal cells
│   ├── grid/
│   │   ├── CellGrid                       ← interface: get/set/isInside/allCoordinates/dimensions
│   │   ├── CellGrid2D                     ← 2D impl backed by DefaultCell[][]
│   │   └── CellGridFlat                   ← nD flat-array impl with stride-based indexing
│   └── parallel/
│       ├── CellularAutomataParallelExecutor   ← parallel variant of the executor
│       ├── CellularAutomataRunner             ← internal — Callable for parallel transition
│       └── CellularAutomataRefinementRunner   ← internal — Callable for parallel refinement
│
├── model/
│   ├── DefaultCell                        ← one cell; has a DefaultStatus + int[] coordinates
│   ├── DefaultStatus                      ← a cell's state; key + arbitrary value Object
│   ├── GridDimensions                     ← immutable nD descriptor (sizes, strides, total)
│   └── NeighborhoodType                   ← enum: MOORE | VON_NEUMANN
│
├── utils/
│   └── Utils                              ← internal helpers (isInside, cloneGrid)
│
└── examples/
    ├── GameOfLifeExample                  ← 2D minimal runnable example
    ├── GameOfLife3DExample                ← 3D example: Carter Bays' 3D Life
    └── CustomStateExample                 ← advanced example with multi-value state
```

---

## Public API vs. Internal

| Class | Status | Notes |
|-------|--------|-------|
| `CellularAutomata` | **Public API** | Core grid object |
| `CellularAutomataConfiguration` | **Public API** | Always build via inner `Builder` |
| `CellularAutomataExecutor` | **Extension point** | Subclass to define your rule |
| `DefaultNeighborhood` | **Extension point** | Subclass for custom 2D neighborhood |
| `DefaultNeighborhoodND` | **Extension point** | Subclass for custom 3D/4D neighborhood |
| `CellGrid` | **Public API** | Interface for grid access (2D and nD) |
| `CellGrid2D` | **Public API** | 2D grid backed by `DefaultCell[][]` |
| `CellGridFlat` | **Public API** | nD flat-array grid for 3D/4D use |
| `GridDimensions` | **Public API** | Immutable nD grid descriptor |
| `DefaultCell` | **Public API** | Returned by the grid; construct for initial state |
| `DefaultStatus` | **Public API** | Create instances for each state your CA needs |
| `NeighborhoodType` | **Public API** | Pass to builder for built-in neighborhoods |
| `MooreNeighborhood` | Public (via `NeighborhoodType.MOORE`) | 2D, rarely instantiated directly |
| `VonNeumannNeighborhood` | Public (via `NeighborhoodType.VON_NEUMANN`) | 2D, rarely instantiated directly |
| `Moore3DNeighborhood` | Public (auto-resolved for 3D) | Use `NeighborhoodType.MOORE` |
| `VonNeumann3DNeighborhood` | Public (auto-resolved for 3D) | Use `NeighborhoodType.VON_NEUMANN` |
| `Moore4DNeighborhood` | Public (auto-resolved for 4D) | Use `NeighborhoodType.MOORE` |
| `VonNeumann4DNeighborhood` | Public (auto-resolved for 4D) | Use `NeighborhoodType.VON_NEUMANN` |
| `CellularAutomataParallelExecutor` | **Extension point** | Parallel variant of executor |
| `CellularAutomataRunner` | **Internal** | Do not use directly |
| `CellularAutomataRefinementRunner` | **Internal** | Do not use directly |
| `Utils` | Internal helper | May be used by custom neighborhoods |

---

## Data Flow per Generation

```
for each generation:
  1. refinements(cell)           — applied to every cell  [optional CCA hook]
  2. snapshot the grid           — clone the grid before any mutation
  3. singleRun(cell, neighbors)  — per cell, reads snapshot, writes result
  4. copy result back            — update the main grid with all new states
```

The snapshot in step 2 ensures full isolation: all cells in `singleRun` see the
*pre-transition* state of their neighbors, regardless of the order in which cells
are processed.

---

## Grid Abstraction

### CellGrid Interface

```java
public interface CellGrid {
    DefaultCell get(int[] coords);
    void set(int[] coords, DefaultCell cell);
    boolean isInside(int[] coords);
    Stream<int[]> allCoordinates();
    GridDimensions dimensions();
}
```

- `CellGrid2D` — backed by `DefaultCell[][]`; returned by `getMap()` / `getUtilsMap()`
  for 2D backward compatibility.
- `CellGridFlat` — flat `DefaultCell[]` array with stride-based indexing; used for 3D/4D.

### GridDimensions

Stores the size of each axis and precomputes strides for O(1) flat-array indexing:

```
flat index = coords[0] * stride[0] + coords[1] * stride[1] + ... + coords[n-1] * stride[n-1]
```

---

## Extension Points

### 1. Custom Transition Rule

Subclass `CellularAutomataExecutor` (sequential) or `CellularAutomataParallelExecutor`
(parallel):

```java
public class MyRule extends CellularAutomataExecutor {
    @Override
    public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {
        // inspect cell.getCurrentStatus() and neighbors
        // return a new DefaultCell with the next state
    }
}
```

### 2. Custom State

`DefaultStatus` accepts any `Object` as its `value`:

```java
DefaultStatus complex = new DefaultStatus("state", Map.of("temp", 100, "pressure", 3));
```

Two `DefaultStatus` values are equal when both `key` and `value` are equal.

### 3. Custom 2D Neighborhood

Subclass `DefaultNeighborhood`:

```java
public class DiagonalNeighborhood extends DefaultNeighborhood {
    @Override
    public List<DefaultCell> getNeighbors(DefaultCell[][] matrix, int i, int j) {
        // return a list of neighbor cells
    }
}
```

### 4. Custom nD Neighborhood

Subclass `DefaultNeighborhoodND`:

```java
public class Custom3DNeighborhood extends DefaultNeighborhoodND {
    @Override
    public List<DefaultCell> getNeighbors(CellGrid grid, int[] coords) {
        // return a list of neighbor cells
    }
}
```

### 5. CCA Pre-Processing

Override `refinements(DefaultCell cell)` in your executor:

```java
@Override
public DefaultCell refinements(DefaultCell cell) {
    // update internal state before neighbors are read
    return cell;
}
```

---

## Configuration Flow

```
CellularAutomataConfigurationBuilder
    → CellularAutomataConfiguration (immutable)
    → CellularAutomata(config)       ← builds CellGrid + resolves neighborhood
    → executor.run(ca)               ← applies transition for N generations
    → updated CellularAutomata
```

---

## Constraints

| Constraint | Detail |
|-----------|--------|
| Java 16 | Compile target `--release 16` |
| No breaking changes | Public API signatures must not change without explicit approval |
| `setInitalState` | Keep the intentional spelling — it is part of the public API |
| Javadoc required | Every new public class and method must have Javadoc |
| License | CC BY-NC-SA 4.0 |
