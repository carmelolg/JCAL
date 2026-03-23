# Changelog

All notable changes to JCAL are documented here.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).
Version numbers follow [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

### Added
- Hugo documentation site fully rewritten and expanded:
  - **Overview** page (`about-jcal`) — CA formal definition table, JCAL design goals,
    neighborhood strategy comparison, and "See also" cross-links.
  - **Getting Started** page — installation (GitHub Maven Registry, JAR download),
    Maven Central status note, and a complete Game of Life Quick Start with annotated
    code snippets.
  - **Implementing a Rule** page (replaces "Basic settings") — explains the executor
    pattern, `singleRun` contract, and parallel execution option.
  - **Configuration Reference** page (replaces "Configurations properties") — all
    builder options with corrected grammar, updated parameter names, and a usage note
    on mutually exclusive options.
  - **Custom State Objects** page (replaces "Custom status") — introduction paragraph,
    three-step overview, tips on equality and the `value` field.
  - **Complex Cellular Automata** page (`cca`) — previously disabled; now enabled with
    an explanation of the `refinements` hook, data-flow diagram, and a heat-diffusion
    code example.
  - **FAQ** page — installation, licensing, API quirks (`setInitalState` spelling,
    `setInfinite` vs `setTotalIterations`), common troubleshooting, and a
    "Contributing to the documentation" section.
  - Home page (`_index.md`) — added introductory paragraph, improved feature card
    descriptions, and added a "Parallel execution" feature card.
  - `config.toml` — corrected `languageCode` from `it-IT` to `en-US`.


- `AGENTS.md` — agent guide describing agent roles (Feature, Refactoring, Documentation,
  Test), their responsibilities, task catalogues, workflow, and guardrails for agentic
  development.  Follows the open `AGENTS.md` standard adopted by all major AI coding agents.
- `.github/skills/jcal-development/SKILL.md` — on-demand skill for AI agents covering
  required domain knowledge (CA theory, JCAL public API), technology stack, coding
  conventions, common development tasks, and extension-point checklists.  Loaded only
  when the task matches, keeping context windows lean.
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
- `README.md` — updated links to `AGENTS.md` and `.github/skills/jcal-development/SKILL.md`
  in the "Quick Start for AI Code Assistants" section and at the bottom of the
  "Documentation" section.
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
