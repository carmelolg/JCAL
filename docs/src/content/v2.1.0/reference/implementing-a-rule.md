---
title: "Implementing a Rule"
date: 2026-04-30
draft: false
summary: "How to write a transition function using CellularAutomataRule."
weight: 30
toc: true
---

## The Transition Function

In JCAL, the **transition function** (σ) is the core of any cellular automaton. It
determines how each cell evolves from one generation to the next.

To define a transition function, extend `CellularAutomataRule` and implement
`transition`. JCAL calls this method **once per cell per generation**, passing the
current cell and its neighbors. Return a **new** `Cell` carrying the cell's
next state — never mutate the input cell.

```
transition(cell, neighbors) → next cell state
```

---

## Minimal Example

```java
public class GameOfLifeRule extends CellularAutomataRule {

    private static final CellState DEAD  = new CellState("dead",  "0");
    private static final CellState ALIVE = new CellState("alive", "1");

    @Override
    public Cell transition(Cell cell, List<Cell> neighbors) {
        long aliveCount = neighbors.stream()
            .filter(n -> n.getCurrentStatus().equals(ALIVE))
            .count();

        boolean isAlive = cell.getCurrentStatus().equals(ALIVE);
        Cell next = new Cell(DEAD, cell.getCol(), cell.getRow());

        if (!isAlive && aliveCount == 3) {
            next.setCurrentStatus(ALIVE);       // birth
        } else if (isAlive && (aliveCount == 2 || aliveCount == 3)) {
            next.setCurrentStatus(ALIVE);       // survival
        }

        return next;
    }
}
```

{{< callout type="tip" >}}
Declare `CellState` constants as static fields (or outside the method) to avoid
creating thousands of equal objects per generation. `CellState.equals` checks both
`key` and `value`, so reuse is safe.
{{< /callout >}}

---

## Running the Automaton

Wire the rule together with a configured grid and call `run`:

```java
CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
    .setWidth(20).setHeight(20)
    .setTotalIterations(10)
    .setDefaultStatus(DEAD)
    .setNeighborhoodType(NeighborhoodType.MOORE)
    .setInitalState(seedCells)
    .build();

CellularAutomata ca = new CellularAutomata(config);
GameOfLifeRule rule = new GameOfLifeRule();
ca = rule.run(ca);
System.out.println(ca);
```

`run` applies the transition function for the configured number of iterations (or
indefinitely when `setInfinite(true)`) and returns the updated `CellularAutomata`.

---

## Anonymous Rule (Inline)

For quick experiments, you can define the rule inline without a named class:

```java
CellularAutomataRule rule = new CellularAutomataRule() {
    @Override
    public Cell transition(Cell cell, List<Cell> neighbors) {
        // rule logic here
        return new Cell(cell.getCurrentStatus(), cell.getCol(), cell.getRow());
    }
};
```

Named classes are recommended for any non-trivial rule because they are easier to
test, reuse, and document.

---

## Parallel Execution

For large grids, replace `CellularAutomataRule` with
`CellularAutomataParallelRule`. The `transition` signature is identical; JCAL
distributes the work across threads automatically using Java parallel streams.

```java
public class MyParallelRule extends CellularAutomataParallelRule {
    @Override
    public Cell transition(Cell cell, List<Cell> neighbors) {
        // exact same logic as the sequential version
    }
}
```

See [Parallel Execution](../parallel-execution/) for more details and caveats.

---

## The CCA Refinements Hook

For **Complex Cellular Automata** that need per-cell pre-processing before neighbor
lookup, override the `refinements` method:

```java
@Override
public Cell refinements(Cell cell) {
    // Adjust internal state before neighbors are evaluated this generation
    return cell;
}
```

See [Complex Cellular Automata](../complex-ca/) for a full explanation and example.

---

## Observing Generations with Listeners

`CellularAutomataRule` supports registering one or more **`GenerationListener`** callbacks
that are invoked after every completed generation. Listeners receive a 1-based generation
index and an immutable **`GridSnapshot`** of the grid state.

```java
GameOfLifeRule rule = new GameOfLifeRule();

rule.addGenerationListener((gen, snap) ->
    System.out.printf("Generation %d — alive cells: %d%n",
        gen,
        snap.getCellStates().stream()
            .filter(s -> s.equals(ALIVE)).count()));

ca = rule.run(ca);
```

Multiple listeners are supported and are called in registration order. For Swing
visualisation, see [UI Visualisation](../ui-visualization/).

---

## See Also

- [Getting Started](../getting-started/) — a complete Quick Start example.
- [Configuration Reference](../configuration/) — all builder options.
- [Custom State Objects](../custom-state/) — enrich cell state beyond key/value strings.
