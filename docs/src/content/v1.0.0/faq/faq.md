---
title: "FAQ"
description: "Frequently asked questions and common troubleshooting tips."
weight: 1
---

## General

### What Java version does JCAL require?

JCAL is compiled with `--release 16`, so it requires **Java 16 or later**.

### Is JCAL available on Maven Central?

Not yet. JCAL is currently published on the **GitHub Maven Registry**. See
[Getting Started](../getting-started/) for installation instructions.

### Can I use JCAL in a commercial project?

JCAL is licensed under
[CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/), which
**does not permit commercial use**. Please review the license terms before integrating
JCAL into a commercial product.

---

## Usage

### What is the difference between `setNeighborhoodType` and `setNeighborhood`?

- `setNeighborhoodType(NeighborhoodType)` — selects a built-in neighborhood
  (`MOORE` for 8 cells, `VON_NEUMANN` for 4 cells).
- `setNeighborhood(DefaultNeighborhood)` — provides a custom neighborhood instance.

**Use exactly one of these** in each configuration.

### What happens if I call both `setTotalIterations` and `setInfinite(true)`?

These options are **mutually exclusive**. Use `setInfinite(true)` when you want the
simulation to run until interrupted, or `setTotalIterations(n)` for a fixed number
of generations.

### Why is the builder method spelled `setInitalState` (one `i`)?

This is an intentional spelling in the public API preserved to avoid a breaking change.

---

## Troubleshooting

### My cells are not evolving — they all stay at the default status.

Check the following:

1. **Initial condition** — have you called `setInitalState(...)` with a non-empty list?
2. **Neighborhood** — is a neighborhood type set?
3. **Total iterations** — is `setTotalIterations(n)` set to a value greater than zero?
4. **Transition logic** — does your `singleRun` method return the correct next state?

### I get a `NullPointerException` inside `singleRun`.

The most common cause is accessing `cell.getCurrentStatus()` without null-checking.
Verify that your `defaultStatus` is set and non-null in the builder.
