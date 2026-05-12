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

{{< code lang="xml" file="dependency.xml">}}{{< /code >}}

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
4. **Implement** the transition rule by extending `CellularAutomataRule`.

### Step 1 — Implement the transition rule

Extend `CellularAutomataRule` and implement `transition`. This method is called
once per cell per generation; return the cell's next state.

{{< code lang="JAVA" file="gol-executor.java">}}{{< /code >}}

### Step 2 — Configure and run

{{< code lang="JAVA" file="main.java">}}{{< /code >}}

Run the `main` method. The CA iterates for the configured number of steps and
prints the final grid to standard output.

---

## Next steps

- [Implementing a Rule](../basic-settings/) — deeper dive into the executor pattern.
- [Configuration Reference](../builder-settings/) — all available builder options.
- [Custom State Objects](../custom-status/) — model richer state with custom Java classes.
