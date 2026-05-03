---
name: jcal-development
description: >
  Skill reference for AI agents contributing to JCAL (Java Cellular Automata Library).
  Use this skill when working on JCAL features, tests, documentation, or refactoring tasks.
---

# JCAL – Skill Reference for AI Agents

> This document lists the domain knowledge, coding conventions, and tool skills
> that an AI code assistant needs to contribute effectively to JCAL.

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
| Class | Package | Key responsibility |
|-------|---------|-------------------|
| `CellState` | `grid` | Cell state — `key` (String) + `value` (Object). Renamed from `DefaultStatus`. |
| `Cell` | `grid` | Single grid cell — status + n-dimensional `coordinates`. Renamed from `DefaultCell`. |
| `CellGrid` | `grid` | Unified n-dimensional grid (2D–4D), flat-array backed with stride indexing. |
| `GridDimensions` | `grid` | Java 16 record; immutable size descriptor (2–4 dimensions, all sizes > 0). |
| `CellularAutomataConfiguration` | `core` | Immutable config; always built via inner `CellularAutomataConfigurationBuilder`. |
| `CellularAutomata` | `core` | The automaton — holds a `CellGrid`; exposes `getGrid()` for nD access. |
| `CellularAutomataExecutor` | `core` | **Extend this** to define a sequential transition rule. |
| `CellularAutomataParallelExecutor` | `core.parallel` | **Extend this** for a parallel transition rule. |
| `Neighborhood` | `neighborhood` | Abstract base for all neighbourhood strategies (2D and nD). |
| `NDCapable` | `neighborhood` | Marker interface — implement when the neighbourhood supports 3D+ grids. |
| `NeighborhoodType` | `neighborhood` | Enum: `MOORE` \| `VON_NEUMANN` |

> **Known API quirk:** The builder method for the initial cell list is `setInitalState`
> (one `i` — intentional, must not be renamed — breaking change).

### Built-in neighbourhood implementations
| Class | Dimensions | Neighbour count |
|-------|-----------|-----------------|
| `MooreNeighborhood` | 2D | 8 |
| `VonNeumannNeighborhood` | 2D | 4 |
| `Moore3DNeighborhood` | 3D | 26 |
| `VonNeumann3DNeighborhood` | 3D | 6 |
| `Moore4DNeighborhood` | 4D | 80 |
| `VonNeumann4DNeighborhood` | 4D | 8 |

3D/4D neighbourhoods implement `NDCapable`. The `CellularAutomata` engine validates
that nD grids are paired with `NDCapable` neighbourhoods.

### Grid coordinate convention
- **2D:** `cell.getCol()` / `cell.getRow()` — col is x-axis (left → right), row is y-axis (top → bottom).
- **nD:** `cell.getCoordinates()` — returns `int[]` with one entry per dimension; accessed via `CellGrid.get(int[] coords)`.
- `CellGrid.allCoordinates()` returns an unmodifiable list of all coordinate arrays.

### CellGrid / CellularAutomata access
```java
CellGrid grid = ca.getGrid();          // n-dimensional access (2D and nD)
Cell[][] matrix = ca.getMap();         // 2D backward-compat only
Cell cell = grid.get(new int[]{x, y}); // 2D
Cell cell = grid.get(new int[]{x, y, z}); // 3D
```

---

## 2. Technology Stack

| Tool / Library | Version | Purpose |
|----------------|---------|---------|
| Java | 16 | Language (compile target: `--release 16`) |
| Apache Maven | 3.8+ | Build, test, and dependency management |
| JUnit 5 | — | Unit and specification tests |
| JaCoCo | 0.8.14 | Code-coverage reporting (badge in README) |
| Hugo | — | Documentation site at <https://carmelolg.github.io/JCAL/> |

---

## 3. Coding Conventions

### Naming
- Classes: `UpperCamelCase`.  Methods / fields: `lowerCamelCase`.
- Test classes: `<Subject>Test` or `<Subject>SpecificationTest`.
- Prefer explicit, descriptive names over abbreviations.
- ⚠️ Do **not** rename `setInitalState` — intentional typo in public API.

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
- Use reflection (`getDeclaredMethod` + `setAccessible(true)`) only for unreachable private branches.
- Test suite currently has **148 tests** at **100% instruction coverage** (JaCoCo).

### Commit messages
Follow the conventional-commits convention:
```
feat:  add diagonal-only neighbourhood implementation
fix:   correct bounds check in VonNeumannNeighborhood
docs:  add Javadoc to CellularAutomataExecutor
test:  add blinker oscillation test
```

---

## 4. Package Structure

