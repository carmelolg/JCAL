---
title: "Custom State Objects"
date: 2026-04-30
draft: false
summary: "Model richer per-cell data by extending CellState with a custom class."
weight: 60
toc: true
---

The built-in `CellState` holds a `key` (String) and a `value` (any Object), which
is sufficient for simple automata such as a binary-state Game of Life. For more complex
simulations — where each cell might carry temperature, pressure, material type, or any
other domain-specific data — you can extend `CellState` with a custom class.

---

## Overview

1. **Define your custom status** by extending `CellState` and adding fields.
2. **Cast in `singleRun`** — in your executor, cast `cell.getCurrentStatus()` to your
   subclass to access the extra fields.
3. **Use your custom status** as the default status and in the initial condition list.

---

## Example: Custom Status for Game of Life

The `GoLStatus` class wraps a simple boolean `isAlive` field and produces a readable
`toString()` so the grid renders clearly.

```java
public class GoLStatus extends CellState {

    public boolean isAlive;

    public GoLStatus(boolean isAlive) {
        super("GoLStatus", isAlive);
        this.isAlive = isAlive;
    }

    @Override
    public String toString() {
        return isAlive ? "1 " : "0 ";
    }
}
```

### The Executor

Cast `currentStatus` to `GoLStatus` inside `singleRun`:

```java
public class GoLCustomExecutor extends CellularAutomataExecutor {

    @Override
    public Cell singleRun(Cell cell, List<Cell> neighbors) {
        long aliveCount = neighbors.stream()
            .filter(n -> ((GoLStatus) n.getCurrentStatus()).isAlive)
            .count();

        boolean isAlive = ((GoLStatus) cell.getCurrentStatus()).isAlive;
        boolean nextAlive = (!isAlive && aliveCount == 3)
                          || (isAlive && (aliveCount == 2 || aliveCount == 3));

        Cell next = new Cell(
            new GoLStatus(nextAlive),
            cell.getCol(), cell.getRow()
        );
        return next;
    }
}
```

### The Application Entry Point

Use `GoLStatus` as the `defaultStatus` and in the initial condition:

```java
GoLStatus dead = new GoLStatus(false);

List<Cell> seed = new ArrayList<>();
seed.add(new Cell(new GoLStatus(true), 1, 1));
seed.add(new Cell(new GoLStatus(true), 1, 2));
seed.add(new Cell(new GoLStatus(true), 1, 3));

CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
    .setWidth(10).setHeight(10)
    .setTotalIterations(5)
    .setDefaultStatus(dead)
    .setNeighborhoodType(NeighborhoodType.MOORE)
    .setInitalState(seed)
    .build();

CellularAutomata ca = new CellularAutomata(config);
ca = new GoLCustomExecutor().run(ca);
System.out.println(ca);
```

---

## Example: Numeric State (Heat Diffusion)

For simulations that require a numeric quantity per cell:

```java
public class HeatStatus extends CellState {

    public double temperature;

    public HeatStatus(double temperature) {
        super("heat", temperature);
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return String.format("%.1f ", temperature);
    }
}
```

Use it in an executor that averages neighbors' temperatures:

```java
public class HeatDiffusionExecutor extends CellularAutomataExecutor {

    @Override
    public Cell singleRun(Cell cell, List<Cell> neighbors) {
        double avgTemp = neighbors.stream()
            .mapToDouble(n -> ((HeatStatus) n.getCurrentStatus()).temperature)
            .average()
            .orElse(0.0);

        double current = ((HeatStatus) cell.getCurrentStatus()).temperature;
        double next = (current + avgTemp) / 2.0;

        return new Cell(new HeatStatus(next), cell.getCol(), cell.getRow());
    }
}
```

---

## Tips

- Two `CellState` values are **equal** when both `key` and `value` are equal.
  If you store mutable objects in `value`, override `equals` / `hashCode` in your
  subclass for correct comparison semantics.
- The `value` field accepts any `Object`, so you can store a `Map`, a POJO, a
  numeric type, or any domain object you need.
- Override `toString()` in your custom status to get useful output from
  `CellularAutomata.toString()`.
- For automata that accumulate quantities between steps (e.g., flows, counters), pair
  the custom status with the [refinements hook](../complex-ca/) for pre-processing.

---

## See Also

- [Complex Cellular Automata](../complex-ca/) — add a refinement step before neighbor lookup.
- [Configuration Reference](../configuration/) — how to wire custom status into the builder.
- [Implementing a Rule](../implementing-a-rule/) — the executor pattern.
