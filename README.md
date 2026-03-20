# JCAL - Java Cellular Automata Library
| | Badge|
|---|---|
| **License** | ![License: CC BY-NC-SA 4.0](https://img.shields.io/badge/License-CC_BY--NC--SA_4.0-lightgrey.svg) |
| **Test Coverage** | ![Coverage](.github/badges/jacoco.svg) |


## Quick Start for AI Code Assistants

> This section gives AI agents and LLM code assistants everything needed to
> understand, extend, and run JCAL with no hidden setup.

### Add the dependency (Maven)

```xml
<dependency>
    <groupId>io.github.carmelolg</groupId>
    <artifactId>jcal</artifactId>
    <version>1.0.0.alpha</version>
</dependency>
```

The package is distributed via **GitHub Packages**.  See [Getting Started](https://carmelolg.github.io/JCAL/) for repository configuration.

### Minimal example – Game of Life in ~30 lines

```java
import io.github.carmelolg.jcal.configuration.CellularAutomataConfiguration;
import io.github.carmelolg.jcal.configuration.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder;
import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.core.CellularAutomataExecutor;
import io.github.carmelolg.jcal.model.DefaultCell;
import io.github.carmelolg.jcal.model.DefaultStatus;
import io.github.carmelolg.jcal.model.NeighborhoodType;
import java.util.Arrays;
import java.util.List;

public class Main {

    static final DefaultStatus DEAD  = new DefaultStatus("dead",  "0");
    static final DefaultStatus ALIVE = new DefaultStatus("alive", "1");

    public static void main(String[] args) throws Exception {
        // 1. Define which cells start alive (blinker pattern)
        List<DefaultCell> initialState = Arrays.asList(
            new DefaultCell(ALIVE, 5, 4),
            new DefaultCell(ALIVE, 5, 5),
            new DefaultCell(ALIVE, 5, 6)
        );

        // 2. Configure the automaton
        CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
            .setWidth(10).setHeight(10)
            .setInfinite(false).setTotalIterations(2)
            .setDefaultStatus(DEAD)
            .setInitalState(initialState)
            .setNeighborhoodType(NeighborhoodType.MOORE)
            .build();

        // 3. Implement the transition rule and run
        CellularAutomata ca = new CellularAutomata(config);
        ca = new GameOfLifeRule().run(ca);
        System.out.println(ca);   // prints the grid
    }

    static class GameOfLifeRule extends CellularAutomataExecutor {
        @Override
        public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {
            long aliveCount = neighbors.stream()
                .filter(n -> n.getCurrentStatus().equals(ALIVE)).count();
            DefaultCell next = new DefaultCell(DEAD, cell.getCol(), cell.getRow());
            boolean isAlive = cell.getCurrentStatus().equals(ALIVE);
            if (!isAlive && aliveCount == 3) next.setCurrentStatus(ALIVE);
            else if (isAlive && (aliveCount == 2 || aliveCount == 3)) next.setCurrentStatus(ALIVE);
            return next;
        }
    }
}
```

Fully annotated versions live in
[`src/main/java/io/github/carmelolg/jcal/examples/`](src/main/java/io/github/carmelolg/jcal/examples/).

---

## Concepts

> A concise reference optimised for quick parsing by LLMs and AI agents.

### Cellular Automata model

A Cellular Automaton is the quadruple **`<Zd, S, X, σ>`**:

| Symbol | Meaning | JCAL type |
|--------|---------|-----------|
| **Zd** | d-dimensional grid of cells | `DefaultCell[][]` inside `CellularAutomata` |
| **S**  | Set of possible cell states | `DefaultStatus` |
| **X**  | Neighborhood strategy | `DefaultNeighborhood` subclass |
| **σ**  | Transition function | `CellularAutomataExecutor` subclass |

### Key classes at a glance

| Class | Role |
|-------|------|
| `DefaultStatus` | A cell's state. Has a `key` (String) and a `value` (any Object). |
| `DefaultCell` | One cell on the grid. Has a `DefaultStatus` and `(col, row)` coordinates. |
| `CellularAutomataConfiguration` | Immutable configuration. Always use the inner `Builder`. |
| `CellularAutomata` | The grid. Call `init(config)` or use the constructor that accepts config. |
| `CellularAutomataExecutor` | **Extend this** to define your transition rule (`singleRun`). |
| `DefaultNeighborhood` | **Extend this** for a custom neighborhood shape. |
| `MooreNeighborhood` | Built-in 8-cell neighborhood (orthogonal + diagonal). |
| `VonNeumannNeighborhood` | Built-in 4-cell neighborhood (orthogonal only). |
| `CellularAutomataParallelExecutor` | Parallel variant of `CellularAutomataExecutor`. |

### Extension points

1. **Custom rule** – subclass `CellularAutomataExecutor`, implement `singleRun(cell, neighbors)`.
2. **Custom state** – pass any `Object` as `value` when constructing `DefaultStatus`.
3. **Custom neighborhood** – subclass `DefaultNeighborhood`, implement `getNeighbors(matrix, i, j)`.
4. **CCA pre-processing** – override `refinements(cell)` in your executor for complex automata.
5. **Parallel execution** – use `CellularAutomataParallelExecutor` for large grids.

### Grid coordinate convention

`map[col][row]` where `col` is the x-axis (left → right) and `row` is the y-axis (top → bottom).

---

## What is a Cellular Automata

Here some references:
* [Wolfram - Cellular Automaton](https://mathworld.wolfram.com/CellularAutomaton.html)
* [The nature of Code by Daniel Shiffman](https://natureofcode.com/book/chapter-7-cellular-automata/)
* [Chapter 3 of my master thesis (**in italian**)](https://github.com/carmelolg/master-thesis/blob/master/Tesi/pdf/main.pdf)

#### TLDR;
A **basic** Cellular Automata is the quadruple `<Z <sup>d</sup>,S,X,o>`

`Z <sup>d</sup>` is a set of cells, a d-dimension matrix of cells

`S` is a set of status where the single cell can be in

`X` is a set of cell's neighbors (the most common neighborhood implementation are [MOORE](https://en.wikipedia.org/wiki/Moore_neighborhood) and [VON NEUMANN](https://en.wikipedia.org/wiki/Von_Neumann_neighborhood)

 `o` is the transition function. This function implements the evolution of the natural or artificial phenomena represented by a Cellular Automata.
 
Thanks to this mathematic model, it's possible represent a lot of natural phenomena like landslides, lava flows and so on...

## What about JCAL idea

During my master's thesis, I contributed to the implementation of a library for Cellular Automata that was primarily used by physicists, geologists, and scientists from various departments. This library was written in C++, and it was more comprehensive than JCAL.

**JCAL wants to implements the same idea but in a smaller and simpler way for Java user and developers.**

## Documentation

[Here](https://carmelolg.github.io/JCAL/) the official documentation.

For a deeper technical overview see [ARCHITECTURE.md](ARCHITECTURE.md).

