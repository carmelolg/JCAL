---
title: "Configuration Reference"
date: 2026-04-30
draft: false
summary: "All options available on CellularAutomataConfigurationBuilder."
weight: 40
toc: true
---

To run a cellular automaton with JCAL, you must first build a
`CellularAutomataConfiguration` using the fluent `CellularAutomataConfigurationBuilder`.
Configuration objects are **immutable** once built; all settings must be applied before
calling `.build()`.

```java
CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
    .setWidth(50)
    .setHeight(50)
    .setTotalIterations(100)
    .setDefaultStatus(dead)
    .setNeighborhoodType(NeighborhoodType.MOORE)
    .setInitalState(seedCells)
    .build();
```

---

## Reference Table

| Builder Method | Type | Default | Required | Description |
|----------------|------|---------|----------|-------------|
| `setWidth(int)` | `int` | `100` | No | Number of columns in the 2D grid |
| `setHeight(int)` | `int` | `100` | No | Number of rows in the 2D grid |
| `setDimensions(int...)` | `int...` | — | For 3D/4D | Grid dimensions: `(x,y,z)` or `(x,y,z,w)` |
| `setTotalIterations(int)` | `int` | `0` | Yes (unless infinite) | Number of generations to simulate |
| `setInfinite(boolean)` | `boolean` | `false` | No | Run until interrupted |
| `setDefaultStatus(CellState)` | `CellState` | — | **Yes** | State applied to every cell at init |
| `setInitalState(List<Cell>)` | `List<Cell>` | empty | No | Cells starting in a non-default state |
| `setNeighborhoodType(NeighborhoodType)` | `NeighborhoodType` | — | One of these | Select a built-in neighborhood |
| `setNeighborhood(Neighborhood)` | `Neighborhood` | — | One of these | Provide a custom 2D neighborhood |

{{< callout type="warning" >}}
Use **exactly one** of `setNeighborhoodType` or `setNeighborhood`.  
`setTotalIterations` and `setInfinite(true)` are mutually exclusive.
{{< /callout >}}

---

## Width

Sets the number of **columns** in the grid (x-axis). Default: `100`.

```java
public CellularAutomataConfigurationBuilder setWidth(int width);
```

---

## Height

Sets the number of **rows** in the grid (y-axis). Default: `100`.

```java
public CellularAutomataConfigurationBuilder setHeight(int height);
```

---

## Dimensions *(3D/4D)*

Sets all dimensions at once for 3D or 4D grids. Replaces `setWidth`/`setHeight`
when building multi-dimensional cellular automata.

```java
// 3D: x, y, z
public CellularAutomataConfigurationBuilder setDimensions(int... dims);

// Usage
builder.setDimensions(10, 10, 10);  // 3D  10×10×10
builder.setDimensions(5, 5, 5, 5);  // 4D  5×5×5×5
```

---

## Total Iterations

Sets the number of **generations** (steps) to simulate. Required when
`setInfinite(false)` (the default).

```java
public CellularAutomataConfigurationBuilder setTotalIterations(int totalIterations);
```

---

## Infinite Loop

When `true`, the automaton runs indefinitely until the JVM is interrupted. When
`false` (default), it stops after `totalIterations` steps.

```java
public CellularAutomataConfigurationBuilder setInfinite(boolean isInfinite);
```

---

## Default Status

Sets the initial state applied to **every** cell in the grid before the initial
condition is overlaid. This is typically the "empty" or "dead" state.  
**Required.**

```java
public CellularAutomataConfigurationBuilder setDefaultStatus(CellState defaultStatus);
```

---

## Initial Condition

Provides the list of cells that start in a state **other than** the default. All
other cells are initialized with `defaultStatus`.

```java
public CellularAutomataConfigurationBuilder setInitalState(List<Cell> initalState);
```

{{< callout type="note" >}}
The method is spelled `setInitalState` (one `t`) — this is intentional in the public
API and will not be changed to preserve backward compatibility.
{{< /callout >}}

---

## Built-in Neighborhood

Selects one of the pre-defined neighborhood shapes. JCAL automatically resolves the
correct implementation for 2D, 3D, or 4D based on the grid dimensions in the config.

```java
public CellularAutomataConfigurationBuilder setNeighborhoodType(NeighborhoodType neighborhoodType);
```

| Value | 2D | 3D | 4D |
|-------|----|----|-----|
| `NeighborhoodType.MOORE` | 8 neighbors | 26 neighbors | 80 neighbors |
| `NeighborhoodType.VON_NEUMANN` | 4 neighbors | 6 neighbors | 8 neighbors |

---

## Custom Neighborhood

Provides a custom 2D neighborhood. The class must extend `Neighborhood` (2D)
or `Neighborhood` (3D/4D).

```java
public CellularAutomataConfigurationBuilder setNeighborhood(Neighborhood neighborhood);
```

See [Neighborhoods](../neighborhoods/) for examples.

---

## See Also

- [Getting Started](../getting-started/) — complete Quick Start example.
- [Neighborhoods](../neighborhoods/) — built-in and custom neighborhood strategies.
- [3D and 4D Support](../3d-4d-support/) — multi-dimensional grid configuration.
