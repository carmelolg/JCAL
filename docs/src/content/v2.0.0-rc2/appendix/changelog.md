---
title: "Changelog"
date: 2026-04-30
draft: false
summary: "Release notes and version history for JCAL."
toc: true
---

All notable changes to JCAL are documented here.
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
- Core API: `CellularAutomata`, `CellularAutomataExecutor`, `CellularAutomataConfiguration`
- 2D grid support: `CellGrid2D` with Moore and Von Neumann neighborhoods
- Cell state management: `CellState`, `Cell`
- Parallel execution via `CellularAutomataParallelExecutor`
- Basic example: `GameOfLifeExample`
- Maven publication to GitHub Packages

### Tested
- 99 unit tests with JaCoCo coverage tracking
