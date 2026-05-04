---
title: "3D and 4D Support"
date: 2025-01-01
draft: false
summary: "Run cellular automata on 3D and 4D grids with built-in neighborhoods and a flat-array grid."
weight: 90
toc: true
tags: ["3d", "4d", "grid", "multidimensional"]
---

JCAL supports cellular automata on **3D and 4D grids** out of the box. The same
builder, executor, and neighborhood APIs you use for 2D work for higher dimensions —
with a few additions for describing the extra dimensions.

---

## Core Concepts

### CellGrid

`CellGrid` is the unified n-dimensional grid class (2D–4D) backed by a flat array with
stride-based indexing. All executors and neighbourhoods operate on `CellGrid` internally.

| Method | Description |
|--------|-------------|
| `get(int[] coords)` | Return the cell at the given n-dimensional coordinates |
| `set(int[] coords, Cell cell)` | Update a cell |
| `allCoordinates()` | Return an unmodifiable list of every coordinate array in the grid |
| `getDimensions()` | Return the `GridDimensions` descriptor |
| `is2D()` | Convenience check for 2D grids |

### GridDimensions

`GridDimensions` is a Java 16 **record** — an immutable descriptor that holds the sizes
of each axis, the strides for flat-array indexing, and the total number of cells.

```java
GridDimensions dims = new GridDimensions(10, 10, 5); // 10×10×5
int totalCells = dims.getTotalCells(); // 500
int[] sizes    = dims.sizes();         // [10, 10, 5]
```

`CellGrid` is created automatically by `CellularAutomata` based on the configured
dimensions — you never instantiate it directly.

---

## Configuring a 3D Grid

Use `setDimensions(x, y, z)` in the builder. JCAL automatically selects the correct
3D neighbourhood.

```java
CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
    .setDimensions(10, 10, 10)                      // 3D: x, y, z
    .setTotalIterations(5)
    .setDefaultStatus(dead)
    .setNeighborhoodType(NeighborhoodType.MOORE)    // resolves to Moore3DNeighborhood
    .setInitalState(seedCells)
    .build();
```

`Cell` for a 3D grid takes three coordinates:

```java
new Cell(alive, x, y, z)
```

---

## Configuring a 4D Grid

Use `setDimensions(x, y, z, w)`:

```java
CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
    .setDimensions(5, 5, 5, 5)                      // 4D: x, y, z, w
    .setTotalIterations(3)
    .setDefaultStatus(dead)
    .setNeighborhoodType(NeighborhoodType.MOORE)    // resolves to Moore4DNeighborhood
    .build();
```

`Cell` for a 4D grid takes four coordinates:

```java
new Cell(alive, x, y, z, w)
```

---

## Built-in Neighborhood Sizes

| `NeighborhoodType` | 2D | 3D | 4D |
|---------------------|----|----|-----|
| `MOORE` | 8 | 26 | 80 |
| `VON_NEUMANN` | 4 | 6 | 8 |

The correct class is resolved automatically based on the number of dimensions in the
configuration.

---

## Example: Carter Bays' 3D Game of Life

A classic 3D Life rule: a dead cell is born if it has exactly 4 live neighbors; a live
cell survives if it has 5 or 6 live neighbors.

```java
public class GameOfLife3DExecutor extends CellularAutomataExecutor {

    private static final CellState DEAD  = new CellState("dead",  0);
    private static final CellState ALIVE = new CellState("alive", 1);

    @Override
    public Cell singleRun(Cell cell, List<Cell> neighbors) {
        long aliveCount = neighbors.stream()
            .filter(n -> n.getCurrentStatus().equals(ALIVE))
            .count();

        boolean isAlive = cell.getCurrentStatus().equals(ALIVE);
        boolean nextAlive = (!isAlive && aliveCount == 4)
                          || (isAlive && (aliveCount == 5 || aliveCount == 6));

        int[] c = cell.getCoordinates();
        Cell next = new Cell(DEAD, c[0], c[1], c[2]);
        if (nextAlive) next.setCurrentStatus(ALIVE);
        return next;
    }
}
```

Running it:

```java
CellState dead  = new CellState("dead",  0);
CellState alive = new CellState("alive", 1);

// A small 3D seed
List<Cell> seed = List.of(
    new Cell(alive, 5, 5, 4),
    new Cell(alive, 5, 5, 5),
    new Cell(alive, 5, 5, 6)
);

CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
    .setDimensions(10, 10, 10)
    .setTotalIterations(3)
    .setDefaultStatus(dead)
    .setNeighborhoodType(NeighborhoodType.MOORE)
    .setInitalState(seed)
    .build();

CellularAutomata ca = new CellularAutomata(config);
ca = new GameOfLife3DExecutor().run(ca);
```

---

## Accessing the Grid

To iterate over the grid in your own code, use the `CellGrid` returned by
`CellularAutomata.getGrid()`:

```java
CellGrid grid = ca.getGrid();
for (int[] coords : grid.allCoordinates()) {
    Cell cell = grid.get(coords);
    System.out.println(Arrays.toString(coords) + " → " + cell.getCurrentStatus());
}
```

---

## Custom nD Neighborhoods

See [Neighborhoods — 3D/4D Custom Neighborhood](../neighborhoods/#3d4d-custom-neighborhood)
for a full example of extending `Neighborhood`.

---

## See Also

- [Neighborhoods](../neighborhoods/) — all built-in and custom neighborhood strategies.
- [Implementing a Rule](../implementing-a-rule/) — the executor pattern.
- [Configuration Reference](../configuration/) — `setDimensions` builder option.
- [Design / Architecture](../../design/architecture/) — `CellGrid` interface and `CellGrid` internals.
