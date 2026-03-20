# Changelog

All notable changes to JCAL are documented here.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).
Version numbers follow [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

### Added
- `ARCHITECTURE.md` — component map, extension-point guide, and data-flow description
  optimised for AI code assistants and new contributors.
- `CONTRIBUTING.md` — developer guide covering setup, coding conventions, and AI-agent
  contribution rules.
- `src/main/java/io/github/carmelolg/jcal/examples/GameOfLifeExample.java` — fully
  commented, runnable Game of Life example demonstrating the four-step JCAL workflow.
- `src/main/java/io/github/carmelolg/jcal/examples/CustomStateExample.java` — advanced
  example showing a three-state heat diffusion automaton with custom `DefaultStatus` values.
- `GameOfLifeSpecificationTest` — living-specification tests for the blinker oscillator,
  block still life, and underpopulation death.
- `CustomStateSpecificationTest` — living-specification tests for multi-value custom states
  and heat diffusion propagation.
- Comprehensive Javadoc on all public-facing classes (`CellularAutomata`,
  `CellularAutomataExecutor`, `CellularAutomataParallelExecutor`, `DefaultNeighborhood`,
  `MooreNeighborhood`, `VonNeumannNeighborhood`, `DefaultCell`, `DefaultStatus`,
  `NeighborhoodType`, `CellularAutomataConfiguration`, `Utils`).
- README — "Quick Start for AI Code Assistants" and "Concepts" sections.

### Changed
- README restructured with a Quick Start section and a Concepts reference table.
- Existing class-level Javadoc updated for clarity and consistency.

---

## [1.0.0.alpha] – initial release

### Added
- Core grid model: `CellularAutomata`, `DefaultCell`, `DefaultStatus`.
- `CellularAutomataConfiguration` with a fluent builder.
- `CellularAutomataExecutor` abstract base class for sequential execution.
- `CellularAutomataParallelExecutor` abstract base class for parallel execution.
- Built-in Moore and Von Neumann neighborhood implementations.
- JUnit 5 test suite covering configuration, execution, neighborhoods, and models.
- Hugo-based documentation site at <https://carmelolg.github.io/JCAL/>.
- GitHub Actions CI workflow (`build` + JaCoCo coverage badge).
