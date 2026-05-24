package io.github.carmelolg.jcal.examples;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.core.CellularAutomataConfiguration;
import io.github.carmelolg.jcal.core.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder;
import io.github.carmelolg.jcal.examples.GameOfLifeExample.GameOfLifeRule;
import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.neighborhood.NeighborhoodType;
import io.github.carmelolg.jcal.ui.CellularAutomataUIRunner;

/**
 * Graphical demonstration of Conway's Game of Life using the JCAL UI layer.
 *
 * <p>This example reuses {@link GameOfLifeRule} and shows the evolution on a
 * Swing window via {@link CellularAutomataUIRunner}.  The initial pattern contains a
 * classic <em>glider</em> and a <em>blinker</em> on a 40×40 grid.
 *
 * <ul>
 *   <li><b>Glider</b> — moves diagonally across the grid.</li>
 *   <li><b>Blinker</b> — period-2 oscillator at the centre.</li>
 * </ul>
 *
 * <p>The window updates in real time.  Each cell is rendered as a 14-pixel
 * square: alive cells are {@link Color#GREEN}, dead cells are {@link Color#BLACK}.
 *
 * <p><b>Run:</b>
 * <pre>{@code
 * mvn compile exec:java \
 *   -Dexec.mainClass="io.github.carmelolg.jcal.examples.GameOfLifeUiExample"
 * }</pre>
 *
 * @see GameOfLifeExample for the console-only version
 * @see CellularAutomataUIRunner for the façade used here
 */
public class GameOfLifeUiExample {

    static final CellState DEAD  = new CellState("dead",  "0");
    static final CellState ALIVE = new CellState("alive", "1");

    public static void main(String[] args) throws Exception {

        // --- Glider (starts at top-left, moves diagonally down-right) ---
        // Classic 5-cell glider shape:
        //  . X .
        //  . . X
        //  X X X
        List<Cell> initialState = Arrays.asList(
            // Glider at (col=1, row=0)
            new Cell(ALIVE, 2, 1),
            new Cell(ALIVE, 3, 2),
            new Cell(ALIVE, 1, 3),
            new Cell(ALIVE, 2, 3),
            new Cell(ALIVE, 3, 3),

            // Blinker (horizontal) at the centre of the grid
            new Cell(ALIVE, 18, 20),
            new Cell(ALIVE, 19, 20),
            new Cell(ALIVE, 20, 20)
        );

        CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
            .setWidth(40)
            .setHeight(40)
            .setInfinite(true)
            //.setTotalIterations(120)
            .setDefaultStatus(DEAD)
            .setInitialState(initialState)
            .setNeighborhoodType(NeighborhoodType.MOORE)
            .build();

        CellularAutomata ca = new CellularAutomata(config);

        CellularAutomataUIRunner.create(ca, new GameOfLifeRule())
            .title("Game of Life — JCAL")
            .cellSize(14)
            .delay(80)
            .renderer(state -> state.equals(ALIVE) ? Color.GREEN : Color.BLACK)
            .start();

        // Keep the main thread alive long enough for the daemon thread to finish.
        Thread.sleep(120 * 80 + 3_000);
    }
}
