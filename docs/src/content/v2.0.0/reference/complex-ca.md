---
title: "Complex Cellular Automata"
date: 2026-04-30
draft: false
summary: "Model advanced phenomena with the refinements hook and custom state objects."
weight: 70
toc: true
---

A **Complex Cellular Automaton (CCA)** extends the standard CA model with a
**pre-processing step** that runs before the transition function reads its neighbors.
This *refinement step* lets each cell update its internal state (for example, normalize
an accumulated flow or decrement a lifetime counter) before those updated values are
seen by neighboring cells.

---

## When to Use a CCA

Use the refinements hook when:

- A cell's next state depends on **accumulated quantities** that must be resolved
  before the transition function runs (e.g., heat diffusion, lava flow, erosion).
- Your simulation requires **per-cell bookkeeping between generations** — decrementing
  a counter, normalizing a fractional flow, clamping a temperature to physical bounds.
- The `transition` transition function alone is insufficient to model the phenomenon.

---

## Data Flow per Generation

```
for each generation:
  1. refinements(cell)           — applied to every cell  [optional CCA hook]
  2. snapshot the grid           — clone the map before any mutation
  3. transition(cell, neighbors)  — per cell, reads the snapshot, returns next state
  4. copy results back           — update the main map with all new states
```

The snapshot in step 2 ensures that all cells in `transition` see the *pre-transition*
state of their neighbors, not the partially-updated state.

---

## Implementing Refinements

Override `refinements(Cell cell)` in your executor and return the (possibly
modified) cell:

```java
public class MyComplexRule extends CellularAutomataRule {

    @Override
    public Cell refinements(Cell cell) {
        // Example: clamp temperature before neighbor lookup
        MyStatus status = (MyStatus) cell.getCurrentStatus();
        status.temperature = Math.max(0.0, Math.min(status.temperature, 1000.0));
        cell.setCurrentStatus(status);
        return cell;
    }

    @Override
    public Cell transition(Cell cell, List<Cell> neighbors) {
        // Neighbors already have their refined state
        MyStatus current = (MyStatus) cell.getCurrentStatus();
        // ... compute next state ...
        return new Cell(nextStatus, cell.getCol(), cell.getRow());
    }
}
```

The default implementation of `refinements` in the base class simply returns the cell
unchanged, so overriding it is optional.

---

## Full Example: Heat Diffusion

This example models heat spreading across a 2D grid. Each step, each cell moves
halfway towards the average temperature of its neighbors. Before that, temperatures
are clamped to the physical range `[0, 1000]` in the refinement phase.

### HeatStatus

```java
public class HeatStatus extends CellState {

    public double temperature;

    public HeatStatus(double temperature) {
        super("heat", temperature);
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return String.format("%5.1f", temperature);
    }
}
```

### HeatDiffusionRule

```java
public class HeatDiffusionRule extends CellularAutomataRule {

    @Override
    public Cell refinements(Cell cell) {
        HeatStatus s = (HeatStatus) cell.getCurrentStatus();
        // Clamp to physical bounds before sharing with neighbors
        s.temperature = Math.max(0.0, Math.min(s.temperature, 1000.0));
        cell.setCurrentStatus(s);
        return cell;
    }

    @Override
    public Cell transition(Cell cell, List<Cell> neighbors) {
        double avgNeighborTemp = neighbors.stream()
            .mapToDouble(n -> ((HeatStatus) n.getCurrentStatus()).temperature)
            .average()
            .orElse(0.0);

        double current = ((HeatStatus) cell.getCurrentStatus()).temperature;
        double next = (current + avgNeighborTemp) / 2.0;  // simple diffusion

        return new Cell(new HeatStatus(next), cell.getCol(), cell.getRow());
    }
}
```

### Running the Simulation

```java
HeatStatus cold = new HeatStatus(0.0);

// A single hot spot in the center
List<Cell> hotSpot = List.of(
    new Cell(new HeatStatus(1000.0), 10, 10)
);

CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
    .setWidth(20).setHeight(20)
    .setTotalIterations(20)
    .setDefaultStatus(cold)
    .setNeighborhoodType(NeighborhoodType.VON_NEUMANN)
    .setInitalState(hotSpot)
    .build();

CellularAutomata ca = new CellularAutomata(config);
ca = new HeatDiffusionRule().run(ca);
System.out.println(ca);
```

---

## Combining with Parallel Execution

The refinements hook is also available on `CellularAutomataParallelRule`:

```java
public class MyParallelCCA extends CellularAutomataParallelRule {

    @Override
    public Cell refinements(Cell cell) { ... }

    @Override
    public Cell transition(Cell cell, List<Cell> neighbors) { ... }
}
```

---

## See Also

- [Custom State Objects](../custom-state/) — how to extend `CellState`.
- [Implementing a Rule](../implementing-a-rule/) — the standard executor pattern.
- [Parallel Execution](../parallel-execution/) — scale the CCA to large grids.
