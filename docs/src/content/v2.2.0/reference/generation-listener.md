---
title: "Generation Listeners"
date: 2026-05-22
draft: false
summary: "React to every generation using GenerationListener and GridSnapshot."
weight: 80
toc: true
---

## Overview

`GenerationListener` is a `@FunctionalInterface` in the `core` package that lets you
hook into the automaton engine **after every completed generation**, without modifying
the transition rule itself.

After each generation, the engine:
1. Creates an immutable `GridSnapshot` of the current grid state.
2. Calls `onGeneration(generation, snapshot)` on every registered listener.

---

## GenerationListener

```java
@FunctionalInterface
public interface GenerationListener {
    void onGeneration(int generation, GridSnapshot snapshot);
}
```

| Parameter | Description |
|-----------|-------------|
| `generation` | 1-based index of the completed generation |
| `snapshot` | Immutable `GridSnapshot` of the grid after the transition |

Register listeners on any `CellularAutomataRule` (or `CellularAutomataParallelRule`)
before calling `run`:

```java
CellularAutomataRule rule = new GameOfLifeRule();

rule.addGenerationListener((gen, snap) -> {
    System.out.println("Generation: " + gen);
});

ca = rule.run(ca);
```

Multiple listeners can be registered; they are called in registration order.

{{< callout type="tip" >}}
Because `GenerationListener` is a `@FunctionalInterface`, you can supply a lambda,
a method reference, or an anonymous class.
{{< /callout >}}

---

## GridSnapshot

`GridSnapshot` is an **immutable** view of the grid at a specific generation. It is the
primary way to read grid state from a listener without touching the live grid.

### Creating a snapshot manually

```java
GridSnapshot snap = GridSnapshot.of(generation, ca.getGrid());
```

The engine creates snapshots automatically for every listener call — you only need this
for manual inspection outside of a listener.

### Reading cell states

**2D convenience:**

```java
CellState state = snap.getState(col, row);
```

**nD (3D, 4D):**

```java
CellState state = snap.getState(new int[]{x, y, z});
```

**Flat list (all cells, row-major order):**

```java
List<CellState> allStates = snap.getCellStates(); // unmodifiable
```

The flat list follows the same row-major ordering as `CellGrid.allCoordinates()`.

### Available methods

| Method | Description |
|--------|-------------|
| `getGeneration()` | The generation index at which the snapshot was taken |
| `getDimensions()` | The `GridDimensions` of this snapshot |
| `getCellStates()` | Unmodifiable flat list of all `CellState` values (row-major) |
| `getState(int col, int row)` | 2D convenience accessor |
| `getState(int[] coords)` | nD accessor |

---

## Common Patterns

### Count alive cells per generation

```java
CellState ALIVE = new CellState("alive", "1");

rule.addGenerationListener((gen, snap) -> {
    long count = snap.getCellStates().stream()
        .filter(s -> s.equals(ALIVE))
        .count();
    System.out.printf("Gen %d: %d alive cells%n", gen, count);
});
```

### Record history

```java
List<GridSnapshot> history = new ArrayList<>();
rule.addGenerationListener((gen, snap) -> history.add(snap));
rule.run(ca);
// history now contains one snapshot per generation
```

### Stop after a condition

Because `CellularAutomataRule.run` runs on the calling thread, you can interrupt it
from inside a listener:

```java
rule.addGenerationListener((gen, snap) -> {
    if (isStableState(snap)) {
        Thread.currentThread().interrupt();
    }
});
```

---

## See Also

- [Implementing a Rule](../implementing-a-rule/) — writing the transition function.
- [UI Visualisation](../ui-visualization/) — using `AutomataListener` to drive a Swing window.
