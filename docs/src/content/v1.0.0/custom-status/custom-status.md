---
title: "Custom State Objects"
description: "Model richer cell state by extending DefaultStatus with a custom Java class."
weight: 1
---

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

### The custom status class

{{< code lang="JAVA" file="custom-status/status.java">}}{{< /code >}}

### The executor

{{< code lang="JAVA" file="custom-status/executor.java">}}{{< /code >}}

### The application entry point

{{< code lang="JAVA" file="custom-status/application.java">}}{{< /code >}}

---

## Tips

- Two `DefaultStatus` values are considered **equal** when both `key` and `value` are equal.
  Override `equals` / `hashCode` in your custom class if you need value-based comparison.
- The `value` field accepts any `Object`, so you can store a `Map`, a POJO, or any
  domain object you need.

---

## See also

- [Complex Cellular Automata](../cca/) — add a refinement step before neighbor lookup.
- [Configuration Reference](../builder-settings/) — how to wire custom status into the builder.
