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

### CellGrid Interface

The `CellGrid` interface abstracts away grid dimensionality. All executors and
neighborhoods operate on `CellGrid` internally.

| Method | Description |
|--------|-------------|
| `get(int[] coords)` | Return the cell at the given n-dimensional coordinates |
| `set(int[] coords, DefaultCell cell)` | Update a cell |
| `isInside(int[] coords)` | Check whether coordinates are within bounds |
| `allCoordinates()` | Stream every coordinate array in the grid |
| `dimensions()` | Return the `GridDimensions` descriptor |

### GridDimensions

`GridDimensions` is an immutable descriptor that holds the sizes of each axis, the
strides for flat-array indexing, and the total number of cells.

```java
GridDimensions dims = new GridDimensions(new int[]{ 10, 10, 5 }); // 10×10×5
int totalCells = dims.getTotal();      // 500
int[] sizes    = dims.getSizes();      // [10, 10, 5]
```

### CellGridFlat

`CellGridFlat` is the flat-array implementation used for 3D and 4D grids. It uses
stride-based indexing to map n-dimensional coordinates to a 1D array.

You do not instantiate `CellGridFlat` directly — JCAL creates it automatically when
you configure `depth` (and optionally `time`) in the builder.

---

## Configuring a 3D Grid

Add `.setDepth(int)` to your builder call. JCAL automatically selects `CellGridFlat`
and the 3D neighborhood variant.

```java
CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
    .setWidth(10)
    .setHeight(10)
    .setDepth(10)                           // third dimension (z)
    .setTotalIterations(5)
    .setDefaultStatus(dead)
    .setNeighborhoodType(NeighborhoodType.MOORE)  // resolves to Moore3DNeighborhood
    .setInitalState(seedCells)
    .build();
```

`DefaultCell` for a 3D grid takes three coordinates:

```java
new DefaultCell(alive, x, y, z)
```

---

## Configuring a 4D Grid

Add `.setDepth(int)` **and** `.setTime(int)`:

```java
CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
    .setWidth(5).setHeight(5)
    .setDepth(5).setTime(5)                 // 5×5×5×5 grid
    .setTotalIterations(3)
    .setDefaultStatus(dead)
    .setNeighborhoodType(NeighborhoodType.MOORE)  // resolves to Moore4DNeighborhood
    .build();
```

`DefaultCell` for a 4D grid takes four coordinates:

```java
new DefaultCell(alive, x, y, z, t)
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

    private static final DefaultStatus DEAD  = new DefaultStatus("dead",  0);
    private static final DefaultStatus ALIVE = new DefaultStatus("alive", 1);

    @Override
    public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {
        long aliveCount = neighbors.stream()
            .filter(n -> n.getCurrentStatus().equals(ALIVE))
            .count();

        boolean isAlive = cell.getCurrentStatus().equals(ALIVE);
        boolean nextAlive = (!isAlive && aliveCount == 4)
                          || (isAlive && (aliveCount == 5 || aliveCount == 6));

        int[] c = cell.getCoordinates();
        DefaultCell next = new DefaultCell(DEAD, c[0], c[1], c[2]);
        if (nextAlive) next.setCurrentStatus(ALIVE);
        return next;
    }
}
```

Running it:

```java
DefaultStatus dead  = new DefaultStatus("dead",  0);
DefaultStatus alive = new DefaultStatus("alive", 1);

// A small 3D seed
List<DefaultCell> seed = List.of(
    new DefaultCell(alive, 5, 5, 4),
    new DefaultCell(alive, 5, 5, 5),
    new DefaultCell(alive, 5, 5, 6)
);

CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
    .setWidth(10).setHeight(10).setDepth(10)
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
grid.allCoordinates().forEach(coords -> {
    DefaultCell cell = grid.get(coords);
    System.out.println(Arrays.toString(coords) + " → " + cell.getCurrentStatus());
});
```

For 2D backward compatibility, `ca.getMap()` still returns a `DefaultCell[][]`.

---

## Custom nD Neighborhoods

See [Neighborhoods — 3D/4D Custom Neighborhood](../neighborhoods/#3d4d-custom-neighborhood)
for a full example of extending `DefaultNeighborhoodND`.

---

## See Also

- [Neighborhoods](../neighborhoods/) — all built-in and custom neighborhood strategies.
- [Implementing a Rule](../implementing-a-rule/) — the executor pattern.
- [Configuration Reference](../configuration/) — `setDepth` and `setTime` builder options.
- [Design / Architecture](../../design/architecture/) — `CellGrid` interface and `CellGridFlat` internals.
