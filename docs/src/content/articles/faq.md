---
title: "FAQ"
date: 2025-01-01
draft: false
summary: "Frequently asked questions and common troubleshooting tips."
weight: 100
toc: true
tags: ["faq", "troubleshooting"]
---

## General

### What Java version does JCAL require?

JCAL is compiled with `--release 16`, so it requires **Java 16 or later**.

### Is JCAL available on Maven Central?

Not yet. JCAL is currently published on the **GitHub Maven Registry**. See
[Getting Started](../getting-started/) for installation instructions. Maven Central
publication is planned for a future release.

### Can I use JCAL in a commercial project?

JCAL is licensed under
[CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/), which
**does not permit commercial use**. Please review the license terms before integrating
JCAL into a commercial product.

---

## Usage

### Do I have to create a named class for every transition rule?

No. You can use an anonymous class directly in your `main` method:

```java
CellularAutomataExecutor rule = new CellularAutomataExecutor() {
    @Override
    public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {
        // inline rule logic
        return new DefaultCell(cell.getCurrentStatus(), cell.getCol(), cell.getRow());
    }
};
```

Named classes are recommended for non-trivial rules because they are easier to test
and reuse.

### What is the difference between `setNeighborhoodType` and `setNeighborhood`?

- `setNeighborhoodType(NeighborhoodType)` — selects a built-in neighborhood
  (`MOORE` or `VON_NEUMANN`). JCAL automatically picks the 2D, 3D, or 4D variant
  based on your grid dimensions.
- `setNeighborhood(DefaultNeighborhood)` — provides a custom neighborhood instance.
  Use this for any non-standard shape.

**Use exactly one of these** in each configuration.

### What happens if I call both `setTotalIterations` and `setInfinite(true)`?

These options are **mutually exclusive**. Setting both results in undefined behavior.
Use `setInfinite(true)` for an open-ended simulation, or `setTotalIterations(n)` for a
fixed number of generations.

### Why is the builder method spelled `setInitalState` (one `t`)?

This is an intentional spelling carried over from the initial release. It has been
preserved to avoid a breaking change. The parameter name `initalState` in the builder
follows the same convention.

### How do I print the grid after each generation?

Call `System.out.println(ca)` after each `executor.run(ca)`, or override the executor's
`run` method to log intermediate states. `CellularAutomata.toString()` renders the grid
by calling `toString()` on each cell's `DefaultStatus`.

### How do I use `getMap()` / `getUtilsMap()` with a 3D grid?

`getMap()` and `getUtilsMap()` only work for 2D grids and return `DefaultCell[][]`.
For 3D/4D, use `ca.getGrid()` instead, which returns a `CellGrid` interface. See
[3D and 4D Support](../3d-4d-support/) for details.

---

## Troubleshooting

### My cells are not evolving — they all stay at the default status.

Check the following:

1. **Initial condition** — Did you call `setInitalState(...)` with a non-empty list?
2. **Neighborhood** — Is a neighborhood set (`setNeighborhoodType` or `setNeighborhood`)?
3. **Total iterations** — Is `setTotalIterations(n)` set to a value **greater than zero**?
4. **Transition logic** — Does `singleRun` return the correct next state? Add a
   `System.out.println` inside `singleRun` to trace execution.

### I get a `NullPointerException` inside `singleRun`.

Common causes:

- `cell.getCurrentStatus()` returns `null` — verify that `setDefaultStatus` is set
  in the builder and that every cell in the initial condition was constructed with a
  non-null status.
- You are casting to a custom status class but some cells were created with a plain
  `DefaultStatus` — ensure consistency across all initial cells and the default.
- You are accessing the `currentStatus` field directly instead of via
  `getCurrentStatus()` — prefer the getter for null-safe access patterns.

### The automaton runs but `System.out.println(ca)` shows nothing useful.

Override `toString()` in your `DefaultStatus` subclass (or ensure the `value` field
has a meaningful `toString()` representation) so the grid renders as expected.

### My custom 3D neighborhood returns the wrong number of neighbors.

Verify that you are checking bounds with `grid.isInside(coords)` before calling
`grid.get(coords)`. Out-of-bounds coordinates at grid edges silently clip the neighbor
list — this is expected behavior (no toric wrapping by default).

---

## Contributing to the Documentation

The documentation source files live in `docs/src/content/` and are built with
[Hugo](https://gohugo.io/) using the [Shiori](https://github.com/carmelolg/shiori) theme.

To preview the docs locally:

```bash
cd docs/src
hugo server
# Open http://localhost:1313/JCAL/
```

To build the static site into `docs/`:

```bash
cd docs/src
hugo
```
