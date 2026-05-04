+++
title = "Custom State Objects"
description = "Model richer cell state by extending DefaultStatus with a custom Java class."
weight = 5
+++

The built-in `DefaultStatus` holds a `key` (String) and a `value` (any Object), which is
sufficient for simple automata such as a binary-state Game of Life. For more complex
simulations — where each cell might carry temperature, pressure, material type, or any
other domain-specific data — you can extend `DefaultStatus` with a custom class.

## Overview

1. **Define your custom status** by extending `DefaultStatus` and adding fields.
2. **Cast in `singleRun`** — in your executor, cast `cell.getCurrentStatus()` to your
   custom class to access the extra fields.
3. **Use your custom status** as the default status and in the initial condition list.

---

## Example: Game of Life with a custom status

The following three files show a complete Game of Life implementation using a custom
status class. Although the logic is the same as the basic example, this pattern scales
naturally to automata that need richer per-cell state.

### The custom status class

Define your own class that extends `DefaultStatus` and adds domain-specific fields.

{{< code lang="JAVA" file="custom-status/status.java">}}{{< /code >}}

### The executor

In `singleRun`, cast the current status to your custom class to read its fields.
Always return a new `DefaultCell` with the next state — do not mutate the input cell.

{{< code lang="JAVA" file="custom-status/executor.java">}}{{< /code >}}

### The application entry point

Use your custom status as the `defaultStatus` and when constructing initial cells.

{{< code lang="JAVA" file="custom-status/application.java">}}{{< /code >}}

---

## Tips

- Two `DefaultStatus` values are considered **equal** when both `key` and `value` are equal.
  Override `equals` / `hashCode` in your custom class if you need value-based comparison.
- The `value` field accepts any `Object`, so you can store a `Map`, a POJO, or any
  domain object you need.
- For automata that model natural flows (e.g., heat diffusion, landslides), the custom
  status can hold numeric quantities updated each generation.

---

## See also

- [Complex Cellular Automata](../cca/) — add a refinement step before neighbor lookup.
- [Configuration Reference](../builder-settings/) — how to wire custom status into the builder.
