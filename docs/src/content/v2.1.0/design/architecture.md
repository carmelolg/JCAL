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
| **Z^d** | d-dimensional grid of cells | `CellGrid` (`CellGrid` for 2D, `CellGrid` for 3D/4D) |
| **S**  | Set of possible cell states | `CellState` |
| **X**  | Neighborhood strategy | `Neighborhood` subclass |
| **σ**  | Transition function | `CellularAutomataRule` subclass |

---

## Package Structure

```
io.github.carmelolg.jcal
├── core/
│   ├── CellularAutomata                   ← the grid (CellGrid); holds neighbourhood + config
│   ├── CellularAutomataConfiguration      ← immutable config; use inner Builder
│   │   └── CellularAutomataConfigurationBuilder
│   ├── CellularAutomataRule           ← abstract; extend to define the transition rule
│   ├── GenerationListener             ← @FunctionalInterface; notified after each generation
│   └── parallel/
│       ├── CellularAutomataParallelRule   ← parallel variant of the executor
│       ├── Runner                             ← internal — Callable for parallel transition
│       └── RefinementRunner                   ← internal — Callable for parallel refinement
│
├── grid/
│   ├── Cell                               ← one cell; has a CellState + int[] coordinates
│   ├── CellState                          ← a cell's state; key + arbitrary value Object
│   ├── CellGrid                           ← unified n-dimensional grid (2D–4D, flat array)
│   ├── GridDimensions                     ← immutable nD descriptor (sizes, strides, total)
│   └── GridSnapshot                       ← immutable snapshot of a CellGrid at a generation
│
├── neighborhood/
│   ├── Neighborhood                       ← abstract base; extend for custom neighbourhoods
│   ├── NDCapable                          ← marker interface; implement for 3D/4D custom neighbourhoods
│   ├── NeighborhoodType                   ← enum: MOORE | VON_NEUMANN
│   ├── MooreNeighborhood                  ← built-in 2D: 8 surrounding cells
│   ├── VonNeumannNeighborhood             ← built-in 2D: 4 orthogonal cells
│   ├── Moore3DNeighborhood                ← built-in 3D: 26 surrounding cells
│   ├── VonNeumann3DNeighborhood           ← built-in 3D: 6 orthogonal cells
│   ├── Moore4DNeighborhood                ← built-in 4D: 80 surrounding cells
│   └── VonNeumann4DNeighborhood           ← built-in 4D: 8 orthogonal cells
│
├── ui/
│   ├── CellRenderer                       ← @FunctionalInterface: CellState → Color
│   ├── GridDisplay                        ← interface: accepts a GridSnapshot for rendering
│   ├── GridPanel                          ← Swing JPanel; paints the grid via CellRenderer
│   ├── CellularAutomataDisplay            ← JFrame-backed window + generation counter
│   ├── AutomataListener                   ← GenerationListener that drives a GridDisplay
│   └── CellularAutomataUIRunner           ← fluent façade: wires display + listener + thread
│
├── utils/
│   └── Utils                              ← internal helpers (isInside, cloneGrid)
│
└── examples/
    ├── GameOfLifeExample                  ← 2D minimal runnable example
    ├── GameOfLifeUiExample                ← 2D example with Swing real-time visualisation
    ├── GameOfLifeAdvancedUiExample        ← 2D advanced patterns with full UI API
    ├── GameOfLife3DExample                ← 3D example: Carter Bays' 3D Life
    ├── GameOfLife3DUiExample              ← 3D example with Swing visualisation
    └── CustomStateExample                 ← advanced example with multi-value state
```

---

## Public API vs. Internal

