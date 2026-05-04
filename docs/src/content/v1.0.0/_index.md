|                   |                                                                                                   |
| :---------------- | :------------------------------------------------------------------------------------------------ |
| **Author**        | [carmelolg](https://carmelolg.github.io)  |
| **License**       | ![License: CC BY-NC-SA 4.0](https://img.shields.io/badge/License-CC_BY--NC--SA_4.0-lightgrey.svg) |
| **Test Coverage** | ![Coverage](https://raw.githubusercontent.com/carmelolg/JCAL/master/.github/badges/jacoco.svg)    |
| **Latest**        | **1.0.0.alpha** — not yet production-ready |
| **Stable**        | _in progress_ |

**JCAL** (Java Cellular Automata Library) is a lightweight Java library for building and
simulating [Cellular Automata](https://mathworld.wolfram.com/CellularAutomaton.html) with
minimal boilerplate. Define your grid, states, neighborhood strategy, and transition rule —
then let JCAL handle the rest.

## Features
<div class="row py-3 mb-5">
<div class="col-md-4">
<div class="card flex-row border-0">
<div class="mt-4">
<span class="fas fa-superscript fa-2x text-primary"></span>
</div>
<div class="card-body pl-2">
<h5 class="card-title">
Cellular Automata first.
</h5>
<p class="card-text text-muted">
Every design decision in JCAL is oriented around making it easy to model
and run Cellular Automata — from simple 2-state rules to complex multi-value state machines.
</p>
</div>
</div>
</div>
<div class="col-md-4">
<div class="card flex-row border-0">
<div class="mt-3">
<span class="fas fa-code fa-2x text-primary"></span>
</div>
<div class="card-body pl-2">
<h5 class="card-title">
Simplicity by design.
</h5>
<p class="card-text text-muted">
JCAL emphasizes <strong>simplicity</strong>: you can define and run a 2D cellular
automaton in just a few lines of Java, without boilerplate.
</p>
</div>
</div>
</div>
<div class="col-md-4">
<div class="card flex-row border-0">
<div class="mt-3">
<span class="fab fa-java fa-2x text-primary"></span>
</div>
<div class="card-body pl-2">
<h5 class="card-title">
Designed for Java.
</h5>
<p class="card-text text-muted">
Written in Java 16, JCAL follows idiomatic Java patterns — fluent builders,
abstract base classes, and standard collections — so it feels natural to Java developers.
</p>
</div>
</div>
</div>
</div>
<div class="row py-3 mb-5">
<div class="col-md-4">
<div class="card flex-row border-0">
<div class="mt-4">
<span class="fas fa-compass fa-2x text-primary"></span>
</div>
<div class="card-body pl-2">
<h5 class="card-title">
Complex Cellular Automata.
</h5>
<p class="card-text text-muted">
Complex Cellular Automata (CCA) are supported through custom state objects
and a pre-processing refinement hook, enabling rich multi-value simulations.
</p>
</div>
</div>
</div>
<div class="col-md-4">
<div class="card flex-row border-0">
<div class="mt-4">
<span class="fas fa-tachometer-alt fa-2x text-primary"></span>
</div>
<div class="card-body pl-2">
<h5 class="card-title">
Parallel execution.
</h5>
<p class="card-text text-muted">
For large grids, swap in <code>CellularAutomataParallelExecutor</code> to
distribute the transition function across threads with no API changes.
</p>
</div>
</div>
</div>
</div>


---

## Acknowledgements
- [University of Calabria](https://www.unical.it/) for inspiring foundational research in Cellular Automata.
+++
title = "Getting Started"
description = "Install JCAL and run your first cellular automaton."
weight = 2
+++

## Installation

### Option 1 — GitHub Maven Registry

JCAL is published on the **GitHub Maven Registry**. Follow the
[GitHub guide](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages)
to configure authentication, then add this dependency to your `pom.xml`:

{{< code lang="xml" file="getting-started/dependency.xml">}}{{< /code >}}

> **Note:** You need a GitHub personal access token with `read:packages` scope. See the
> linked guide for instructions on setting up your `~/.m2/settings.xml`.

### Option 2 — Download the JAR

Download the latest release JAR from the
[Releases page](https://github.com/carmelolg/JCAL/releases) and add it to your project's
build path as a local dependency.

### Maven Central

<span class="text-danger">*Not yet available.*</span> Maven Central publication is planned for a future release.

---

## Quick Start

The following example implements **Conway's Game of Life** — a classic two-state 2D
cellular automaton. It demonstrates all four steps of the JCAL workflow:

1. Define the possible **states** (`DefaultStatus`).
2. Specify the **initial condition** (which cells start alive).
3. **Configure** the grid via `CellularAutomataConfigurationBuilder`.
4. **Implement** the transition rule by extending `CellularAutomataExecutor`.

### Step 1 — Implement the transition rule

Extend `CellularAutomataExecutor` and implement `singleRun`. This method is called
once per cell per generation; return the cell's next state.

{{< code lang="JAVA" file="getting-started/gol-executor.java">}}{{< /code >}}

### Step 2 — Configure and run

{{< code lang="JAVA" file="getting-started/main.java">}}{{< /code >}}

Run the `main` method. The CA iterates for the configured number of steps and
prints the final grid to standard output.

---

## Next steps

- [Implementing a Rule](../basic-settings/) — deeper dive into the executor pattern.
- [Configuration Reference](../builder-settings/) — all available builder options.
- [Custom State Objects](../custom-status/) — model richer state with custom Java classes.
