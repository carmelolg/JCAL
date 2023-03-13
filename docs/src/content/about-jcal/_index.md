+++
title = "About JCAL"
description = ""
weight = 1
+++

## What is a Cellular Automata

Here some references:
* [Wolfram - Cellular Automaton](https://mathworld.wolfram.com/CellularAutomaton.html)
* [The nature of Code by Daniel Shiffman](https://natureofcode.com/book/chapter-7-cellular-automata/)
* [Chapter 3 of my master thesis (**in italian**)](https://github.com/carmelolg/master-thesis/blob/master/Tesi/pdf/main.pdf)

#### TLDR;
A **basic** Cellular Automata is the quadruple `<Z`<span style="color: #e83e8c; font-size:87.5%;"><sup>d</sup></span>`,S,X,` <span style="color: #e83e8c; font-size:87.5%;">&alpha;</span>`>`

`Z`<span style="color: #e83e8c; font-size:87.5%;"><sup>d</sup></span> is a set of cells, a d-dimension matrix of cells

`S` is a set of status where the single cell can be in

`X` is a set of cell's neighbors (the most common neighborhood implementation are [MOORE](https://en.wikipedia.org/wiki/Moore_neighborhood) and [VON NEUMANN](https://en.wikipedia.org/wiki/Von_Neumann_neighborhood)

 <span style="color: #e83e8c; font-size:87.5%;">&alpha;</span> is the transition function. This function implements the evolution of the natural or artificial phenomena represented by a Cellular Automata.
 
Thanks to this mathematic model, it's possible represent a lot of natural phenomena like landslides, lava flows and so on...

--- 
## What about JCAL idea

In the past, during my master thesis's work, I contribuited to implement a library for Cellular Automata, mainly used by Physicists, Geologists and Scientists from different departments. This library was written in C++ and obviously was more complete than JCAL. 

**JCAL wants to implements the same idea but in a smaller and simpler way for Java user and developers.**
