---
title: "GIF Export"
date: 2026-11-25
draft: false
summary: "Record the evolution of a 2D automaton into an animated GIF with GifRecorder."
weight: 95
toc: true
---

## Overview

`io.github.carmelolg.jcal.ui.GifRecorder` is a `GenerationListener` that records the
evolution of a **2D** automaton into an animated, endlessly looping GIF. It uses only the
JDK, so it adds no dependency, and it works on headless machines.

---

## Quick Start

```java
CellRenderer renderer = state -> state.equals(ALIVE) ? Color.GREEN : Color.BLACK;

try (GifRecorder gif = GifRecorder.create(Path.of("life.gif"), renderer)
        .cellSize(8)
        .delay(100)
        .every(1)) {
    gif.record(GridSnapshot.of(0, ca.getGrid()));   // optional: initial state
    rule.addGenerationListener(gif);
    rule.run(ca);
}
```

{{< callout type="warning" >}}
The GIF is complete only after `close()`. Always use try-with-resources.
{{< /callout >}}

### Options

| Method | Default | Description |
|--------|---------|-------------|
| `.cellSize(int)` | `10` | Pixel size of each cell |
| `.delay(int)` | `100` | Milliseconds per frame, rounded to 10 ms |
| `.every(int)` | `1` | Record one generation out of *n*; the last one is always kept |

Options must be set before the first frame is recorded.

---

## Behaviour

| Situation | Result |
|-----------|--------|
| Existing output file | Overwritten at the first frame |
| No frame recorded | File not touched |
| Missing parent directory or write error | `UncheckedIOException`, which stops the run |
| Up to 256 colours in a frame | Exact colours |
| More than 256 colours in a frame | Approximated colours, one warning logged |
| Translucent colours | Blended over black |
| 3D or 4D snapshot | `IllegalArgumentException` |
| Generations after `close()` | Ignored |

Frames are written as they are produced, so memory does not grow with the length of the
run. File size and run time do: use `every(n)` or a smaller `cellSize` for large grids.

### Infinite automata

Interrupt the execution thread, then let the try-with-resources block close the recorder.
If the JVM exits first, for example by closing the window opened by
`CellularAutomataUIRunner`, the file is incomplete.

---

## Running the Example

```bash
mvn compile exec:java \
  -Dexec.mainClass="io.github.carmelolg.jcal.examples.GameOfLifeGifExample" \
  -Dexec.args="game-of-life.gif"
```

---

## See Also

- [Generation Listeners](../generation-listener/) — the `GenerationListener` / `GridSnapshot` API.
- [UI Visualisation](../ui-visualization/) — real-time rendering and `CellRenderer`.
