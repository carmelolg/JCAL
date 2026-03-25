|                   |                                                                                                   |
| :---------------- |:--------------------------------------------------------------------------------------------------|
| **Author**        | [carmelolg](https://carmelolg.github.io)                                                          |
| **License**       | ![License: CC BY-NC-SA 4.0](https://img.shields.io/badge/License-CC_BY--NC--SA_4.0-lightgrey.svg) |
| **Test Coverage** | ![Coverage](https://raw.githubusercontent.com/carmelolg/JCAL/master/.github/badges/jacoco.svg)    |
| **Latest**        | **1.0.0**                                                                                         |
| **Stable**        | **1.0.0**                                                                                         |

**JCAL** (Java Cellular Automata Library) is a lightweight Java library for building and
simulating [Cellular Automata](https://mathworld.wolfram.com/CellularAutomaton.html) with
minimal boilerplate. Define your grid, states, neighborhood strategy, and transition rule —
then let JCAL handle the rest.

## Features
<div class="row py-3 mb-5">
<div class="col-md-4">
<div class="card flex-row border-0">
<div class="mt-4">
<span class="fas fa-superscript fa-2x text-primary"></span>
</div>
<div class="card-body pl-2">
<h5 class="card-title">
Cellular Automata first.
</h5>
<p class="card-text text-muted">
Every design decision in JCAL is oriented around making it easy to model
and run Cellular Automata — from simple 2-state rules to complex multi-value state machines.
</p>
</div>
</div>
</div>
<div class="col-md-4">
<div class="card flex-row border-0">
<div class="mt-3">
<span class="fas fa-code fa-2x text-primary"></span>
</div>
<div class="card-body pl-2">
<h5 class="card-title">
Simplicity by design.
</h5>
<p class="card-text text-muted">
JCAL emphasizes <strong>simplicity</strong>: you can define and run a 2D cellular
automaton in just a few lines of Java, without boilerplate.
</p>
</div>
</div>
</div>
<div class="col-md-4">
<div class="card flex-row border-0">
<div class="mt-3">
<span class="fab fa-java fa-2x text-primary"></span>
</div>
<div class="card-body pl-2">
<h5 class="card-title">
Designed for Java.
</h5>
<p class="card-text text-muted">
Written in Java 16, JCAL follows idiomatic Java patterns — fluent builders,
abstract base classes, and standard collections — so it feels natural to Java developers.
</p>
</div>
</div>
</div>
</div>
<div class="row py-3 mb-5">
<div class="col-md-4">
<div class="card flex-row border-0">
<div class="mt-4">
<span class="fas fa-compass fa-2x text-primary"></span>
</div>
<div class="card-body pl-2">
<h5 class="card-title">
Complex Cellular Automata.
</h5>
<p class="card-text text-muted">
Complex Cellular Automata (CCA) are supported through custom state objects
and a pre-processing refinement hook, enabling rich multi-value simulations.
</p>
</div>
</div>
</div>
<div class="col-md-4">
<div class="card flex-row border-0">
<div class="mt-4">
<span class="fas fa-tachometer-alt fa-2x text-primary"></span>
</div>
<div class="card-body pl-2">
<h5 class="card-title">
Parallel execution.
</h5>
<p class="card-text text-muted">
For large grids, swap in <code>CellularAutomataParallelExecutor</code> to
distribute the transition function across threads with no API changes.
</p>
</div>
</div>
</div>
</div>


---

## Acknowledgements
- [University of Calabria](https://www.unical.it/) for inspiring foundational research in Cellular Automata.
