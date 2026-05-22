+++
title = "Overview"
description = "What is a Cellular Automaton, and what is JCAL?"
weight = 1
+++

## What is a Cellular Automaton?

A **Cellular Automaton (CA)** is a discrete computational model consisting of a grid of
cells, each holding a finite state. At each time step, every cell updates its state
according to a fixed **transition function** that depends on the cell's current state and
the states of its immediate neighbors.

Despite their simple rules, cellular automata can produce remarkably complex behavior —
making them a powerful tool for modeling natural phenomena such as population dynamics,
fluid flow, landslides, and lava flows.

### Formal definition

A Cellular Automaton is formally described as the quadruple **`<Z`<span style="color: #e83e8c; font-size:87.5%;"><sup>d</sup></span>`, S, X, σ>`**:

| Symbol | Meaning | In JCAL |
|--------|---------|---------|
| **Z<sup>d</sup>** | A *d*-dimensional grid of cells | `DefaultCell[][]` inside `CellularAutomata` |
| **S** | The finite set of possible cell states | `DefaultStatus` instances |
| **X** | The neighborhood — which cells are considered "neighbors" | `DefaultNeighborhood` subclass |
| **σ** | The transition function — one step of evolution | `CellularAutomataRule` subclass |

### Neighborhood strategies

The two most common neighborhood shapes are:

- **[Moore neighborhood](https://en.wikipedia.org/wiki/Moore_neighborhood)** — the 8 surrounding cells (orthogonal + diagonal). Use `NeighborhoodType.MOORE`.
- **[Von Neumann neighborhood](https://en.wikipedia.org/wiki/Von_Neumann_neighborhood)** — the 4 orthogonal cells only. Use `NeighborhoodType.VON_NEUMANN`.

You can also define a fully custom neighborhood by subclassing `DefaultNeighborhood`.

### Further reading

- [Wolfram MathWorld — Cellular Automaton](https://mathworld.wolfram.com/CellularAutomaton.html)
- [The Nature of Code — Chapter 7: Cellular Automata](https://natureofcode.com/book/chapter-7-cellular-automata/) by Daniel Shiffman

---

## What is JCAL?

JCAL was born from the author's work during a master's thesis, where a C++ library for
Cellular Automata was developed and used by physicists, geologists, and researchers across
multiple departments. That library was comprehensive but complex.

**JCAL brings the same ideas to Java in a smaller, simpler, and more accessible form.**

### Design goals

- **Minimal boilerplate** — define a working CA in a few lines of Java.
- **Idiomatic Java** — fluent builder API, abstract base classes, standard collections.
- **Extensible** — swap in custom states, neighborhood shapes, or parallel execution with minimal code changes.
- **Complex CA support** — custom state objects and a refinement hook enable rich, multi-value simulations beyond simple binary-state automata.

### See also

- [Getting Started](../getting-started/) — install JCAL and run your first automaton.
- [Implementing a Rule](../basic-settings/) — how to write a transition function.
- [Configuration Reference](../builder-settings/) — all builder options explained.
