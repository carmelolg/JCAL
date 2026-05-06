---
title: "JCAL Documentation — v1.0.0"
---

## JCAL v1.0.0

First stable release of **JCAL** (Java Cellular Automata Library) — a lightweight Java
library for building and simulating [Cellular Automata](https://mathworld.wolfram.com/CellularAutomaton.html)
with minimal boilerplate.

| | |
|:---|:---|
| **Version** | 1.0.0 |
| **Author** | [carmelolg](https://github.com/carmelolg) |
| **License** | CC BY-NC-SA 4.0 |
| **Release** | [GitHub Releases](https://github.com/carmelolg/JCAL/releases/tag/1.0.0) |

## Features

| Feature | Description |
|---------|-------------|
| ☕ **Idiomatic Java** | Fluent builder API, abstract base classes, standard collections |
| 🔲 **2D grids** | Rectangular grids with configurable width and height |
| 🏘️ **Built-in neighborhoods** | Moore and Von Neumann for 2D |
| 🔌 **Extensible** | Custom states, neighborhoods, and rules with minimal code |
| ⚙️ **Complex CA support** | Refinement hook enables rich multi-value simulations |
| ⚡ **Parallel execution** | `CellularAutomataParallelExecutor` — same API, more threads |

## What's new in v2.0.0-rc2?

Looking for the latest features? See the [v2.0.0 documentation](/v2.0.0/).

- **n-dimensional grids** — 2D, 3D, and 4D support via `CellGrid`
- **Production-ready logging** — SLF4J logging throughout
- **New neighborhoods** — Moore and Von Neumann for 3D and 4D

## Acknowledgements

Inspired by research carried out at the [University of Calabria](https://www.unical.it/)
on Cellular Automata models for natural-phenomena simulation.
