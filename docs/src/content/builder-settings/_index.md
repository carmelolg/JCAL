+++
title = "Configurations properties"
description = ""
weight = 4
+++

If you want to build your own cellular automata configuration (`CellularAutomataConfiguration`), you must use the `CellularAutomataConfigurationBuilder`. 

The following example shows you how to build a configuration.

**Please remember that creating an instance of `CellularAutomataConfiguration` is mandatory in order to create an instance of `CellularAutomata`.**
{{< code lang="JAVA" file="builder-settings/builder.java">}}{{< /code >}}

On this page, I will show you all the possible configuration options that you can use.


### Width

Set the matrix width (the number of columns).

**Default is 100**

{{< table style="table-striped" >}}
| Params | Type |
| -- |:--:|
| _width_ | number |
{{< /table >}}

Here is the signature of the method:

```java
public CellularAutomataConfigurationBuilder setWidth(int width);
```
---

### Height   

Set the matrix height (the number of rows)

**Default is 100**

{{< table style="table-striped" >}}
| Params | Type |
| -- |:--:|
| _height_ | number |
{{< /table >}}

Here is the signature of the method:

```java
public CellularAutomataConfigurationBuilder setHeight(int height);
```
---

### Infinite Loop

If you want to loop infinitely, simply set the following configuration to _true_.

**Default is false**

{{< table style="table-striped" >}}
| Params | Type |
| -- |:--:|
| _isInfinite_ | boolean |
{{< /table >}}

Here is the signature of the method:

```java
public CellularAutomataConfigurationBuilder setInfinite(boolean isInfinite);
```
---

### Total interactions

Set the number of iterations of the transition function

**This parameter is mandatory if isInfinite is not setted.**

{{< table style="table-striped" >}}
| Params | Type |
| -- |:--:|
| _totalIterations_ | number |
{{< /table >}}

Here is the signature of the method:

```java
public CellularAutomataConfigurationBuilder setTotalIterations(int totalIterations);
```
---

### Default status

Set the default status for each cell of the CA's map.

**This parameter is mandatory.**

{{< table style="table-striped" >}}
| Params | Type |
| -- |:--:|
| _defaultStatus_ | DefaultStatus |
{{< /table >}}

Here is the signature of the method:

```java
public CellularAutomataConfigurationBuilder setDefaultStatus(DefaultStatus defaultStatus);
```
---

### Initial condition

Set the initial configuration from where to start the cellular automata. In other words, set the cells that have a different status than empty/dead in the starting phase.

{{< table style="table-striped" >}}
| Params | Type |
| -- |:--:|
| _initalState_ | List of DefaultCell |
{{< /table >}}

Here is the signature of the method:

```java
public CellularAutomataConfigurationBuilder setInitalState(List<DefaultCell> initalState);
```
---

### Default Neighborhood

If you don't have a custom neighborhood you can choose one already implemented in the `NeighborhoodType` enum.

{{< table style="table-striped" >}}
| Params | Type |
| -- |:--:|
| _neighborhoodType_ | NeighborhoodType |
{{< /table >}}

Here is the signature of the method:

```java
public CellularAutomataConfigurationBuilder setNeighborhoodType(NeighborhoodType neighborhoodType);
```
---

### Custom Neighborhood

If you have a custom neighborhood, you can set your class here.

**The class has to inerhit the `DefaultNeighborhood` class.**

{{< table style="table-striped" >}}
| Params | Type |
| -- |:--:|
| _neighborhood_ | DefaultNeighborhood |
{{< /table >}}

Here is the signature of the method:

```java
public CellularAutomataConfigurationBuilder setNeighborhood(DefaultNeighborhood neighborhood);
```
