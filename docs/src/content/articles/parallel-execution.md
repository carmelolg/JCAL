---
title: "Parallel Execution"
date: 2025-01-01
draft: false
summary: "Scale JCAL to large grids with CellularAutomataParallelExecutor."
weight: 80
toc: true
tags: ["parallel", "performance"]
---

For large grids where the sequential executor becomes a bottleneck, JCAL provides
`CellularAutomataParallelExecutor` — a drop-in replacement that distributes the
transition function across multiple threads using Java parallel streams.

---

## When to Use

The parallel executor is beneficial when:

- Your grid is **large** (typically 500×500 or more in 2D, smaller thresholds in 3D/4D).
- The transition function (`singleRun`) is **computationally intensive** per cell.
- Your hardware has **multiple CPU cores** available.

For small grids or trivially cheap rules, the thread-management overhead of parallel
streams can make the parallel executor *slower* than the sequential one. Benchmark first.

---

## Usage

Extend `CellularAutomataParallelExecutor` instead of `CellularAutomataExecutor`. The
`singleRun` method signature is **identical** — no API changes required.

```java
public class GameOfLifeParallelExecutor extends CellularAutomataParallelExecutor {

    private static final CellState DEAD  = new CellState("dead",  "0");
    private static final CellState ALIVE = new CellState("alive", "1");

    @Override
    public Cell singleRun(Cell cell, List<Cell> neighbors) {
        long aliveCount = neighbors.stream()
            .filter(n -> n.getCurrentStatus().equals(ALIVE))
            .count();

        boolean isAlive = cell.getCurrentStatus().equals(ALIVE);
        Cell next = new Cell(DEAD, cell.getCol(), cell.getRow());

        if (!isAlive && aliveCount == 3) {
            next.setCurrentStatus(ALIVE);
        } else if (isAlive && (aliveCount == 2 || aliveCount == 3)) {
            next.setCurrentStatus(ALIVE);
        }

        return next;
    }
}
```

Run it exactly as you would the sequential executor:

```java
CellularAutomata ca = new CellularAutomata(config);
GameOfLifeParallelExecutor executor = new GameOfLifeParallelExecutor();
ca = executor.run(ca);
```

---

## Thread Safety

JCAL guarantees that each call to `singleRun` is **independent**: the method receives
a snapshot of the grid taken before any cell is mutated. As long as your implementation
of `singleRun` does not access shared mutable state outside of its arguments, it is
thread-safe by design.

{{< callout type="warning" >}}
Do **not** use mutable instance fields in your `CellularAutomataParallelExecutor`
subclass unless they are explicitly thread-safe (e.g., `AtomicInteger`, `ConcurrentHashMap`).
The `singleRun` method is called concurrently for different cells.
{{< /callout >}}

---

## Parallel CCA (with Refinements)

The `refinements` hook is also available on `CellularAutomataParallelExecutor`:

```java
public class HeatDiffusionParallel extends CellularAutomataParallelExecutor {

    @Override
    public Cell refinements(Cell cell) {
        // Clamp temperature — called in parallel for each cell
        HeatStatus s = (HeatStatus) cell.getCurrentStatus();
        s.temperature = Math.max(0.0, Math.min(s.temperature, 1000.0));
        cell.setCurrentStatus(s);
        return cell;
    }

    @Override
    public Cell singleRun(Cell cell, List<Cell> neighbors) {
        double avg = neighbors.stream()
            .mapToDouble(n -> ((HeatStatus) n.getCurrentStatus()).temperature)
            .average().orElse(0.0);

        double next = (((HeatStatus) cell.getCurrentStatus()).temperature + avg) / 2.0;
        return new Cell(new HeatStatus(next), cell.getCol(), cell.getRow());
    }
}
```

The refinement phase and the transition phase are each fully parallelized internally.

---

## Internal Architecture

Internally, `CellularAutomataParallelExecutor` uses:

- **`CellularAutomataRunner`** — a `Callable` that applies `singleRun` to a subset
  of cells in parallel.
- **`CellularAutomataRefinementRunner`** — a `Callable` that applies `refinements`
  in parallel before the snapshot is taken.

These are internal classes; do not use them directly.

---

## See Also

- [Implementing a Rule](../implementing-a-rule/) — the sequential executor pattern.
- [Complex Cellular Automata](../complex-ca/) — the `refinements` hook.
- [Configuration Reference](../configuration/) — grid configuration options.
