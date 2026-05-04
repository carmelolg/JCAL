---
title: "Neighborhoods"
date: 2025-01-01
draft: false
summary: "Built-in and custom neighborhood strategies for 2D, 3D, and 4D grids."
weight: 50
toc: true
tags: ["neighborhood", "moore", "von-neumann"]
---

The **neighborhood** (X) defines which cells are considered "neighbors" of a given cell
when applying the transition function. JCAL ships with built-in neighborhoods for 2D,
3D, and 4D grids, and provides extension points for custom shapes.

---

## Built-in Neighborhoods

Select a built-in neighborhood via `NeighborhoodType` in the builder:

```java
.setNeighborhoodType(NeighborhoodType.MOORE)
// or
.setNeighborhoodType(NeighborhoodType.VON_NEUMANN)
```

JCAL automatically resolves the correct class (2D, 3D, or 4D) based on the configured
grid dimensions.

### Moore Neighborhood

The **Moore neighborhood** includes all cells within Chebyshev distance 1 — orthogonal
*and* diagonal neighbors.

| Dimension | Neighbors | Class |
|-----------|-----------|-------|
| 2D | 8 | `MooreNeighborhood` |
| 3D | 26 | `Moore3DNeighborhood` |
| 4D | 80 | `Moore4DNeighborhood` |

```
2D example (center = X):
[ ][ ][ ]
[ ][X][ ]
[ ][ ][ ]
All 8 surrounding cells are neighbors.
```

### Von Neumann Neighborhood

The **Von Neumann neighborhood** includes only the cells sharing a face with the center
cell — orthogonal neighbors only, no diagonals.

| Dimension | Neighbors | Class |
|-----------|-----------|-------|
| 2D | 4 | `VonNeumannNeighborhood` |
| 3D | 6 | `VonNeumann3DNeighborhood` |
| 4D | 8 | `VonNeumann4DNeighborhood` |

```
2D example (center = X):
   [ ]
[ ][X][ ]
   [ ]
Only 4 orthogonal cells are neighbors.
```

---

## 2D Custom Neighborhood

Extend `Neighborhood` and implement `getNeighbors(Cell[][] matrix, int i, int j)`.
Use `Utils.isInside(matrix, row, col)` to avoid out-of-bounds access.

```java
public class DiagonalOnlyNeighborhood extends Neighborhood {

    @Override
    public List<Cell> getNeighbors(Cell[][] matrix, int i, int j) {
        List<Cell> neighbors = new ArrayList<>();
        int[][] diagonals = { {-1, -1}, {-1, 1}, {1, -1}, {1, 1} };

        for (int[] d : diagonals) {
            if (Utils.isInside(matrix, i + d[0], j + d[1])) {
                neighbors.add(matrix[i + d[0]][j + d[1]]);
            }
        }
        return neighbors;
    }
}
```

Pass your instance to the builder:

```java
.setNeighborhood(new DiagonalOnlyNeighborhood())
```

{{< callout type="warning" >}}
Use either `setNeighborhoodType` **or** `setNeighborhood` — never both.
{{< /callout >}}

---

## 3D/4D Custom Neighborhood

Extend `Neighborhood` and implement `getNeighbors(CellGrid grid, int[] coords)`.

```java
public class Custom3DNeighborhood extends Neighborhood {

    @Override
    public List<Cell> getNeighbors(CellGrid grid, int[] coords) {
        List<Cell> neighbors = new ArrayList<>();
        int x = coords[0], y = coords[1], z = coords[2];

        // Example: only the 6 face-sharing neighbors (Von Neumann)
        int[][] offsets = {
            {-1,0,0}, {1,0,0},
            {0,-1,0}, {0,1,0},
            {0,0,-1}, {0,0,1}
        };

        for (int[] off : offsets) {
            int[] neighbor = { x + off[0], y + off[1], z + off[2] };
            if (grid.isInside(neighbor)) {
                neighbors.add(grid.get(neighbor));
            }
        }
        return neighbors;
    }
}
```

Then pass it via the builder:

```java
.setNeighborhood(new Custom3DNeighborhood())
```

---

## Boundary Behavior

By default, cells at the grid boundary have fewer neighbors because JCAL does not wrap
the grid (no toric topology). Neighbors outside the grid bounds are simply omitted from
the list passed to `singleRun`.

If you need wrap-around behavior, implement it in your custom `Neighborhood` by
applying modular arithmetic to the row/column indices.

---

## See Also

- [Implementing a Rule](../implementing-a-rule/) — how to use the neighbors list in `singleRun`.
- [3D and 4D Support](../3d-4d-support/) — multi-dimensional grids and neighborhoods.
- [Configuration Reference](../configuration/) — how to register a neighborhood in the builder.
