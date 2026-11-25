package io.github.carmelolg.jcal.examples;

import java.awt.Color;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.core.CellularAutomataConfiguration;
import io.github.carmelolg.jcal.core.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder;
import io.github.carmelolg.jcal.core.CellularAutomataRule;
import io.github.carmelolg.jcal.grid.Cell;
import io.github.carmelolg.jcal.grid.CellState;
import io.github.carmelolg.jcal.grid.GridSnapshot;
import io.github.carmelolg.jcal.neighborhood.NeighborhoodType;
import io.github.carmelolg.jcal.ui.GifRecorder;

/**
 * Records Conway's Game of Life, starting from a random 40x40 grid, into an animated GIF.
 *
 * <p>The output file is the first program argument, or {@code game-of-life.gif} in the
 * working directory.
 *
 * <p><b>Run:</b>
 * <pre>{@code
 * mvn compile exec:java \
 *   -Dexec.mainClass="io.github.carmelolg.jcal.examples.GameOfLifeGifExample"
 * }</pre>
 *
 * @see GifRecorder
 */
public class GameOfLifeGifExample {

    private static final Logger logger = LoggerFactory.getLogger(GameOfLifeGifExample.class);

    static final CellState DEAD  = new CellState("dead",  "0");
    static final CellState ALIVE = new CellState("alive", "1");

    private static final int SIZE = 40;
    private static final int GENERATIONS = 100;

    public static void main(String[] args) {
        Path output = Path.of(args.length > 0 ? args[0] : "game-of-life.gif");

        // A fixed seed makes every run produce the same animation.
        Random random = new Random(42);
        List<Cell> initialState = new ArrayList<>();
        for (int col = 0; col < SIZE; col++) {
            for (int row = 0; row < SIZE; row++) {
                if (random.nextInt(4) == 0) initialState.add(new Cell(ALIVE, col, row));
            }
        }

        CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
                .setWidth(SIZE)
                .setHeight(SIZE)
                .setTotalIterations(GENERATIONS)
                .setDefaultStatus(DEAD)
                .setInitialState(initialState)
                .setNeighborhoodType(NeighborhoodType.MOORE)
                .build();

        CellularAutomata ca = new CellularAutomata(config);
        CellularAutomataRule rule = new GameOfLifeRule();

        try (GifRecorder gif = GifRecorder.create(output, state -> state.equals(ALIVE) ? Color.GREEN : Color.BLACK)
                .cellSize(8)
                .delay(100)) {
            gif.record(GridSnapshot.of(0, ca.getGrid())); // the initial state
            rule.addGenerationListener(gif);
            rule.run(ca);
        }

        logger.info("GIF written to {}", output.toAbsolutePath());
    }

    /** Conway's Game of Life rule (B3/S23). */
    static class GameOfLifeRule extends CellularAutomataRule {

        @Override
        public Cell transition(Cell cell, List<Cell> neighbors) {
            long aliveCount = neighbors.stream()
                    .filter(n -> n.getCurrentStatus().equals(ALIVE))
                    .count();
            boolean alive = cell.getCurrentStatus().equals(ALIVE);
            boolean nextAlive = aliveCount == 3 || (alive && aliveCount == 2);
            return new Cell(nextAlive ? ALIVE : DEAD, cell.getCol(), cell.getRow());
        }
    }
}
