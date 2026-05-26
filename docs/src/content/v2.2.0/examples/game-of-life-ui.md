---
title: "Conway's Game of Life (UI)"
date: 2026-05-22
draft: false
summary: "Real-time Swing visualisation of a 2D Game of Life with glider and blinker."
toc: true
---

Demonstrates the **JCAL Swing UI layer**: a 40×40 Game of Life grid rendered in real
time using `CellularAutomataUIRunner`. The initial pattern includes a classic **glider**
that moves diagonally and a **blinker** oscillator at the centre.

{{< callout type="info" >}}
The window updates after every generation. Each cell is rendered as a 14-pixel square:
**green** for alive cells, **black** for dead cells.
{{< /callout >}}

## Running the Example

```bash
mvn compile exec:java \
  -Dexec.mainClass="io.github.carmelolg.jcal.examples.GameOfLifeUiExample"
```

---

## Full Code

**File:** `GameOfLifeUiExample.java`

```java
package io.github.carmelolg.jcal.examples;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.core.CellularAutomataConfiguration;
import io.github.carmelolg.jcal.core.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder;
import io.github.carmelolg.jcal.examples.GameOfLifeExample.GameOfLifeRule;
import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.neighborhood.NeighborhoodType;
import io.github.carmelolg.jcal.ui.CellularAutomataUIRunner;

public class GameOfLifeUiExample {

    static final CellState DEAD  = new CellState("dead",  "0");
    static final CellState ALIVE = new CellState("alive", "1");

    public static void main(String[] args) throws Exception {

        // Classic 5-cell glider at top-left + blinker at centre
        List<Cell> initialState = Arrays.asList(
            new Cell(ALIVE, 2, 1),   // glider
            new Cell(ALIVE, 3, 2),
            new Cell(ALIVE, 1, 3),
            new Cell(ALIVE, 2, 3),
            new Cell(ALIVE, 3, 3),

            new Cell(ALIVE, 18, 20), // blinker (horizontal)
            new Cell(ALIVE, 19, 20),
            new Cell(ALIVE, 20, 20)
        );

        CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
            .setWidth(40)
            .setHeight(40)
            .setInfinite(true)
            .setDefaultStatus(DEAD)
            .setInitialState(initialState)
            .setNeighborhoodType(NeighborhoodType.MOORE)
            .build();

        CellularAutomata ca = new CellularAutomata(config);

        CellularAutomataUIRunner.create(ca, new GameOfLifeRule())
            .title("Game of Life — JCAL")
            .cellSize(14)
            .delay(80)
            .renderer(state -> state.equals(ALIVE) ? Color.GREEN : Color.BLACK)
            .start();

        // Keep the main thread alive while the daemon thread runs the simulation
        Thread.sleep(120 * 80 + 3_000);
    }
}
```

---

## Key Concepts

- **`CellularAutomataUIRunner`** — fluent façade that wires together the display,
  listener, and execution thread in a single call chain.
- **`CellRenderer`** — lambda mapping a `CellState` to a `java.awt.Color`.
- **Infinite mode** — `setInfinite(true)` runs until the thread is interrupted or the
  process exits.
- **Daemon thread** — `start()` runs the evolution on a background daemon thread so it
  doesn't prevent JVM shutdown.

---

## See Also

- [Game of Life 2D](../game-of-life-2d/) — Console-only version of the same rule
- [UI Visualisation](../../reference/ui-visualization/) — Full reference for the UI layer
- [Generation Listeners](../../reference/generation-listener/) — Lower-level listener API
