+++
title = "Custom status"
description = ""
weight = 5
+++

"It's possible to customize the status in order to support more complex Cellular Automata than the Game of Life example.

The following code represent a Game of Life implementation but using a custom status.

### The custom status
You can add what you want inside and use the status inside the cellular automata and the transition function.
{{< code lang="JAVA" file="custom-status/status.java">}}{{< /code >}}

### The executor
In the transition function **you should cast the DefaultStatus** with your object in order to use all params and feature inside your custom status.
{{< code lang="JAVA" file="custom-status/executor.java">}}{{< /code >}}

### The main application
The main application has to use the custom status and not the DefaultStatus instance.
{{< code lang="JAVA" file="custom-status/application.java">}}{{< /code >}}