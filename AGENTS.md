# JCAL – Agent Guide

> This document describes the AI agents that can work on the JCAL repository,
> their responsibilities, the tasks they are expected to perform, and the
> conventions they must follow.
> It is the companion to [.github/skills/jcal-development/SKILL.md](.github/skills/jcal-development/SKILL.md),
> which lists the domain knowledge and coding conventions each agent must have.

---

## 1. Purpose of Agentic Development in JCAL

JCAL adopts an **agent-assisted development** strategy in which AI code assistants
(agents) are first-class contributors.  Agents are expected to:

- Understand the codebase from structured documentation (`README.md`,
  `ARCHITECTURE.md`, `.github/skills/jcal-development/SKILL.md`, `CONTRIBUTING.md`).
- Propose, implement, and test changes with minimal human supervision.
- Follow the same coding conventions as human contributors.
- Update documentation (`ARCHITECTURE.md`, `CHANGELOG.md`) as part of every change.

---

## 2. Agent Roles

### 2.1 Feature Agent
**Responsibility:** Implement new cellular automaton rules, neighbourhood strategies,
or library features requested in GitHub Issues.

| Task | How |
|------|-----|
| Create a new CA rule | Subclass `CellularAutomataExecutor`; implement `singleRun` |
| Create a custom neighbourhood | Subclass `DefaultNeighborhood`; implement `getNeighbors` |
| Add an example | Add a self-contained class in `examples/` with a `main` method |
| Add tests | Mirror package structure in `test/`; use `@DisplayName` |
| Update docs | Update `ARCHITECTURE.md` component map and `CHANGELOG.md` |

**Entry points to read first:**
1. `ARCHITECTURE.md` — component map and extension-point guide.
2. `.github/skills/jcal-development/SKILL.md` — coding conventions and common development tasks.
3. `src/main/java/io/github/carmelolg/jcal/examples/` — reference examples.

---

### 2.2 Refactoring Agent
**Responsibility:** Improve internal quality without changing public behaviour.

| Task | Constraint |
|------|-----------|
| Rename internal identifiers | Must not change public API signatures |
| Extract helpers into `utils/` | Class must remain package-private |
| Improve Javadoc | All public classes and methods must have Javadoc |
| Improve test coverage | Must not delete or weaken existing assertions |

> **Hard constraint:** Do **not** rename `setInitalState` — this is an intentional
> spelling in the public builder API.  Renaming it would be a breaking change.

---

### 2.3 Documentation Agent
**Responsibility:** Keep documentation accurate, up-to-date, and optimised for
both human readers and AI code assistants.

| Artefact | When to update |
|----------|---------------|
| `README.md` | New features, new agents, or structural changes |
| `ARCHITECTURE.md` | New classes, changed package structure, new extension points |
| `CHANGELOG.md` | Every merged change — add entry under `## [Unreleased]` |
| `.github/skills/jcal-development/SKILL.md` | New conventions, new tech-stack entries, API surface changes |
| `AGENTS.md` | New agent roles or task types |
| `CONTRIBUTING.md` | Changed workflow or coding guidelines |

---

### 2.4 Test Agent
**Responsibility:** Write and maintain specification-style and regression tests.

| Task | How |
|------|-----|
| Specification test | Assert on a known CA pattern (e.g. blinker oscillator in Game of Life) |
| Regression test | Reproduce a bug first, then fix it |
| Coverage gap | Identify untested paths; write minimal tests to cover them |

Test class naming: `<Subject>Test` or `<Subject>SpecificationTest`.
All tests live in `src/test/java/io/github/carmelolg/jcal/`.

---

## 3. Agent Workflow

```
1. Read the issue or task description carefully.
2. Explore the codebase:
     - ARCHITECTURE.md  (component map)
     - .github/skills/jcal-development/SKILL.md  (conventions and tasks)
     - Relevant source files in src/main/
3. Run the baseline test suite to confirm no pre-existing failures:
     mvn test
4. Implement the smallest change that satisfies the requirement.
5. Add or update tests.
6. Run the test suite again:
     mvn test
7. Update ARCHITECTURE.md and CHANGELOG.md.
8. Open or update the pull request.
```

---

## 4. Constraints and Guardrails

| Constraint | Detail |
|-----------|--------|
| Java 16 | Compile target `--release 16`; do not use features beyond Java 16 |
| No breaking changes | Public API signatures must not change without explicit approval |
| `setInitalState` | Keep the typo — it is intentional |
| All tests must pass | `mvn test` must exit 0 before opening a PR |
| Javadoc required | Every new public class and method must have Javadoc |
| Licence | Contributions are licensed under CC BY-NC-SA 4.0 |

---

## 5. Reading Order for a New Agent Session

To get up to speed as quickly as possible, read the documents in this order:

1. **`README.md`** — Quick Start + Concepts overview.
2. **`ARCHITECTURE.md`** — Package structure, component roles, extension points.
3. **`.github/skills/jcal-development/SKILL.md`** — Domain knowledge, tech stack, coding conventions.
4. **`CONTRIBUTING.md`** — Workflow and PR conventions.
5. **`CHANGELOG.md`** — What has changed recently.
6. **`AGENTS.md`** *(this file)* — Agent roles and task catalogue.

---

## 6. Related Resources

| Resource | Path / URL |
|----------|-----------|
| Architecture overview | [ARCHITECTURE.md](ARCHITECTURE.md) |
| Skill reference | [.github/skills/jcal-development/SKILL.md](.github/skills/jcal-development/SKILL.md) |
| Contribution guide | [CONTRIBUTING.md](CONTRIBUTING.md) |
| Changelog | [CHANGELOG.md](CHANGELOG.md) |
| Official docs | <https://carmelolg.github.io/JCAL/> |
