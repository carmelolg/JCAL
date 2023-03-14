# JCAL - Java Cellular Automata Library
| | Badge|
|---|---|
| **License** | ![License: CC BY-NC-SA 4.0](https://img.shields.io/badge/License-CC_BY--NC--SA_4.0-lightgrey.svg) |
| **Unit Test** | ![Test](https://github.com/carmelolg/JCAL/workflows/tests/badge.svg) |
| **Test Coverage** | ![Coverage](.github/badges/jacoco.svg) |


## What is a Cellular Automata

Here some references:
* [Wolfram - Cellular Automaton](https://mathworld.wolfram.com/CellularAutomaton.html)
* [The nature of Code by Daniel Shiffman](https://natureofcode.com/book/chapter-7-cellular-automata/)
* [Chapter 3 of my master thesis (**in italian**)](https://github.com/carmelolg/master-thesis/blob/master/Tesi/pdf/main.pdf)

#### TLDR;
A **basic** Cellular Automata is the quadruple `<Z <sup>d</sup>,S,X,o>`

`Z <sup>d</sup>` is a set of cells, a d-dimension matrix of cells

`S` is a set of status where the single cell can be in

`X` is a set of cell's neighbors (the most common neighborhood implementation are [MOORE](https://en.wikipedia.org/wiki/Moore_neighborhood) and [VON NEUMANN](https://en.wikipedia.org/wiki/Von_Neumann_neighborhood)

 `o` is the transition function. This function implements the evolution of the natural or artificial phenomena represented by a Cellular Automata.
 
Thanks to this mathematic model, it's possible represent a lot of natural phenomena like landslides, lava flows and so on...

## What about JCAL idea

During my master's thesis, I contributed to the implementation of a library for Cellular Automata that was primarily used by physicists, geologists, and scientists from various departments. This library was written in C++, and it was more comprehensive than JCAL.

**JCAL wants to implements the same idea but in a smaller and simpler way for Java user and developers.**

## Documentation

[Here](https://carmelolg.github.io/JCAL/) the official documentation.
