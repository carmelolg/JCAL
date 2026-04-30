---
title: "Configuration Reference"
date: 2025-01-01
draft: false
summary: "All options available on CellularAutomataConfigurationBuilder."
weight: 40
toc: true
tags: ["configuration", "builder"]
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
| `setDepth(int)` | `int` | — | For 3D | Depth dimension (z-axis) |
| `setTime(int)` | `int` | — | For 4D | Fourth dimension |
| `setTotalIterations(int)` | `int` | `0` | Yes (unless infinite) | Number of generations to simulate |
| `setInfinite(boolean)` | `boolean` | `false` | No | Run until interrupted |
| `setDefaultStatus(DefaultStatus)` | `DefaultStatus` | — | **Yes** | State applied to every cell at init |
| `setInitalState(List<DefaultCell>)` | `List<DefaultCell>` | empty | No | Cells starting in a non-default state |
| `setNeighborhoodType(NeighborhoodType)` | `NeighborhoodType` | — | One of these | Select a built-in neighborhood |
| `setNeighborhood(DefaultNeighborhood)` | `DefaultNeighborhood` | — | One of these | Provide a custom 2D neighborhood |

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

## Depth *(3D/4D only)*

Sets the third dimension (z-axis) of the grid. Required when building a 3D or 4D
cellular automaton using `CellGridFlat`.

```java
public CellularAutomataConfigurationBuilder setDepth(int depth);
```

---

## Time *(4D only)*

Sets the fourth dimension. Required when building a 4D cellular automaton.

```java
public CellularAutomataConfigurationBuilder setTime(int time);
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
public CellularAutomataConfigurationBuilder setDefaultStatus(DefaultStatus defaultStatus);
```

---

## Initial Condition

Provides the list of cells that start in a state **other than** the default. All
other cells are initialized with `defaultStatus`.

```java
public CellularAutomataConfigurationBuilder setInitalState(List<DefaultCell> initalState);
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

Provides a custom 2D neighborhood. The class must extend `DefaultNeighborhood` (2D)
or `DefaultNeighborhoodND` (3D/4D).

```java
public CellularAutomataConfigurationBuilder setNeighborhood(DefaultNeighborhood neighborhood);
```

See [Neighborhoods](../neighborhoods/) for examples.

---

## See Also

- [Getting Started](../getting-started/) — complete Quick Start example.
- [Neighborhoods](../neighborhoods/) — built-in and custom neighborhood strategies.
- [3D and 4D Support](../3d-4d-support/) — multi-dimensional grid configuration.
