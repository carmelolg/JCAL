---
title: "Complex Cellular Automata"
description: "Model advanced phenomena using the refinements hook and custom state objects."
weight: 1
---

A **Complex Cellular Automaton (CCA)** extends the standard CA model with a
**pre-processing step** that runs before the transition function reads its neighbors.
This refinement step gives each cell the opportunity to update its internal state
(for example, accumulate incoming flows or decrement a counter) before those updated
values are seen by neighboring cells in the same generation.

## When to use a CCA

Use the refinements hook when:

- A cell's next state depends on **accumulated quantities** that must be resolved
  before the transition function is applied (e.g., heat diffusion, lava flow, erosion).
- Your simulation requires cells to perform **bookkeeping between generations** — such
  as decrementing a lifetime counter or normalizing a fractional flow value.
- The transition function alone is insufficient to model the phenomenon correctly.

## Data flow per generation

```
for each generation:
  1. refinements(cell)        — applied to every cell  [optional CCA hook]
  2. snapshot the grid        — clone the map before any mutation
  3. singleRun(cell, neighbors) — per cell, reads the snapshot, returns next state
  4. copy results back        — update the main map with all new states
```

## Implementing refinements

Override `refinements(DefaultCell cell)` in your executor class. Return the modified cell.

```java
public class MyComplexRule extends CellularAutomataExecutor {

    @Override
    public DefaultCell refinements(DefaultCell cell) {
        MyStatus status = (MyStatus) cell.getCurrentStatus();
        status.normalizeFlow();
        cell.setCurrentStatus(status);
        return cell;
    }

    @Override
    public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {
        MyStatus current = (MyStatus) cell.getCurrentStatus();
        // ... compute next state ...
        return new DefaultCell(nextStatus, cell.getCol(), cell.getRow());
    }
}
```

## Combining with a custom status

CCA implementations almost always pair the `refinements` hook with a
[custom state object](../custom-status/) that carries the extra per-cell data.

### Example pattern: heat diffusion

```java
public class HeatStatus extends DefaultStatus {
    public double temperature;

    public HeatStatus(double temperature) {
        super("heat", temperature);
        this.temperature = temperature;
    }
}

public class HeatDiffusionExecutor extends CellularAutomataExecutor {

    @Override
    public DefaultCell refinements(DefaultCell cell) {
        HeatStatus s = (HeatStatus) cell.getCurrentStatus();
        s.temperature = Math.max(0.0, Math.min(s.temperature, 1000.0));
        cell.setCurrentStatus(s);
        return cell;
    }

    @Override
    public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {
        double avgTemp = neighbors.stream()
            .mapToDouble(n -> ((HeatStatus) n.getCurrentStatus()).temperature)
            .average()
            .orElse(0.0);

        double current = ((HeatStatus) cell.getCurrentStatus()).temperature;
        double next = (current + avgTemp) / 2.0;

        return new DefaultCell(new HeatStatus(next), cell.getCol(), cell.getRow());
    }
}
```

---

## See also

- [Custom State Objects](../custom-status/) — how to extend `DefaultStatus`.
- [Implementing a Rule](../basic-settings/) — the standard executor pattern.
- [Configuration Reference](../builder-settings/) — builder options.