```
io.github.carmelolg.jcal
├── core/
│   ├── CellularAutomata.java                  # automaton orchestrator
│   ├── CellularAutomataConfiguration.java     # immutable config + builder
│   ├── CellularAutomataExecutor.java           # abstract sequential rule
│   └── parallel/
│       ├── CellularAutomataParallelExecutor.java
│       ├── CellularAutomataRunner.java
│       └── CellularAutomataRefinementRunner.java
├── grid/
│   ├── Cell.java                              # single cell (was DefaultCell)
│   ├── CellState.java                         # cell state (was DefaultStatus)
│   ├── CellGrid.java                          # unified nD grid
│   └── GridDimensions.java                    # Java 16 record, 2–4 dims
├── neighborhood/
│   ├── Neighborhood.java                      # abstract base (was DefaultNeighborhood)
│   ├── NDCapable.java                         # marker for 3D+ neighbourhoods
│   ├── NeighborhoodType.java
│   ├── MooreNeighborhood.java
│   ├── VonNeumannNeighborhood.java
│   ├── Moore3DNeighborhood.java
│   ├── VonNeumann3DNeighborhood.java
│   ├── Moore4DNeighborhood.java
│   └── VonNeumann4DNeighborhood.java
├── utils/
│   └── Utils.java                             # isInside, cloneGrid
└── examples/
    ├── GameOfLifeExample.java
    ├── CustomStateExample.java
    └── GameOfLife3DExample.java
```

---

## 5. Common Development Tasks

### Build and test
```bash
mvn test          # compile + run all JUnit 5 tests (148 tests)
mvn package       # produce the JAR
```

### Run a single example
```bash
mvn compile exec:java \
  -Dexec.mainClass="io.github.carmelolg.jcal.examples.GameOfLifeExample"
```

### Add a new CA rule (checklist)
1. Create a class that extends `CellularAutomataExecutor` (or the parallel variant).
2. Implement `singleRun(Cell cell, List<Cell> neighbors)`.
3. Optionally override `refinements(Cell cell)` for CCA pre-processing.
4. Add a test in `test/…/` using `@DisplayName` assertions.
5. Add an example in `examples/` if the rule is non-trivial.
6. Update `ARCHITECTURE.md` and `CHANGELOG.md`.

### Add a custom neighbourhood — 2D (checklist)
1. Extend `Neighborhood`.
2. Implement `getNeighbors(CellGrid grid, int[] coords)`.
3. Use `Utils.isInside(grid.getMap(), r, c)` for bounds checking.
4. Wire in via `.setNeighborhood(new YourNeighborhood())` in the builder.
5. Test with known patterns.

### Add a custom neighbourhood — 3D/4D (checklist)
1. Extend `Neighborhood` **and** implement `NDCapable`.
2. Implement `getNeighbors(CellGrid grid, int[] coords)` using stride-based iteration.
3. Use `Utils.isInside(grid.getDimensions().sizes(), coords)` for bounds checking.
4. Wire in via `.setNeighborhood(new YourNeighborhood())` in the builder.
5. Test with known patterns.

### Configure a 3D / 4D automaton
```java
CellularAutomataConfiguration cfg = new CellularAutomataConfigurationBuilder()
    .setDimensions(10, 10, 10)              // 3D grid
    .setTotalIterations(5)
    .setDefaultStatus(DEAD)
    .setNeighborhoodType(NeighborhoodType.MOORE) // resolves to Moore3DNeighborhood
    .setInitalState(initialCells)
    .build();
CellularAutomata ca = new CellularAutomata(cfg);
new MyRule().run(ca);
```

---

## 6. Extension Points Summary

| Extension point | How to use |
|-----------------|-----------|
| Custom rule (sequential) | Subclass `CellularAutomataExecutor`, implement `singleRun(Cell, List<Cell>)` |
| Custom rule (parallel) | Subclass `CellularAutomataParallelExecutor` instead |
| Custom state | Pass any `Object` as `value` to `CellState` |
| Custom neighbourhood (2D) | Subclass `Neighborhood`, implement `getNeighbors` |
| Custom neighbourhood (nD) | Subclass `Neighborhood` + implement `NDCapable` |
| CCA pre-processing | Override `refinements(Cell cell)` in your executor |
| nD grid access | Use `ca.getGrid()` → `CellGrid.get(int[])` / `CellGrid.allCoordinates()` |

---

## 7. Documentation & References

| Resource | URL / Path |
|----------|-----------|
| Official docs | <https://carmelolg.github.io/JCAL/> |
| Architecture overview | [ARCHITECTURE.md](../../../ARCHITECTURE.md) |
| Contribution guide | [CONTRIBUTING.md](../../../CONTRIBUTING.md) |
| Agent guide | [AGENTS.md](../../../AGENTS.md) |
| Changelog | [CHANGELOG.md](../../../CHANGELOG.md) |
| Wolfram – Cellular Automaton | <https://mathworld.wolfram.com/CellularAutomaton.html> |
| Moore neighbourhood | <https://en.wikipedia.org/wiki/Moore_neighborhood> |
| Von Neumann neighbourhood | <https://en.wikipedia.org/wiki/Von_Neumann_neighborhood> |
