---
title: "Parallel Execution"
date: 2026-04-30
draft: false
summary: "Scale JCAL to large grids with CellularAutomataParallelRule."
weight: 80
toc: true
---

For large grids where the sequential executor becomes a bottleneck, JCAL provides
`CellularAutomataParallelRule` — a drop-in replacement that distributes the
transition function across multiple threads using Java parallel streams.

---

## When to Use

The parallel executor is beneficial when:

- Your grid is **large** (typically 500×500 or more in 2D, smaller thresholds in 3D/4D).
- The transition function (`transition`) is **computationally intensive** per cell.
- Your hardware has **multiple CPU cores** available.

For small grids or trivially cheap rules, the thread-management overhead of parallel
streams can make the parallel executor *slower* than the sequential one. Benchmark first.

---

## Usage

Extend `CellularAutomataParallelRule` instead of `CellularAutomataRule`. The
`transition` method signature is **identical** — no API changes required.

```java
public class GameOfLifeParallelRule extends CellularAutomataParallelRule {

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
GameOfLifeParallelRule executor = new GameOfLifeParallelRule();
ca = executor.run(ca);
```

---

## Thread Safety

JCAL guarantees that each call to `transition` is **independent**: the method receives
a snapshot of the grid taken before any cell is mutated. As long as your implementation
of `transition` does not access shared mutable state outside of its arguments, it is
thread-safe by design.

{{< callout type="warning" >}}
Do **not** use mutable instance fields in your `CellularAutomataParallelRule`
subclass unless they are explicitly thread-safe (e.g., `AtomicInteger`, `ConcurrentHashMap`).
The `transition` method is called concurrently for different cells.
{{< /callout >}}

---

## Parallel CCA (with Refinements)

The `refinements` hook is also available on `CellularAutomataParallelRule`:

```java
public class HeatDiffusionParallel extends CellularAutomataParallelRule {

    @Override
    public Cell refinements(Cell cell) {
        // Clamp temperature — called in parallel for each cell
        HeatStatus s = (HeatStatus) cell.getCurrentStatus();
        s.temperature = Math.max(0.0, Math.min(s.temperature, 1000.0));
        cell.setCurrentStatus(s);
        return cell;
    }

    @Override
    public Cell transition(Cell cell, List<Cell> neighbors) {
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

Internally, `CellularAutomataParallelRule` uses:

- **`CellularAutomataRunner`** — a `Callable<Void>` that applies `transition` to a single
  row of cells. Each runner receives only the coordinates for its assigned row via
  `CellGrid.coordinatesForRow(row)`, avoiding the O(n²) overhead of filtering the full
  coordinate list per task.
- **`CellularAutomataRefinementRunner`** — a `Callable<Void>` that applies `refinements`
  in parallel before the snapshot is taken, using the same row-scoped coordinate strategy.

Both classes live in `io.github.carmelolg.jcal.core` and are internal; do not use them directly.

---

## See Also

- [Implementing a Rule](../implementing-a-rule/) — the sequential executor pattern.
- [Complex Cellular Automata](../complex-ca/) — the `refinements` hook.
- [Configuration Reference](../configuration/) — grid configuration options.
