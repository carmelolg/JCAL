# Contributing to JCAL

Thank you for your interest in contributing to JCAL!
This guide explains how to set up the project, make changes, and submit a pull request.

---

## Prerequisites

| Tool | Minimum version |
|------|----------------|
| Java JDK | 16 |
| Apache Maven | 3.8+ |
| Git | any recent version |

---

## Getting Started

```bash
# 1. Fork the repository and clone your fork
git clone https://github.com/<your-username>/JCAL.git
cd JCAL

# 2. Build and run the test suite to verify a clean baseline
mvn test
```

All tests should pass before you make any changes.

---

## Project Structure

```
src/
  main/java/io/github/carmelolg/jcal/
    configuration/   ← CellularAutomataConfiguration + Builder
    core/            ← CellularAutomata, executors, neighborhoods
    model/           ← DefaultCell, DefaultStatus, NeighborhoodType
    utils/           ← internal helpers
    examples/        ← runnable reference examples
  test/java/io/github/carmelolg/jcal/
    …                ← JUnit 5 tests (mirror the main package layout)
```

See [ARCHITECTURE.md](ARCHITECTURE.md) for a full component map and extension-point guide.

---

## Development Workflow

1. Create a feature branch from `master`:
   ```bash
   git checkout -b feature/my-feature
   ```

2. Make your changes following the guidelines below.

3. Run the tests:
   ```bash
   mvn test
   ```

4. Commit with a clear, concise message:
   ```
   feat: add diagonal-only neighborhood implementation
   fix: correct bounds check in VonNeumannNeighborhood
   docs: add Javadoc to CellularAutomataExecutor
   ```

5. Push your branch and open a pull request against `master`.

---

## Coding Guidelines

### General

- Use **Java 16** language features (the project is compiled with `--release 16`).
- Prefer **explicit, descriptive names** over abbreviations.
- Do not add magic constants; document defaults and assumptions in Javadoc.
- Keep classes focused: one responsibility per class.

### Javadoc

All **public** classes and methods must have Javadoc that includes:
- A one-line summary.
- A `<p>` paragraph for typical usage or a `<pre>{@code ...}</pre>` example where helpful.
- `@param`, `@return`, and `@throws` tags where applicable.
- `@see` references to related types.

### Tests

- Mirror the package structure: tests for `core/Foo.java` go in `test/…/core/FooTest.java`.
- Use `@DisplayName` to describe the behaviour in plain English.
- New features must be accompanied by at least one test.
- Specification-style tests (asserting known patterns) are strongly encouraged for new rules or neighborhoods.

### Examples

If you add a new feature, consider adding a self-contained example in
`src/main/java/io/github/carmelolg/jcal/examples/`.  Examples must:
- Run with `main(String[] args)`.
- Be fully commented (explain *intent*, not just what the line does).
- Demonstrate the minimum amount of code needed to use the feature.

---

## For AI Agents and Automated Contributors

This project welcomes contributions from AI code assistants.  Please ensure:

1. **No breaking changes** to existing public API without explicit documentation of the change.
2. **All tests pass** (`mvn test`) before submitting.
3. New code follows the Javadoc and naming conventions above.
4. The [ARCHITECTURE.md](ARCHITECTURE.md) is updated if you add new classes or change the package structure.
5. [CHANGELOG.md](CHANGELOG.md) is updated with a human-readable entry under `## [Unreleased]`.

---

## Reporting Issues

Open a GitHub issue with:
- A short description of the problem or feature request.
- A minimal reproducer (code snippet or test case) if applicable.
- The Java version and OS you are using.

---

## License

By contributing, you agree that your contributions will be licensed under the same
[CC BY-NC-SA 4.0](LICENSE.md) license as the rest of the project.
