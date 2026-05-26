# Changelog

All notable changes to JCAL are documented in this file.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Versions align with [Semantic Versioning](https://semver.org/).

---

## [2.2.0] — 2026-05-26

### Added
- `CellularAutomataException` — unchecked exception replacing raw `throws Exception` across core engine

### Changed
- `CellularAutomataParallelRule` — `addGenerationListener()` and `notifyListeners()` now mirror the sequential engine
- `CellularAutomataRunner` / `CellularAutomataRefinementRunner` — eliminated O(n²) coordinate scan; replaced with `subList`-based slice indexing
- Removed unused `offset` parameter from runner constructors
- **`core.parallel` sub-package eliminated** — `CellularAutomataParallelRule`, `CellularAutomataRunner`,
  and `CellularAutomataRefinementRunner` moved from `core.parallel` into `core` directly.
  Update imports from `io.github.carmelolg.jcal.core.parallel.*` to `io.github.carmelolg.jcal.core.*`.
- **`getUtilsGrid()` is now package-private** in `CellularAutomata` — `@Deprecated public` removed;
  the method is an internal double-buffer detail not accessible to library users.

### Fixed
- Removed `throws SecurityException` from `innerRun()`

### Tests
- `CellularAutomataTest` updated: `assertThrows` now expects `CellularAutomataException`
- Added `GenerationListener` tests in `CellularAutomataParallelRuleTest`

---

## [2.1.0] — 2026-05-22

### Added
- `GenerationListener` — `@FunctionalInterface` callback invoked after each generation (package `core`)
- `GridSnapshot` — immutable grid state snapshot passed to listeners (package `grid`)
- `CellularAutomataRule.addGenerationListener()` — register listeners on the sequential engine
- **Swing UI layer** (package `ui`):
  - `CellRenderer` — interface for custom cell rendering
  - `GridPanel` — `JPanel` subclass rendering a `GridSnapshot`
  - `GridDisplay` — `JFrame` wrapper managing the display lifecycle
  - `CellularAutomataDisplay` — high-level facade combining grid + display
  - `AutomataListener` — `GenerationListener` implementation bridging engine to UI
  - `CellularAutomataUIRunner` — convenience runner wiring engine + UI together
- UI examples: `GameOfLifeUiExample`, `GameOfLife3DUiExample`, `GameOfLifeAdvancedUiExample`
- Documentation site updated to v2.1.0 with new pages: `generation-listener`, `ui-visualization`, `game-of-life-ui`

### Changed
- `CellularAutomataExecutor` renamed to `CellularAutomataRule`
- `CellularAutomataParallelExecutor` renamed to `CellularAutomataParallelRule`
- `singleRun()` renamed to `transition()` in both rule classes and all usages
- `hugo.toml` updated: `currentVersion → v2.1.0`, v2.1.0 added to versions list

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
- Added exception-path tests for `CellularAutomataParallelExecutor` lambda handlers

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
- `Neighborhood` (previously `DefaultNeighborhood`) unified to one abstract base — 2D subclasses
  override `getNeighbors(CellGrid, int[])` instead of the old `DefaultCell[][]` signature
- `CellularAutomata.resolveNeighborhood` extended to auto-select 3D/4D neighbourhood from `NeighborhoodType`
- `CellularAutomata.check()` validates that nD grids are paired with `NDCapable` neighbourhoods

### Removed
- `CellGrid2D`, `CellGridFlat`, `CellGridBase` — replaced by the unified `CellGrid`
- `DefaultNeighborhoodND` — merged into the unified `Neighborhood` + `NDCapable` model

---

## [1.0.0] — 2026-03-25

### Added
- `AGENTS.md` and `SKILL.md` — agent-friendly documentation for AI code assistants
- Comprehensive Javadoc on all public classes and methods
- `GameOfLifeExample` — fully commented minimal example with blinker pattern
- Agentic-development infrastructure: agent roles, task catalogue, workflow

### Changed
- Package renamed from `it.carmelolagamba` → `io.github.carmelolg`
- `CellularAutomataConfiguration` builder: improved defaults and validation
- Hugo documentation theme improved with professional colour palette and typography

### Fixed
- pom.xml cleaned and dependency versions pinned
- JUnit test suite stabilised (previously flaky on CI)
- Hugo build fixed: extended version enabled for SCSS/SASS support
- GitHub Actions updated to latest action versions

### Tests
- Coverage increased from 76% to 97% (JaCoCo)

---

## [1.0.0-alpha] — 2023-03-17

### Added
- `CellularAutomataParallelExecutor` — parallel execution via Java streams
- `CellularAutomataRunner` / `CellularAutomataRefinementRunner` — parallel helpers
- Complex Cellular Automata (CCA) support: `refinements(Cell)` hook in executor
- `DefaultStatus.value` field — arbitrary `Object` for richer cell states
- JaCoCo coverage badge in CI

### Changed
- `CellularAutomataExecutor` — refined `singleRun` / `refinements` contract
- Internal refactor: optimised imports, encoding fixes

---

## [0.1.0-alpha] — 2023-02-17

### Added
- First alpha release of JCAL
- `DefaultCell` — single cell with `(col, row)` coordinates and status
- `DefaultStatus` — cell state with `key` + `value`
- `CellularAutomataConfiguration` with inner `CellularAutomataConfigurationBuilder`
- `CellularAutomata` — 2D grid orchestrator (`map[col][row]`)
- `CellularAutomataExecutor` — abstract base for sequential transition rules
- `MooreNeighborhood` — 8-cell 2D Moore neighbourhood
- `VonNeumannNeighborhood` — 4-cell 2D Von Neumann neighbourhood
- `Utils.isInside` / `Utils.cloneGrid` — grid utility helpers
- JUnit 5 test suite
- GitHub Actions CI pipeline with JaCoCo coverage reporting

---

[Unreleased]: https://github.com/carmelolg/JCAL/compare/v2.1.0...HEAD
[2.1.0]: https://github.com/carmelolg/JCAL/compare/2.0.0...v2.1.0
[2.0.0]: https://github.com/carmelolg/JCAL/compare/2.0.0-rc2...2.0.0
[2.0.0-rc2]: https://github.com/carmelolg/JCAL/compare/2.0.0-rc1...2.0.0-rc2
[2.0.0-rc1]: https://github.com/carmelolg/JCAL/compare/1.0.0...2.0.0-rc1
[1.0.0]: https://github.com/carmelolg/JCAL/compare/1.0.0.alpha...1.0.0
[1.0.0-alpha]: https://github.com/carmelolg/JCAL/compare/0.1.0-alpha...1.0.0.alpha
[0.1.0-alpha]: https://github.com/carmelolg/JCAL/releases/tag/1.0.0.alpha
