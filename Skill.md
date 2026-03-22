# JCAL – Skill Reference for AI Agents

> This document lists the domain knowledge, coding conventions, and tool skills
> that an AI code assistant needs to contribute effectively to JCAL.
> It is the companion to [Agent.md](Agent.md), which describes the available agents
> and their responsibilities.

---

## 1. Required Domain Knowledge

### Cellular Automata Theory
| Concept | Notes |
|---------|-------|
| Formal CA definition `<Zd, S, X, σ>` | Grid, states, neighbourhood, transition function |
| Moore neighbourhood | 8 surrounding cells (orthogonal + diagonal) |
| Von Neumann neighbourhood | 4 orthogonal cells only |
| Transition function | Applied once per cell per generation |
| Complex Cellular Automata (CCA) | Requires a pre-processing refinement step before neighbour lookup |

### JCAL API (public surface)
| Class | Key responsibility |
|-------|-------------------|
| `DefaultStatus` | Cell state — `key` (String) + `value` (Object) |
| `DefaultCell` | Single grid cell — status + `(col, row)` coordinates |
| `CellularAutomataConfiguration` | Immutable config; always built via inner `Builder` |
| `CellularAutomata` | The 2-D grid (`map[col][row]`) |
| `CellularAutomataExecutor` | **Extend this** to define a sequential transition rule |
| `CellularAutomataParallelExecutor` | **Extend this** for a parallel transition rule |
| `DefaultNeighborhood` | **Extend this** to define a custom neighbourhood shape |
| `NeighborhoodType` | Enum: `MOORE` \| `VON_NEUMANN` |

> **Known API quirk:** The builder method for the initial cell list is `setInitalState`
> (one `i` — intentional, kept to avoid a breaking change).

### Grid coordinate convention
`map[col][row]` — `col` is the x-axis (left → right), `row` is the y-axis (top → bottom).

---

## 2. Technology Stack

| Tool / Library | Version | Purpose |
|----------------|---------|---------|
| Java | 16 | Language (compile target: `--release 16`) |
| Apache Maven | 3.8+ | Build, test, and dependency management |
| JUnit 5 | — | Unit and specification tests |
| JaCoCo | — | Code-coverage reporting (badge in README) |
| Hugo | — | Documentation site at <https://carmelolg.github.io/JCAL/> |

---

## 3. Coding Conventions

### Naming
- Classes: `UpperCamelCase`.  Methods / fields: `lowerCamelCase`.
- Test classes: `<Subject>Test` or `<Subject>SpecificationTest`.
- Prefer explicit, descriptive names over abbreviations.

### Javadoc
All **public** classes and methods must have Javadoc:
- One-line summary.
- `<p>` paragraph or `<pre>{@code ...}</pre>` usage example where helpful.
- `@param`, `@return`, `@throws` tags where applicable.
- `@see` references to related types.

### Tests
- Mirror the main package layout: `test/…/core/FooTest.java` for `main/…/core/Foo.java`.
- Use `@DisplayName` to express behaviour in plain English.
- Every new feature requires at least one test.
- Specification-style tests (assert on known CA patterns) are strongly preferred.

### Commit messages
Follow the conventional-commits convention:
```
feat:  add diagonal-only neighbourhood implementation
fix:   correct bounds check in VonNeumannNeighborhood
docs:  add Javadoc to CellularAutomataExecutor
test:  add blinker oscillation test
```

---

## 4. Common Development Tasks

### Build and test
```bash
mvn test          # compile + run all JUnit 5 tests
mvn package       # produce the JAR
```

### Run a single example
```bash
mvn compile exec:java \
  -Dexec.mainClass="io.github.carmelolg.jcal.examples.GameOfLifeExample"
```

### Add a new CA rule (checklist)
1. Create a class that extends `CellularAutomataExecutor` (or the parallel variant).
2. Implement `singleRun(DefaultCell cell, List<DefaultCell> neighbors)`.
3. Optionally override `refinements(DefaultCell cell)` for CCA pre-processing.
4. Add a test in `test/…/` using `@DisplayName` assertions.
5. Add an example in `examples/` if the rule is non-trivial.
6. Update `ARCHITECTURE.md` and `CHANGELOG.md`.

### Add a custom neighbourhood (checklist)
1. Extend `DefaultNeighborhood`.
2. Implement `getNeighbors(DefaultCell[][] matrix, int i, int j)`.
3. Use `Utils.isInside(matrix, r, c)` for bounds checking.
4. Wire in via `.setNeighborhood(new YourNeighborhood())` in the builder.
5. Test with known patterns.

---

## 5. Extension Points Summary

| Extension point | How to use |
|-----------------|-----------|
| Custom rule | Subclass `CellularAutomataExecutor`, implement `singleRun` |
| Custom state | Pass any `Object` as `value` to `DefaultStatus` |
| Custom neighbourhood | Subclass `DefaultNeighborhood`, implement `getNeighbors` |
| CCA pre-processing | Override `refinements(cell)` in your executor |
| Parallel execution | Subclass `CellularAutomataParallelExecutor` instead |

---

## 6. Documentation & References

| Resource | URL / Path |
|----------|-----------|
| Official docs | <https://carmelolg.github.io/JCAL/> |
| Architecture overview | [ARCHITECTURE.md](ARCHITECTURE.md) |
| Contribution guide | [CONTRIBUTING.md](CONTRIBUTING.md) |
| Agent guide | [Agent.md](Agent.md) |
| Changelog | [CHANGELOG.md](CHANGELOG.md) |
| Wolfram – Cellular Automaton | <https://mathworld.wolfram.com/CellularAutomaton.html> |
| Moore neighbourhood | <https://en.wikipedia.org/wiki/Moore_neighborhood> |
| Von Neumann neighbourhood | <https://en.wikipedia.org/wiki/Von_Neumann_neighborhood> |
