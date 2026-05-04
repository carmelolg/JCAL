+++
title = "Configuration Reference"
description = "All options available on CellularAutomataConfigurationBuilder."
weight = 4
+++

To run a cellular automaton with JCAL, you must first build a `CellularAutomataConfiguration`
using the fluent `CellularAutomataConfigurationBuilder`. Configuration objects are **immutable**
once built; all settings must be applied before calling `.build()`.

{{< code lang="JAVA" file="builder.java">}}{{< /code >}}

---

### Width

Sets the number of **columns** in the grid.

**Default: 100**

{{< table style="table-striped" >}}
| Parameter | Type |
| -- |:--:|
| `width` | `int` |
{{< /table >}}

```java
public CellularAutomataConfigurationBuilder setWidth(int width);
```

---

### Height

Sets the number of **rows** in the grid.

**Default: 100**

{{< table style="table-striped" >}}
| Parameter | Type |
| -- |:--:|
| `height` | `int` |
{{< /table >}}

```java
public CellularAutomataConfigurationBuilder setHeight(int height);
```

---

### Infinite loop

When set to `true`, the automaton runs indefinitely until interrupted.
When `false` (the default), the automaton stops after `totalIterations` steps.

**Default: `false`**

> **Note:** `setInfinite(true)` and `setTotalIterations` are mutually exclusive.

{{< table style="table-striped" >}}
| Parameter | Type |
| -- |:--:|
| `isInfinite` | `boolean` |
{{< /table >}}

```java
public CellularAutomataConfigurationBuilder setInfinite(boolean isInfinite);
```

---

### Total iterations

Sets the number of generations (steps) to simulate.

**Required when `isInfinite` is `false`.**

{{< table style="table-striped" >}}
| Parameter | Type |
| -- |:--:|
| `totalIterations` | `int` |
{{< /table >}}

```java
public CellularAutomataConfigurationBuilder setTotalIterations(int totalIterations);
```

---

### Default status

Sets the initial state applied to **every** cell in the grid before the initial condition
is overlaid. This is typically the "empty" or "dead" state.

**Required.**

{{< table style="table-striped" >}}
| Parameter | Type |
| -- |:--:|
| `defaultStatus` | `DefaultStatus` |
{{< /table >}}

```java
public CellularAutomataConfigurationBuilder setDefaultStatus(DefaultStatus defaultStatus);
```

---

### Initial condition

Provides the list of cells that start in a state **other than** the default. All other
cells are initialized with the default status.

{{< table style="table-striped" >}}
| Parameter | Type |
| -- |:--:|
| `initalState` | `List<DefaultCell>` |
{{< /table >}}

```java
public CellularAutomataConfigurationBuilder setInitalState(List<DefaultCell> initalState);
```

---

### Built-in neighborhood

Selects one of the pre-defined neighborhood shapes from the `NeighborhoodType` enum.

{{< table style="table-striped" >}}
| Parameter | Type | Values |
| -- |:--:|:--:|
| `neighborhoodType` | `NeighborhoodType` | `MOORE`, `VON_NEUMANN` |
{{< /table >}}

```java
public CellularAutomataConfigurationBuilder setNeighborhoodType(NeighborhoodType neighborhoodType);
```

---

### Custom neighborhood

Provides a custom neighborhood implementation. The class must extend `DefaultNeighborhood`.

> **Note:** Use either `setNeighborhoodType` or `setNeighborhood` — not both.

{{< table style="table-striped" >}}
| Parameter | Type |
| -- |:--:|
| `neighborhood` | `DefaultNeighborhood` |
{{< /table >}}

```java
public CellularAutomataConfigurationBuilder setNeighborhood(DefaultNeighborhood neighborhood);
```

---

## See also

- [Implementing a Rule](../basic-settings/) — how to write a transition function.
- [Custom State Objects](../custom-status/) — how to use rich state objects with the configuration.