| Class | Status | Notes |
|-------|--------|-------|
| `CellularAutomata` | **Public API** | Core grid object |
| `CellularAutomataConfiguration` | **Public API** | Always build via inner `Builder` |
| `CellularAutomataRule` | **Extension point** | Subclass to define your rule |
| `GenerationListener` | **Public API** | `@FunctionalInterface`; register via `addGenerationListener` |
| `GridSnapshot` | **Public API** | Immutable grid snapshot; passed to every listener call |
| `Neighborhood` | **Extension point** | Subclass for any custom neighbourhood (2D or nD) |
| `NDCapable` | **Marker interface** | Implement alongside `Neighborhood` for 3D/4D custom neighbourhoods |
| `CellGrid` | **Public API** | Unified n-dimensional grid class (2D–4D) |
| `GridDimensions` | **Public API** | Immutable nD grid descriptor |
| `Cell` | **Public API** | Returned by the grid; construct for initial state |
| `CellState` | **Public API** | Create instances for each state your CA needs |
| `NeighborhoodType` | **Public API** | Pass to builder for built-in neighbourhoods |
| `MooreNeighborhood` | Public (via `NeighborhoodType.MOORE`) | 2D, rarely instantiated directly |
| `VonNeumannNeighborhood` | Public (via `NeighborhoodType.VON_NEUMANN`) | 2D, rarely instantiated directly |
| `Moore3DNeighborhood` | Public (auto-resolved for 3D) | Use `NeighborhoodType.MOORE` |
| `VonNeumann3DNeighborhood` | Public (auto-resolved for 3D) | Use `NeighborhoodType.VON_NEUMANN` |
| `Moore4DNeighborhood` | Public (auto-resolved for 4D) | Use `NeighborhoodType.MOORE` |
| `VonNeumann4DNeighborhood` | Public (auto-resolved for 4D) | Use `NeighborhoodType.VON_NEUMANN` |
| `CellularAutomataParallelRule` | **Extension point** | Parallel variant of executor |
| `CellRenderer` | **Public API** | `@FunctionalInterface`; maps `CellState` to `Color` for UI rendering |
| `GridDisplay` | **Public API** | Interface for any component that renders a `GridSnapshot` |
| `CellularAutomataDisplay` | **Public API** | Swing `JFrame` window with generation counter |
| `AutomataListener` | **Public API** | `GenerationListener` that drives a `GridDisplay` |
| `CellularAutomataUIRunner` | **Public API** | Fluent façade for zero-boilerplate Swing visualisation |
| `GridPanel` | Public (used by `CellularAutomataDisplay`) | Swing `JPanel`; rarely used directly |
| `Runner` | **Internal** | Do not use directly |
| `RefinementRunner` | **Internal** | Do not use directly |
| `Utils` | Internal helper | May be used by custom neighbourhoods |

---

## Data Flow per Generation

```
for each generation:
  1. refinements(cell)           — applied to every cell  [optional CCA hook]
  2. snapshot the grid           — clone the grid before any mutation
  3. transition(cell, neighbors)  — per cell, reads snapshot, writes result
  4. copy result back            — update the main grid with all new states
```

The snapshot in step 2 ensures full isolation: all cells in `transition` see the
*pre-transition* state of their neighbors, regardless of the order in which cells
are processed.

---

## Grid Abstraction

### CellGrid

`CellGrid` is the unified n-dimensional grid class (2D–4D) backed by a flat array
with stride-based indexing. All executors and neighbourhoods operate on `CellGrid`.

```java
Cell   get(int[] coords)
void   set(int[] coords, Cell cell)
List<int[]> allCoordinates()
GridDimensions getDimensions()
boolean is2D()
```

`CellGrid` is always created by `CellularAutomata` — you never instantiate it directly.

### GridDimensions

A Java 16 **record** that stores the size of each axis and precomputes strides for
O(1) flat-array indexing:

```
flat index = coords[0] * stride[0] + coords[1] * stride[1] + … + coords[n-1] * stride[n-1]
```

---

## Extension Points

### 1. Custom Transition Rule

Subclass `CellularAutomataRule` (sequential) or `CellularAutomataParallelRule`
(parallel):

```java
public class MyRule extends CellularAutomataRule {
    @Override
    public Cell transition(Cell cell, List<Cell> neighbors) {
        // inspect cell.getCurrentStatus() and neighbors
        // return a new Cell with the next state
    }
}
```

### 2. Custom State

`CellState` accepts any `Object` as its `value`:

```java
CellState complex = new CellState("state", Map.of("temp", 100, "pressure", 3));
```

Two `CellState` values are equal when both `key` and `value` are equal.

### 3. Custom 2D Neighborhood

Subclass `Neighborhood`:

```java
public class DiagonalNeighborhood extends Neighborhood {
    @Override
    public List<Cell> getNeighbors(Cell[][] matrix, int i, int j) {
        // return a list of neighbor cells
    }
}
```

### 4. Custom nD Neighborhood

Subclass `Neighborhood`:

```java
public class Custom3DNeighborhood extends Neighborhood {
    @Override
    public List<Cell> getNeighbors(CellGrid grid, int[] coords) {
        // return a list of neighbor cells
    }
}
```

### 5. CCA Pre-Processing

Override `refinements(Cell cell)` in your executor:

```java
@Override
public Cell refinements(Cell cell) {
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
