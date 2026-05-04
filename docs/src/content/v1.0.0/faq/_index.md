+++
title = "FAQ"
description = "Frequently asked questions and common troubleshooting tips."
weight = 7
+++

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

No. You can use an anonymous class or a lambda-style anonymous subclass directly in
your `main` method. Named classes are recommended for non-trivial rules because they
are easier to test and reuse.

```java
CellularAutomataExecutor rule = new CellularAutomataExecutor() {
    @Override
    public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {
        // inline rule logic
        return new DefaultCell(cell.getCurrentStatus(), cell.getCol(), cell.getRow());
    }
};
```

### What is the difference between `setNeighborhoodType` and `setNeighborhood`?

- `setNeighborhoodType(NeighborhoodType)` — selects a built-in neighborhood
  (`MOORE` for 8 cells, `VON_NEUMANN` for 4 cells). Use this for standard shapes.
- `setNeighborhood(DefaultNeighborhood)` — provides a custom neighborhood instance
  that you have implemented by extending `DefaultNeighborhood`. Use this for any
  non-standard shape (diagonal-only, extended range, etc.).

**Use exactly one of these** in each configuration; combining both is not supported.

### What happens if I call both `setTotalIterations` and `setInfinite(true)`?

These options are **mutually exclusive**. Setting both may result in undefined behavior.
Use `setInfinite(true)` when you want the simulation to run until interrupted, or
`setTotalIterations(n)` for a fixed number of generations.

### Why is the builder method spelled `setInitalState` (one `i`)?

This is an intentional spelling in the public API carried over from the initial
release. It has been preserved to avoid a breaking change. The parameter name
`initalState` in the builder follows the same convention.

### How do I print the grid after each generation?

Override the executor's `run` method and call `System.out.println(ca)` after each
step, or implement a listener/callback in your application logic. The `CellularAutomata`
class provides a default `toString()` that renders the grid to a string.

---

## Troubleshooting

### My cells are not evolving — they all stay at the default status.

Check the following:

1. **Initial condition** — have you called `setInitalState(...)` with a non-empty list?
2. **Neighborhood** — is a neighborhood type set (`setNeighborhoodType` or `setNeighborhood`)?
3. **Total iterations** — is `setTotalIterations(n)` set to a value greater than zero?
4. **Transition logic** — does your `singleRun` method return the correct next state for
   the cells in question? Add a `System.out.println` inside `singleRun` to trace execution.

### I get a `NullPointerException` inside `singleRun`.

The most common cause is accessing `cell.getCurrentStatus()` or a neighbor's status
without null-checking. Verify that:

- Your `defaultStatus` is set and non-null in the builder.
- You are using `getCurrentStatus()` (the getter) rather than accessing the field directly
  (`currentStatus`) where possible.
- When casting to a custom status class, ensure every cell in the initial condition was
  constructed with an instance of that class, not with a plain `DefaultStatus`.

### The automaton runs but `System.out.println(ca)` shows nothing useful.

Override `toString()` in your `DefaultStatus` subclass (or ensure the `value` field has
a meaningful `toString()` representation) so that the grid renders as expected.

---

## Contributing to the documentation

The documentation source files live in `docs/src/content/` and are built with
[Hugo](https://gohugo.io/). To build the docs locally:

1. Install Hugo (extended edition, `latest`).
2. From the repository root, run:
   ```bash
   cd docs/src
   hugo server
   ```
3. Open `http://localhost:1313/JCAL/` in your browser.

To add a new page, create a folder under `docs/src/content/` with an `_index.md` file.
Set `weight` in the front matter to control its position in the navigation.
