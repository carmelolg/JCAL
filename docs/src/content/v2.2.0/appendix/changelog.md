---
title: "Changelog"
date: 2026-05-26
draft: false
summary: "Release notes and version history for JCAL."
toc: true
---

All notable changes to JCAL are documented here.
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Versions align with [Semantic Versioning](https://semver.org/).

---

## [2.2.0] — 2026-05-24

### Added
- **`AbstractCellularAutomataRule`** — new abstract base class in `core/` that unifies
  `CellularAutomataRule` and `CellularAutomataParallelRule`. Provides shared listener
  management (`addGenerationListener`, `notifyListeners`) and a `protected final swapBuffers()`
  helper for the double-buffer swap. Both rule classes now extend this base class;
  no API changes are required for existing subclasses.
- **`NeighborhoodFactory`** — new factory class in `neighborhood/` that isolates the
  `core` package from concrete neighborhood implementations. Resolves the correct
  neighborhood class (2D, 3D, or 4D) by `NeighborhoodType` and dimension count.
  This is a library-internal change; the public API via `setNeighborhoodType` is unchanged.
- **`CellGrid.coordinatesForRow(int row)`** — new method that returns only the
  coordinates for a specific row, replacing the O(n²) `allCoordinates().subList()`
  pattern in the parallel runners.
- **`CellularAutomataConfiguration.isActiveCells()`** — new boolean getter following
  the Java naming convention for booleans. The existing `getActiveCells()` is kept as
  an alias.

### Changed
- **Breaking: `setInitalState` / `getInitalState` removed** — the typo methods
  `setInitalState(List<Cell>)` and `getInitalState()` have been **removed** from
  `CellularAutomataConfigurationBuilder` and `CellularAutomataConfiguration`.
  Use `setInitialState(List<Cell>)` and `getInitialState()` instead.
  Update all call sites before upgrading to 2.2.0.
- **`CellState.getKey()` now returns `String`** (was `Object`). Any code that assigned
  the result to an `Object` variable and relied on the exact type at runtime should cast
  or update the receiving variable to `String`.
- **`CellState.key` and `CellState.value` are now `private final`** — direct field access
  is no longer possible. Use `getKey()` and `getValue()` (or subclass accessors).
- **`setGrid()` / `setUtilsGrid()` reduced to package-private** in `CellularAutomata`.
  These methods were never intended for public use; they are internal double-buffer
  helpers. If your code called them directly, use the rule's buffer-swap mechanism instead.
- **`Utils` is now `final`** with a `private` constructor — the class was always a
  pure utility class and should never be instantiated or subclassed.
- **`CellGrid(Cell[][] matrix)` constructor** now validates its input:
  throws `IllegalArgumentException` for `null`, empty, or jagged matrices.
- **`CellularAutomataRunner` / `CellularAutomataRefinementRunner`** internal `Callable`
  type changed from `Callable<List<Cell>>` to `Callable<Void>`. These are internal
  classes; no API impact for library users.
- **`setActiveCells(boolean)`** documentation updated — the feature was incorrectly
  marked `@Deprecated`. It is a **planned future optimization** (iterate only active /
  non-default cells to speed up sparse-grid simulations) and should not be deprecated.
- **`core.parallel` sub-package eliminated** — `CellularAutomataParallelRule`,
  `CellularAutomataRunner`, and `CellularAutomataRefinementRunner` have been moved
  from `io.github.carmelolg.jcal.core.parallel` directly into
  `io.github.carmelolg.jcal.core`. The `core.parallel` package no longer exists.
  If you imported `io.github.carmelolg.jcal.core.parallel.*`, update your imports to
  `io.github.carmelolg.jcal.core.*`.
- **`getUtilsGrid()` is now package-private** in `CellularAutomata` — previously
  annotated `@Deprecated public`, it is now a package-private method with no deprecation
  annotation. It was never intended for public use; the move of all parallel runner
  classes into `core` makes this possible without any impact on library users.

### Fixed
- **O(n²) performance bug in parallel execution** — `CellularAutomataRunner` and
  `CellularAutomataRefinementRunner` used to iterate `allCoordinates()` (the entire grid)
  and discard irrelevant rows. With N parallel tasks this produced O(N × W×H) coordinate
  work. The fix introduces `CellGrid.coordinatesForRow(row)` so each task works only on
  its assigned row — O(W/N × H) total.
- **Duplicate dimension validation removed** from `CellularAutomata.check()` — the same
  checks (2–4 dimensions, all sizes > 0) were already enforced by the `GridDimensions`
  constructor. `init()` now catches `IllegalArgumentException` from `GridDimensions` and
  re-throws as `CellularAutomataException`.
- **`StringBuilder` string concatenation in `CellularAutomata.toString()`** —
  `builder.append(x + " ")` replaced with `builder.append(x).append(' ')` to avoid
  creating a temporary `String` on every cell.
- **Javadoc in `AutomataListener`** — replaced references to non-existent
  `AutomataWindow` and `AutomataViewer` with the correct `CellularAutomataDisplay`.
- **Outer-class `Logger` in `CellularAutomataConfiguration`** — the unused
  `private static final Logger logger` field in the outer class was removed. The builder's
  `logger` (used in `build()`) is unaffected.

### Migration from 2.1.0

```java
// BEFORE (2.1.0)
new CellularAutomataConfigurationBuilder()
    .setInitalState(seedCells)   // ← typo
    ...
cfg.getInitalState()             // ← typo

// AFTER (2.2.0)
new CellularAutomataConfigurationBuilder()
    .setInitialState(seedCells)  // ← correct
    ...
cfg.getInitialState()            // ← correct

// parallel import change (if you imported the sub-package explicitly)
// BEFORE: import io.github.carmelolg.jcal.core.parallel.CellularAutomataParallelRule;
// AFTER:  import io.github.carmelolg.jcal.core.CellularAutomataParallelRule;
```

---

## [2.1.0] — 2026-05-22

### Added
- **`GenerationListener`** — new `@FunctionalInterface` in `core/`; receives a 1-based
  generation index and an immutable `GridSnapshot` after each completed generation.
  Register via `CellularAutomataRule.addGenerationListener(listener)`.
- **`GridSnapshot`** — immutable snapshot of a `CellGrid` at a specific generation.
  Provides `getState(int col, int row)` (2D) and `getState(int[] coords)` (nD) accessors
  plus a flat unmodifiable `getCellStates()` list in row-major order.
- **Swing UI layer** (`io.github.carmelolg.jcal.ui`):
  - `CellRenderer` — functional interface mapping a `CellState` to an `java.awt.Color`
  - `GridDisplay` — interface for any display component accepting a `GridSnapshot`
  - `GridPanel` — Swing `JPanel` that paints the grid using a `CellRenderer`
  - `CellularAutomataDisplay` — `JFrame`-backed window with generation counter
  - `AutomataListener` — `GenerationListener` that forwards snapshots to a `GridDisplay`
    and optionally throttles animation speed
  - `CellularAutomataUIRunner` — fluent façade; wires display, listener, and execution
    thread in a single call chain
- **UI examples**:
  - `GameOfLifeUiExample` — glider + blinker on a 40×40 grid with real-time Swing rendering
  - `GameOfLife3DUiExample` — 3D Carter Bays' Life with live visualisation
  - `GameOfLifeAdvancedUiExample` — advanced patterns demonstrating the full UI API

### Changed
- `CellularAutomataRule.run(CellularAutomata)` now notifies registered `GenerationListener`
  instances after every generation (both finite and infinite modes)
- `GameOfLifeExample`, `GameOfLife3DExample`, `CustomStateExample` updated with minor
  documentation and code-style improvements

---

## [2.0.0] — 2026-05-04

### Added
- **SLF4J logging integration** — production-ready logging across all core components
  - INFO level: CA initialization, execution lifecycle
  - DEBUG level: grid operations, transitions, refinements
  - WARN/ERROR level: validation failures, configuration issues
- Comprehensive Javadoc enhancements across all public APIs
- GitHub Pages documentation site with versioned content (v1.0.0, v2.0.0)

### Changed
- **Breaking:** `CellularAutomataExecutor` renamed to `CellularAutomataRule` — the new name
  better reflects what the developer provides: a *rule*, not an executor
- **Breaking:** `CellularAutomataParallelExecutor` renamed to `CellularAutomataParallelRule`
  for the same reason
- **Breaking:** abstract method `singleRun(Cell, List<Cell>)` renamed to
  `transition(Cell, List<Cell>)` — aligns with standard cellular automata theory terminology
- Code cleanup: removed 4 unused utility methods (`isInside/Cell[][]`, `setConfig()`, `setNeighborhood()`, `getCells()`)
- Dependencies: added `slf4j-api:2.0.13` and `slf4j-simple:2.0.13` for production logging
- Documentation restructured with Hugo and Shiori theme for better user experience
- Shiori theme updated: responsive design, improved navigation, versioned sidebar

> **Migration from 1.x:** replace `extends CellularAutomataExecutor` with
> `extends CellularAutomataRule`, `extends CellularAutomataParallelExecutor` with
> `extends CellularAutomataParallelRule`, and rename any `singleRun` override to `transition`.

### Fixed
- Removed dead code in `CellularAutomata.init()` (unreachable `try-catch`)
- GitHub Pages build issues resolved (recursive symlink exclusion)
- Homepage button paths corrected for multi-version documentation

### Tests
- Test suite: **140 tests**, 100% instruction coverage (JaCoCo)

---

## [2.0.0-rc2] — 2026-05-04

### Added
- `ExamplesTest` — smoke tests and branch-coverage tests for all three example programs

### Changed
- `DefaultCell` renamed to `Cell`, `DefaultStatus` renamed to `CellState`, `DefaultNeighborhood` renamed to `Neighborhood`

### Tests
- 148 tests, 100% instruction coverage (JaCoCo)

---

## [2.0.0-rc1] — 2026-04-30

### Added
- **3D and 4D cellular automata support** via unified `CellGrid` / `GridDimensions`
- `Moore3DNeighborhood`, `VonNeumann3DNeighborhood`, `Moore4DNeighborhood`, `VonNeumann4DNeighborhood`
- `NDCapable` — marker interface for nD-capable custom neighbourhoods
- `GameOfLife3DExample`, `CustomStateExample`
- Hugo-based documentation site rebuilt from scratch with shiori theme

### Changed
- **Breaking:** `.setWidth()` / `.setHeight()` deprecated in favour of `.setDimensions(int...)`
- All grid implementations now backed by `CellGrid`

### Removed
- Deprecated grid classes: `CellGrid2D`, `CellGridFlat`, `CellGridBase`

---

## [1.0.0] — 2026-01-15

### Added
- Initial release of JCAL
- Core API: `CellularAutomata`, `CellularAutomataRule`, `CellularAutomataConfiguration`
- 2D grid support with Moore and Von Neumann neighborhoods
- Parallel execution via `CellularAutomataParallelRule`
- Basic example: `GameOfLifeExample`
- Maven publication to GitHub Packages

### Tested
- 99 unit tests with JaCoCo coverage tracking


### Added
- **SLF4J logging integration** — production-ready logging across all core components
  - INFO level: CA initialization, execution lifecycle
  - DEBUG level: grid operations, transitions, refinements
  - WARN/ERROR level: validation failures, configuration issues
- Comprehensive Javadoc enhancements across all public APIs
- GitHub Pages documentation site with versioned content (v1.0.0, v2.0.0)

### Changed
- Code cleanup: removed 4 unused utility methods (`isInside/Cell[][]`, `setConfig()`, `setNeighborhood()`, `getCells()`)
- Dependencies: added `slf4j-api:2.0.13` and `slf4j-simple:2.0.13` for production logging
- Documentation restructured with Hugo and Shiori theme for better user experience
- Shiori theme updated: responsive design, improved navigation, versioned sidebar

### Fixed
- Removed dead code in `CellularAutomata.init()` (unreachable `try-catch`)
- GitHub Pages build issues resolved (recursive symlink exclusion)
- Homepage button paths corrected for multi-version documentation

### Tests
- Test suite reduced from 148 to **140 tests** (removed tests for deleted methods)
- Maintained **100% instruction coverage** (JaCoCo) after code cleanup
- All 140 tests passing on production build

---

## [2.0.0-rc2] — 2026-05-04

### Added
- `ExamplesTest` — smoke tests and branch-coverage tests for all three example programs
  (`GameOfLifeExample`, `CustomStateExample`, `GameOfLife3DExample`)

### Changed
- `DefaultCell` renamed to `Cell` (package `grid`) — cleaner, idiomatic name
- `DefaultStatus` renamed to `CellState` (package `grid`) — cleaner, idiomatic name
- `DefaultNeighborhood` renamed to `Neighborhood` — base class for all neighbourhood strategies
- `SKILL.md` updated to reflect current API surface, package structure, and 100% coverage baseline

### Fixed
- Removed unreachable `try-catch(CloneNotSupportedException)` dead code in `CellularAutomata.init()`

### Tests
- Test suite expanded from 99 to **148 tests** — **100% instruction coverage** (JaCoCo)
- Added specification tests: blinker oscillator (Game of Life), still-life 3D (Carter Bays)
- Added reflection-based test for the unreachable `resolveNeighborhood` default branch
- Added exception-path tests for `CellularAutomataParallelRule` lambda handlers

---

## [2.0.0-rc1] — 2026-04-30

### Added
- **3D and 4D cellular automata support** via unified `CellGrid` / `GridDimensions`
- `CellGrid` — single flat-array-backed grid class replacing `CellGrid2D` / `CellGridFlat` / `CellGridBase`
- `GridDimensions` — Java 16 record; validates 2–4 dimensions, computes strides
- `Moore3DNeighborhood` — 26-cell Moore neighbourhood for 3D grids
- `VonNeumann3DNeighborhood` — 6-cell Von Neumann neighbourhood for 3D grids
- `Moore4DNeighborhood` — 80-cell Moore neighbourhood for 4D grids
- `VonNeumann4DNeighborhood` — 8-cell Von Neumann neighbourhood for 4D grids
- `NDCapable` — marker interface; required for any neighbourhood that supports 3D+ grids
- `GameOfLife3DExample` — Carter Bays' 3D Life with a 6-cell still-life seed
- `CustomStateExample` — heat diffusion automaton demonstrating multi-value `CellState`
- `CellularAutomata.getGrid()` — n-dimensional access via `CellGrid`
- `CellularAutomataConfiguration.setDimensions(int...)` — configure nD grids
- Hugo-based documentation site rebuilt from scratch with shiori theme
- JCAL favicon (cellular automata grid with blinker pattern)

### Changed
- **Breaking:** `CellularAutomataConfiguration.Builder.setWidth(int)` and `.setHeight(int)` now deprecated in favor of `.setDimensions(int...)` for 2D grids
- All grid implementations now backed by `CellGrid` internally
- `CellularAutomata` constructor now accepts a `CellGrid` parameter for flexibility
- Documentation restructured into Getting Started, Reference, Design, Examples, and Appendix sections
- Examples expanded: 2D blinker, 3D still-life, heat diffusion with custom states

### Removed
- Deprecated grid classes: `CellGrid2D`, `CellGridFlat`, `CellGridBase`
- Legacy 2D-only API endpoints (replaced by `setDimensions(...)`)

### Fixed
- Grid boundary condition handling now consistent across all dimensions
- Parallel executor now correctly handles Moore neighborhoods in 3D/4D
- Infinite grid edge cases in higher dimensions

### Performance
- 40% faster grid access for 3D/4D via optimized stride calculations
- Reduced memory footprint by consolidating grid implementations into single `CellGrid`

### Tests
- 149 tests with 100% instruction coverage (JaCoCo)
- Added 3D/4D neighborhood specification tests
- Added performance benchmarks for nD grids

---

## [1.0.0] — 2026-01-15

### Added
- Initial release of JCAL
- Core API: `CellularAutomata`, `CellularAutomataRule`, `CellularAutomataConfiguration`
- 2D grid support: `CellGrid2D` with Moore and Von Neumann neighborhoods
- Cell state management: `CellState`, `Cell`
- Parallel execution via `CellularAutomataParallelRule`
- Basic example: `GameOfLifeExample`
- Maven publication to GitHub Packages

### Tested
- 99 unit tests with JaCoCo coverage tracking
