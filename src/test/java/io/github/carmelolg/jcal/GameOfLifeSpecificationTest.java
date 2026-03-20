package io.github.carmelolg.jcal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.carmelolg.jcal.configuration.CellularAutomataConfiguration;
import io.github.carmelolg.jcal.configuration.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder;
import io.github.carmelolg.jcal.core.CellularAutomata;
import io.github.carmelolg.jcal.core.CellularAutomataExecutor;
import io.github.carmelolg.jcal.model.DefaultCell;
import io.github.carmelolg.jcal.model.DefaultStatus;
import io.github.carmelolg.jcal.model.NeighborhoodType;

/**
 * Specification tests for Conway's Game of Life using JCAL.
 *
 * <p>Each test encodes a <em>known pattern</em> from Game of Life theory and
 * asserts that the automaton evolves exactly as expected.  These tests serve as
 * a <b>living specification</b>: they document expected behaviour and catch regressions.
 *
 * <p>Patterns covered:
 * <ul>
 *   <li><b>Blinker</b> – a period-2 oscillator (3 cells in a row).</li>
 *   <li><b>Block</b> – a 2x2 still life (does not change between generations).</li>
 *   <li><b>Underpopulation</b> – an isolated alive cell dies because it has no neighbours.</li>
 * </ul>
 *
 * <p>Grid coordinates follow the convention used throughout JCAL:
 * {@code map[col][row]} where {@code col} is the x-axis (left to right) and
 * {@code row} is the y-axis (top to bottom).
 */
@DisplayName("Game of Life – specification tests")
public class GameOfLifeSpecificationTest {

    // Canonical Game of Life statuses
    private static final DefaultStatus DEAD  = new DefaultStatus("dead",  "0");
    private static final DefaultStatus ALIVE = new DefaultStatus("alive", "1");

    // -------------------------------------------------------------------------
    // Helper: build a CA from a list of initial live cells and run it N steps
    // -------------------------------------------------------------------------

    private CellularAutomata runGameOfLife(int width, int height,
                                           List<DefaultCell> initialState,
                                           int iterations) throws Exception {
        CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
            .setWidth(width)
            .setHeight(height)
            .setInfinite(false)
            .setTotalIterations(iterations)
            .setDefaultStatus(DEAD)
            .setInitalState(initialState)
            .setNeighborhoodType(NeighborhoodType.MOORE)
            .build();

        CellularAutomata ca = new CellularAutomata(config);
        return new GameOfLifeRule().run(ca);
    }

    // -------------------------------------------------------------------------
    // Test 1: Blinker oscillator (period 2)
    // -------------------------------------------------------------------------

    /**
     * The <em>blinker</em> is the simplest oscillator in Game of Life.
     *
     * <p>Initial state (horizontal):
     * <pre>
     *   . . . . .
     *   . X X X .   (row 2, cols 1-3)
     *   . . . . .
     * </pre>
     *
     * <p>After 1 step (vertical):
     * <pre>
     *   . . X . .   (col 2, rows 1-3)
     *   . . X . .
     *   . . X . .
     * </pre>
     *
     * <p>After 2 steps the blinker returns to its original horizontal orientation.
     */
    @Test
    @DisplayName("Blinker oscillates back to its original state after 2 generations")
    void blinkerOscillatesWithPeriodTwo() throws Exception {
        // Horizontal blinker at row=2, cols 1..3
        List<DefaultCell> horizontalBlinker = Arrays.asList(
            new DefaultCell(ALIVE, 1, 2),
            new DefaultCell(ALIVE, 2, 2),
            new DefaultCell(ALIVE, 3, 2)
        );

        // After 2 steps the blinker is back in the horizontal orientation
        CellularAutomata result = runGameOfLife(7, 7, horizontalBlinker, 2);

        assertNotNull(result, "CA result must not be null");
        assertEquals(ALIVE, result.getMap()[1][2].getCurrentStatus(), "col=1,row=2 should be alive");
        assertEquals(ALIVE, result.getMap()[2][2].getCurrentStatus(), "col=2,row=2 should be alive");
        assertEquals(ALIVE, result.getMap()[3][2].getCurrentStatus(), "col=3,row=2 should be alive");
        // Cells above/below the original blinker should be dead again
        assertEquals(DEAD, result.getMap()[2][1].getCurrentStatus(), "col=2,row=1 should be dead");
        assertEquals(DEAD, result.getMap()[2][3].getCurrentStatus(), "col=2,row=3 should be dead");
    }

