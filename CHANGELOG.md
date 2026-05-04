# Changelog

All notable changes to JCAL are documented in this file.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Versions align with [Semantic Versioning](https://semver.org/).

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

[Unreleased]: https://github.com/carmelolg/JCAL/compare/2.0.0-rc1...HEAD
[2.0.0-rc1]: https://github.com/carmelolg/JCAL/compare/1.0.0...2.0.0-rc1
[1.0.0]: https://github.com/carmelolg/JCAL/compare/1.0.0.alpha...1.0.0
[1.0.0-alpha]: https://github.com/carmelolg/JCAL/compare/0.1.0-alpha...1.0.0.alpha
[0.1.0-alpha]: https://github.com/carmelolg/JCAL/releases/tag/1.0.0.alpha
