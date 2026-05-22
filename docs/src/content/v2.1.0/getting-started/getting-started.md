---
title: "Getting Started"
date: 2026-04-30
draft: false
summary: "Install JCAL and run your first cellular automaton in minutes."
weight: 20
toc: true
---

## Prerequisites

- **Java 16 or later** (JCAL is compiled with `--release 16`)
- **Maven 3.x** (or any build tool that supports Maven repositories)
- A GitHub account with a personal access token (for GitHub Maven Registry)

---

## Installation

### GitHub Maven Registry

JCAL is published on the **GitHub Maven Registry**. You need a GitHub personal access
token with `read:packages` scope configured in your `~/.m2/settings.xml`.

Follow the
[GitHub guide on authenticating to GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages),
then add the dependency to your `pom.xml`:

```xml
<dependency>
  <groupId>io.github.carmelolg</groupId>
  <artifactId>jcal</artifactId>
  <version>2.0.0</version>
</dependency>
```

Also add the repository:

```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/carmelolg/JCAL</url>
  </repository>
</repositories>
```

### Download the JAR

Download the latest release JAR from the
[Releases page](https://github.com/carmelolg/JCAL/releases) and add it to your
project's build path as a local dependency.

{{< callout type="note" >}}
Maven Central publication is planned for a future release. For now, the GitHub
Maven Registry is the primary distribution channel.
{{< /callout >}}

---

## Quick Start: Conway's Game of Life

The following example implements **Conway's Game of Life** — the most famous two-state
2D cellular automaton. It demonstrates all four steps of the JCAL workflow:

1. Define the possible **states**.
2. Specify the **initial condition**.
3. **Configure** the grid.
4. **Implement** the transition rule.

### Step 1 — Define States

```java
CellState dead  = new CellState("dead",  "0");
CellState alive = new CellState("alive", "1");
```

`CellState` holds a `key` (String) and a `value` (any Object). Two statuses are
equal when both fields are equal.

### Step 2 — Specify the Initial Condition

```java
// A "blinker" — three vertically-aligned live cells
List<Cell> seed = Arrays.asList(
    new Cell(alive, 5, 4),
    new Cell(alive, 5, 5),
    new Cell(alive, 5, 6)
);
```

Every cell not listed here starts in the default (dead) state.

### Step 3 — Configure the Grid

```java
CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
    .setWidth(10)
    .setHeight(10)
    .setTotalIterations(2)
    .setDefaultStatus(dead)
    .setNeighborhoodType(NeighborhoodType.MOORE)
    .setInitalState(seed)          // note: intentional spelling in the API
    .build();
```

See [Configuration Reference](../configuration/) for all available options.

### Step 4 — Implement the Transition Rule

Extend `CellularAutomataRule` and implement `transition`. JCAL calls this method
**once per cell per generation**; return a new `Cell` with the cell's next state.

```java
public class GameOfLifeRule extends CellularAutomataRule {

    @Override
    public Cell transition(Cell cell, List<Cell> neighbors) {
        CellState dead  = new CellState("dead",  "0");
        CellState alive = new CellState("alive", "1");

        long aliveCount = neighbors.stream()
            .filter(n -> n.getCurrentStatus().equals(alive))
            .count();

        boolean isAlive = cell.getCurrentStatus().equals(alive);
        Cell next = new Cell(dead, cell.getCol(), cell.getRow());

        if (!isAlive && aliveCount == 3) {
            next.setCurrentStatus(alive);           // birth
        } else if (isAlive && (aliveCount == 2 || aliveCount == 3)) {
            next.setCurrentStatus(alive);           // survival
        }
        // otherwise the cell stays/becomes dead

        return next;
    }
}
```

### Step 5 — Run

```java
CellularAutomata ca = new CellularAutomata(config);
GameOfLifeRule rule = new GameOfLifeRule();
ca = rule.run(ca);
System.out.println(ca);
```

`CellularAutomata.toString()` renders the grid using each status's `toString()` value.
The blinker will have rotated 90° after one generation (2 iterations = back to start).

---

## Next Steps

- [Implementing a Rule](../implementing-a-rule/) — deeper dive into the rule pattern.
- [Configuration Reference](../configuration/) — all available builder options.
- [Custom State Objects](../custom-state/) — model richer state with custom Java classes.
- [3D and 4D Support](../3d-4d-support/) — run simulations in higher dimensions.
- [Generation Listeners](../generation-listener/) — react to every generation with callbacks.
- [UI Visualisation](../ui-visualization/) — render your automaton in real time with Swing.
