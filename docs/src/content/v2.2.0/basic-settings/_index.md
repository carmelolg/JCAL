+++
title = "Implementing a Rule"
description = "How to write a transition function using CellularAutomataRule."
weight = 3
+++

## The transition function

In JCAL, the **transition function** (σ) is the core of any cellular automaton. It
determines how each cell evolves from one generation to the next.

To define a transition function, extend `CellularAutomataRule` and implement the
`transition` method. JCAL calls this method **once per cell per generation**, passing the
current cell and its neighbors. Return a new `DefaultCell` carrying the cell's next state.

```
transition(cell, neighbors) → next cell state
```

### Example: Conway's Game of Life executor

{{< code lang="JAVA" file="gol-executor.java">}}{{< /code >}}

### Running the cellular automaton

Once you have an executor, wire it together with a configured grid and call `run`:

{{< code lang="JAVA" file="main.java">}}{{< /code >}}

---

## Parallel execution

For large grids, replace `CellularAutomataRule` with
`CellularAutomataParallelRule`. The `transition` signature is identical; JCAL
distributes the work across threads automatically.

```java
public class MyParallelRule extends CellularAutomataParallelRule {
    @Override
    public DefaultCell transition(DefaultCell cell, List<DefaultCell> neighbors) {
        // same logic as the sequential version
    }
}
```

---

## See also

- [Getting Started](../getting-started/) — a complete Quick Start example.
- [Configuration Reference](../builder-settings/) — all builder options.
- [Custom State Objects](../custom-status/) — enrich cell state beyond key/value strings.
- [Complex Cellular Automata](../cca/) — the refinements hook for CCA pre-processing.
