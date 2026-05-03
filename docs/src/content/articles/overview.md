---
title: "Overview"
date: 2025-01-01
draft: false
summary: "What is a Cellular Automaton, and what is JCAL?"
weight: 10
toc: true
tags: ["concepts"]
---

## What is a Cellular Automaton?

A **Cellular Automaton (CA)** is a discrete computational model consisting of a grid of
cells, each holding a finite state. At each time step (a *generation*), every cell
updates its state according to a fixed **transition function** that depends only on the
cell's current state and the states of its immediate neighbors.

Despite deceptively simple rules, cellular automata can generate remarkably complex
behavior — making them a powerful tool for modeling natural phenomena such as:

- Population dynamics
- Fluid and lava flow
- Landslide propagation
- Heat diffusion
- Crystal growth

### Formal Definition

A Cellular Automaton is the quadruple **`<Z^d, S, X, σ>`**:

| Symbol | Meaning | JCAL type |
|--------|---------|-----------|
| **Z^d** | A *d*-dimensional grid of cells | `CellGrid` → `CellGrid` (2D) or `CellGrid` (3D/4D) |
| **S** | The finite set of possible cell states | `CellState` instances |
| **X** | The neighborhood — which cells are "neighbors" | `Neighborhood` subclass |
| **σ** | The transition function — one step of evolution | `CellularAutomataExecutor` subclass |

### Neighborhood Strategies

The two most common neighborhood shapes are:

- **[Moore neighborhood](https://en.wikipedia.org/wiki/Moore_neighborhood)** —
  all surrounding cells (orthogonal + diagonal): 8 in 2D, 26 in 3D, 80 in 4D.
  Use `NeighborhoodType.MOORE`.
- **[Von Neumann neighborhood](https://en.wikipedia.org/wiki/Von_Neumann_neighborhood)** —
  orthogonal cells only: 4 in 2D, 6 in 3D, 8 in 4D.
  Use `NeighborhoodType.VON_NEUMANN`.

You can also define a custom neighborhood by subclassing `Neighborhood` (2D) or
`Neighborhood` (3D/4D). See [Neighborhoods](../neighborhoods/) for details.

### Further Reading

- [Wolfram MathWorld — Cellular Automaton](https://mathworld.wolfram.com/CellularAutomaton.html)
- [The Nature of Code — Chapter 7](https://natureofcode.com/book/chapter-7-cellular-automata/) by Daniel Shiffman

---

## What is JCAL?

JCAL was born from the author's master's thesis, where a C++ library for Cellular
Automata was developed and used by physicists, geologists, and researchers across
multiple departments. That library was comprehensive but complex.

**JCAL brings the same ideas to Java in a smaller, simpler, and more accessible form.**

### Design Goals

| Goal | How JCAL Achieves It |
|------|----------------------|
| **Minimal boilerplate** | A working CA in a handful of Java lines |
| **Idiomatic Java** | Fluent builder API, abstract base classes, standard `java.util` collections |
| **Extensible** | Swap in custom states, neighborhood shapes, or parallel execution with minimal changes |
| **Complex CA support** | Custom state objects and a `refinements` hook enable rich, multi-value simulations |
| **Multi-dimensional** | 2D, 3D, and 4D grids with matching built-in neighborhood strategies |

### Key Classes at a Glance

| Class | Role |
|-------|------|
| `CellState` | A cell's state. Has a `key` (String) and a `value` (any Object). |
| `Cell` | One cell on the grid. Has a `CellState` and coordinate(s). |
| `CellularAutomataConfiguration` | Immutable configuration. Always use the inner `Builder`. |
| `CellularAutomata` | The grid. Create from a configuration object. |
| `CellularAutomataExecutor` | **Extend this** to define your transition rule (`singleRun`). |
| `Neighborhood` | **Extend this** for a custom 2D neighborhood shape. |
| `Neighborhood` | **Extend this** for a custom 3D/4D neighborhood shape. |
| `CellularAutomataParallelExecutor` | Parallel variant of `CellularAutomataExecutor`. |

### Grid Coordinate Convention

In 2D: `map[col][row]` where `col` is the x-axis (left → right) and `row` is the
y-axis (top → bottom).

---

## See Also

- [Getting Started](../getting-started/) — install JCAL and run your first automaton.
- [Design / Architecture](../../design/architecture/) — component map and data flow.