    /**
     * After exactly 1 step the blinker flips to vertical orientation.
     */
    @Test
    @DisplayName("Blinker flips to vertical orientation after 1 generation")
    void blinkerFlipsToVerticalAfterOneStep() throws Exception {
        List<DefaultCell> horizontalBlinker = Arrays.asList(
            new DefaultCell(ALIVE, 1, 2),
            new DefaultCell(ALIVE, 2, 2),
            new DefaultCell(ALIVE, 3, 2)
        );

        CellularAutomata result = runGameOfLife(7, 7, horizontalBlinker, 1);

        assertNotNull(result);
        // Vertical blinker: col=2, rows 1..3
        assertEquals(ALIVE, result.getMap()[2][1].getCurrentStatus(), "col=2,row=1 should be alive");
        assertEquals(ALIVE, result.getMap()[2][2].getCurrentStatus(), "col=2,row=2 should be alive");
        assertEquals(ALIVE, result.getMap()[2][3].getCurrentStatus(), "col=2,row=3 should be alive");
        // Original horizontal cells at cols 1 and 3 should now be dead
        assertEquals(DEAD, result.getMap()[1][2].getCurrentStatus(), "col=1,row=2 should be dead");
        assertEquals(DEAD, result.getMap()[3][2].getCurrentStatus(), "col=3,row=2 should be dead");
    }

    // -------------------------------------------------------------------------
    // Test 2: Block – a 2x2 still life
    // -------------------------------------------------------------------------

    /**
     * The <em>block</em> is the simplest still life in Game of Life: a 2x2 square of
     * alive cells that never changes.
     *
     * <p>Pattern:
     * <pre>
     *   . . . . .
     *   . X X . .   (rows 1-2, cols 1-2)
     *   . X X . .
     *   . . . . .
     * </pre>
     *
     * <p>After any number of steps the block must remain unchanged.
     */
    @Test
    @DisplayName("Block (2x2 still life) does not change after 5 generations")
    void blockRemainsUnchangedAfterFiveGenerations() throws Exception {
        List<DefaultCell> block = Arrays.asList(
            new DefaultCell(ALIVE, 1, 1),
            new DefaultCell(ALIVE, 1, 2),
            new DefaultCell(ALIVE, 2, 1),
            new DefaultCell(ALIVE, 2, 2)
        );

        CellularAutomata result = runGameOfLife(6, 6, block, 5);

        assertNotNull(result);
        // All four block cells must still be alive
        assertEquals(ALIVE, result.getMap()[1][1].getCurrentStatus(), "col=1,row=1 should be alive");
        assertEquals(ALIVE, result.getMap()[1][2].getCurrentStatus(), "col=1,row=2 should be alive");
        assertEquals(ALIVE, result.getMap()[2][1].getCurrentStatus(), "col=2,row=1 should be alive");
        assertEquals(ALIVE, result.getMap()[2][2].getCurrentStatus(), "col=2,row=2 should be alive");
        // Immediate neighbours of the block must remain dead
        assertEquals(DEAD, result.getMap()[0][1].getCurrentStatus(), "col=0,row=1 should be dead");
        assertEquals(DEAD, result.getMap()[3][2].getCurrentStatus(), "col=3,row=2 should be dead");
    }

    // -------------------------------------------------------------------------
    // Test 3: Underpopulation – isolated cell dies
    // -------------------------------------------------------------------------

    /**
     * An isolated alive cell has zero alive neighbours and therefore dies in one step
     * (underpopulation rule).
     */
    @Test
    @DisplayName("Isolated alive cell dies from underpopulation after 1 generation")
    void isolatedCellDiesFromUnderpopulation() throws Exception {
        List<DefaultCell> singleCell = Arrays.asList(
            new DefaultCell(ALIVE, 5, 5)
        );

        CellularAutomata result = runGameOfLife(10, 10, singleCell, 1);

        assertNotNull(result);
        assertEquals(DEAD, result.getMap()[5][5].getCurrentStatus(),
            "Isolated cell should die from underpopulation");
    }

    // -------------------------------------------------------------------------
    // Inner class: Game of Life transition rule (mirrors GameOfLifeExample)
    // -------------------------------------------------------------------------

    private static class GameOfLifeRule extends CellularAutomataExecutor {
        @Override
        public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {
            long aliveCount = neighbors.stream()
                .filter(n -> n.getCurrentStatus().equals(ALIVE))
                .count();

            DefaultCell next = new DefaultCell(DEAD, cell.getCol(), cell.getRow());
            boolean isAlive = cell.getCurrentStatus().equals(ALIVE);

            if (!isAlive && aliveCount == 3) {
                next.setCurrentStatus(ALIVE);
            } else if (isAlive && (aliveCount == 2 || aliveCount == 3)) {
                next.setCurrentStatus(ALIVE);
            }
            return next;
        }
    }
}
