---
title: "UI Visualisation"
date: 2026-05-22
draft: false
summary: "Real-time Swing rendering of cellular automata using CellularAutomataUIRunner."
weight: 90
toc: true
---

## Overview

The `io.github.carmelolg.jcal.ui` package provides a **Swing-based visualisation layer**
that renders the evolution of a cellular automaton in real time.

The fastest way to get a window on screen is the **`CellularAutomataUIRunner`** fluent façade.
For more control (custom layouts, multiple windows, manual threading) you can wire the
lower-level components — `CellularAutomataDisplay` and `AutomataListener` — directly.

---

## Quick Start: CellularAutomataUIRunner

```java
CellState DEAD  = new CellState("dead",  "0");
CellState ALIVE = new CellState("alive", "1");

CellularAutomata ca = new CellularAutomata(config);

CellularAutomataUIRunner.create(ca, new GameOfLifeRule())
    .title("Game of Life — JCAL")
    .cellSize(14)
    .delay(80)                                                    // ms between frames
    .renderer(state -> state.equals(ALIVE) ? Color.GREEN : Color.BLACK)
    .start();
```

`start()` opens the window and launches the automaton on a **background daemon thread**,
keeping the Swing EDT free. The method returns immediately.

### Builder options

| Method | Default | Description |
|--------|---------|-------------|
| `.title(String)` | `"JCAL Automata"` | Window title |
| `.cellSize(int)` | `10` | Pixel size of each cell |
| `.delay(int)` | `100` | Milliseconds between generations (0 = no delay) |
| `.renderer(CellRenderer)` | *(required)* | Maps a `CellState` to a `java.awt.Color` |

{{< callout type="warning" >}}
Calling `start()` without a `renderer` throws `IllegalStateException`.
{{< /callout >}}

---

## Components

### CellRenderer

A `@FunctionalInterface` that maps a cell's `CellState` to a `java.awt.Color`.

```java
CellRenderer renderer = state -> state.equals(ALIVE) ? Color.GREEN : Color.BLACK;
```

Implement more complex renderers with a lambda or anonymous class:

```java
CellRenderer heatRenderer = state -> {
    int temp = (int) state.getValue();
    return new Color(Math.min(255, temp * 2), 0, Math.max(0, 255 - temp * 2));
};
```

---

### AutomataListener

`AutomataListener` implements `GenerationListener` and forwards each snapshot to a
`GridDisplay`. It optionally introduces a delay to throttle the animation.

```java
AutomataListener listener = new AutomataListener(display, 100); // 100 ms per frame
rule.addGenerationListener(listener);
```

The delay is applied on the **execution thread**, so the Swing EDT remains unblocked.

---

### CellularAutomataDisplay

A `JFrame`-backed window that owns a `GridPanel` (for rendering) and a status label
showing the current generation number.

```java
CellularAutomataDisplay display = new CellularAutomataDisplay("Game of Life", renderer, 14);
display.show();

AutomataListener listener = new AutomataListener(display, 80);
rule.addGenerationListener(listener);

// Run on a background thread
new Thread(() -> {
    try { rule.run(ca); } catch (Exception e) { e.printStackTrace(); }
}, "jcal-runner").start();
```

All Swing mutations inside `CellularAutomataDisplay` are dispatched on the EDT via
`SwingUtilities.invokeLater`.

---

### GridDisplay

An interface that any display component can implement to be driven by an `AutomataListener`:

```java
public interface GridDisplay {
    void update(GridSnapshot snapshot);
}
```

You can provide a custom `GridDisplay` (e.g., a JavaFX wrapper or a test stub) to
`AutomataListener` instead of `CellularAutomataDisplay`.

---

## Full Manual Example

```java
CellularAutomataDisplay display = new CellularAutomataDisplay(
    "My Automaton", heatRenderer, 10);
display.show();

CellularAutomataRule rule = new HeatDiffusionRule();
rule.addGenerationListener(new AutomataListener(display, 50));

Thread runner = new Thread(() -> {
    try {
        rule.run(ca);
        display.close();    // close the window when evolution ends
    } catch (Exception e) {
        e.printStackTrace();
    }
}, "jcal-runner");
runner.setDaemon(true);
runner.start();
```

---

## Running UI Examples

The project ships three ready-to-run UI examples:

```bash
# Game of Life — glider + blinker on a 40×40 grid
mvn compile exec:java \
  -Dexec.mainClass="io.github.carmelolg.jcal.examples.GameOfLifeUiExample"

# 3D Carter Bays' Life with Swing visualisation
mvn compile exec:java \
  -Dexec.mainClass="io.github.carmelolg.jcal.examples.GameOfLife3DUiExample"

# Advanced patterns
mvn compile exec:java \
  -Dexec.mainClass="io.github.carmelolg.jcal.examples.GameOfLifeAdvancedUiExample"
```

See [Game of Life UI Example](../../examples/game-of-life-ui/) for the full annotated source.

---

## See Also

- [Generation Listeners](../generation-listener/) — the `GenerationListener` / `GridSnapshot` API.
- [Implementing a Rule](../implementing-a-rule/) — writing the transition function.
