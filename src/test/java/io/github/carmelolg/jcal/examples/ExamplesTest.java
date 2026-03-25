package io.github.carmelolg.jcal.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.carmelolg.jcal.model.DefaultCell;

/**
 * Tests for the example classes bundled with JCAL.
 *
 * <p>These tests exercise {@link GameOfLifeExample} and {@link CustomStateExample}
 * end-to-end (via their {@code main()} method) as well as at the unit level by
 * directly invoking the inner executor rule classes with hand-crafted cell states.
 * This ensures that every branch inside the transition functions is reachable.
 */
class ExamplesTest {

    // =========================================================================
    // GameOfLifeExample
    // =========================================================================

    /** Smoke-test: the full example runs without throwing any exception. */
    @Test
    void gameOfLifeExampleMain() throws Exception {
        GameOfLifeExample.main(new String[]{});
    }

    /** Dead cell with exactly 3 alive neighbours → becomes alive (birth rule). */
    @Test
    void gameOfLifeRuleDeadCellWithThreeAliveNeighborsBecomeAlive() {
        GameOfLifeExample.GameOfLifeRule rule = new GameOfLifeExample.GameOfLifeRule();

        DefaultCell deadCell = new DefaultCell(GameOfLifeExample.DEAD, 3, 3);
        List<DefaultCell> neighbors = Arrays.asList(
            new DefaultCell(GameOfLifeExample.ALIVE, 2, 3),
            new DefaultCell(GameOfLifeExample.ALIVE, 4, 3),
            new DefaultCell(GameOfLifeExample.ALIVE, 3, 2),
            new DefaultCell(GameOfLifeExample.DEAD,  3, 4)
        );

        DefaultCell result = rule.singleRun(deadCell, neighbors);
        assertNotNull(result);
        assertEquals(GameOfLifeExample.ALIVE, result.getCurrentStatus(),
            "Dead cell with 3 alive neighbours should be born");
    }

    /** Alive cell with exactly 2 alive neighbours → survives. */
    @Test
    void gameOfLifeRuleAliveCellWithTwoNeighborsSurvives() {
        GameOfLifeExample.GameOfLifeRule rule = new GameOfLifeExample.GameOfLifeRule();

        DefaultCell aliveCell = new DefaultCell(GameOfLifeExample.ALIVE, 3, 3);
        List<DefaultCell> neighbors = Arrays.asList(
            new DefaultCell(GameOfLifeExample.ALIVE, 2, 3),
            new DefaultCell(GameOfLifeExample.ALIVE, 4, 3),
            new DefaultCell(GameOfLifeExample.DEAD,  3, 2),
            new DefaultCell(GameOfLifeExample.DEAD,  3, 4)
        );

        DefaultCell result = rule.singleRun(aliveCell, neighbors);
        assertNotNull(result);
        assertEquals(GameOfLifeExample.ALIVE, result.getCurrentStatus(),
            "Alive cell with 2 alive neighbours should survive");
    }

    /** Alive cell with exactly 3 alive neighbours → survives. */
    @Test
    void gameOfLifeRuleAliveCellWithThreeNeighborsSurvives() {
        GameOfLifeExample.GameOfLifeRule rule = new GameOfLifeExample.GameOfLifeRule();

        DefaultCell aliveCell = new DefaultCell(GameOfLifeExample.ALIVE, 3, 3);
        List<DefaultCell> neighbors = Arrays.asList(
            new DefaultCell(GameOfLifeExample.ALIVE, 2, 3),
            new DefaultCell(GameOfLifeExample.ALIVE, 4, 3),
            new DefaultCell(GameOfLifeExample.ALIVE, 3, 2),
            new DefaultCell(GameOfLifeExample.DEAD,  3, 4)
        );

        DefaultCell result = rule.singleRun(aliveCell, neighbors);
        assertNotNull(result);
        assertEquals(GameOfLifeExample.ALIVE, result.getCurrentStatus(),
            "Alive cell with 3 alive neighbours should survive");
    }

    /** Alive cell with only 1 alive neighbour → dies (underpopulation). */
    @Test
    void gameOfLifeRuleAliveCellWithOneNeighborDies() {
        GameOfLifeExample.GameOfLifeRule rule = new GameOfLifeExample.GameOfLifeRule();

        DefaultCell aliveCell = new DefaultCell(GameOfLifeExample.ALIVE, 3, 3);
        List<DefaultCell> neighbors = Arrays.asList(
            new DefaultCell(GameOfLifeExample.ALIVE, 2, 3),
            new DefaultCell(GameOfLifeExample.DEAD,  4, 3),
            new DefaultCell(GameOfLifeExample.DEAD,  3, 2)
        );

        DefaultCell result = rule.singleRun(aliveCell, neighbors);
        assertNotNull(result);
        assertEquals(GameOfLifeExample.DEAD, result.getCurrentStatus(),
            "Alive cell with 1 alive neighbour should die (underpopulation)");
    }

    /** Alive cell with 4 alive neighbours → dies (overcrowding). */
    @Test
    void gameOfLifeRuleAliveCellWithFourNeighborsDies() {
        GameOfLifeExample.GameOfLifeRule rule = new GameOfLifeExample.GameOfLifeRule();

        DefaultCell aliveCell = new DefaultCell(GameOfLifeExample.ALIVE, 3, 3);
        List<DefaultCell> neighbors = Arrays.asList(
            new DefaultCell(GameOfLifeExample.ALIVE, 2, 3),
            new DefaultCell(GameOfLifeExample.ALIVE, 4, 3),
            new DefaultCell(GameOfLifeExample.ALIVE, 3, 2),
            new DefaultCell(GameOfLifeExample.ALIVE, 3, 4)
        );

        DefaultCell result = rule.singleRun(aliveCell, neighbors);
        assertNotNull(result);
        assertEquals(GameOfLifeExample.DEAD, result.getCurrentStatus(),
            "Alive cell with 4 alive neighbours should die (overcrowding)");
    }

    /** Dead cell with no alive neighbours → stays dead. */
    @Test
    void gameOfLifeRuleDeadCellWithNoNeighborsStaysDead() {
        GameOfLifeExample.GameOfLifeRule rule = new GameOfLifeExample.GameOfLifeRule();

        DefaultCell deadCell = new DefaultCell(GameOfLifeExample.DEAD, 3, 3);
        List<DefaultCell> neighbors = Collections.singletonList(
            new DefaultCell(GameOfLifeExample.DEAD, 2, 3)
        );

        DefaultCell result = rule.singleRun(deadCell, neighbors);
        assertNotNull(result);
        assertEquals(GameOfLifeExample.DEAD, result.getCurrentStatus(),
            "Dead cell with no alive neighbours should stay dead");
    }

    // =========================================================================
    // CustomStateExample
    // =========================================================================

    /** Smoke-test: the full custom-state example runs without throwing any exception. */
    @Test
    void customStateExampleMain() throws Exception {
        CustomStateExample.main(new String[]{});
    }

    /** HOT cell stays HOT regardless of its neighbours. */
    @Test
    void heatDiffusionRuleHotCellStaysHot() {
        CustomStateExample.HeatDiffusionRule rule = new CustomStateExample.HeatDiffusionRule();

        DefaultCell hotCell = new DefaultCell(CustomStateExample.HOT, 5, 5);
        List<DefaultCell> neighbors = Arrays.asList(
            new DefaultCell(CustomStateExample.COLD, 4, 5),
            new DefaultCell(CustomStateExample.COLD, 6, 5)
        );

        DefaultCell result = rule.singleRun(hotCell, neighbors);
        assertNotNull(result);
        assertEquals(CustomStateExample.HOT, result.getCurrentStatus(),
            "HOT cell should remain HOT");
    }

    /** COLD cell adjacent to a HOT cell → becomes WARM. */
    @Test
    void heatDiffusionRuleColdCellAdjacentToHotBecomesWarm() {
        CustomStateExample.HeatDiffusionRule rule = new CustomStateExample.HeatDiffusionRule();

        DefaultCell coldCell = new DefaultCell(CustomStateExample.COLD, 4, 5);
        List<DefaultCell> neighbors = Arrays.asList(
            new DefaultCell(CustomStateExample.HOT,  5, 5),
            new DefaultCell(CustomStateExample.COLD, 3, 5)
        );

        DefaultCell result = rule.singleRun(coldCell, neighbors);
        assertNotNull(result);
        assertEquals(CustomStateExample.WARM, result.getCurrentStatus(),
            "COLD cell next to a HOT cell should become WARM");
    }

    /** COLD cell with 2+ WARM neighbours → becomes WARM. */
    @Test
    void heatDiffusionRuleColdCellWithTwoWarmNeighborsBecomesWarm() {
        CustomStateExample.HeatDiffusionRule rule = new CustomStateExample.HeatDiffusionRule();

        DefaultCell coldCell = new DefaultCell(CustomStateExample.COLD, 5, 5);
        List<DefaultCell> neighbors = Arrays.asList(
            new DefaultCell(CustomStateExample.WARM, 4, 5),
            new DefaultCell(CustomStateExample.WARM, 6, 5),
            new DefaultCell(CustomStateExample.COLD, 5, 4)
        );

        DefaultCell result = rule.singleRun(coldCell, neighbors);
        assertNotNull(result);
        assertEquals(CustomStateExample.WARM, result.getCurrentStatus(),
            "COLD cell surrounded by 2+ WARM cells should become WARM");
    }

    /** COLD cell with only 1 WARM neighbour (and no HOT) → stays COLD. */
    @Test
    void heatDiffusionRuleColdCellWithOneWarmNeighborStaysCold() {
        CustomStateExample.HeatDiffusionRule rule = new CustomStateExample.HeatDiffusionRule();

        DefaultCell coldCell = new DefaultCell(CustomStateExample.COLD, 5, 5);
        List<DefaultCell> neighbors = Arrays.asList(
            new DefaultCell(CustomStateExample.WARM, 4, 5),
            new DefaultCell(CustomStateExample.COLD, 6, 5)
        );

        DefaultCell result = rule.singleRun(coldCell, neighbors);
        assertNotNull(result);
        assertEquals(CustomStateExample.COLD, result.getCurrentStatus(),
            "COLD cell with only 1 WARM neighbour and no HOT should stay COLD");
    }

    /** WARM cell with no HOT neighbours and at least one WARM neighbour → stays WARM. */
    @Test
    void heatDiffusionRuleWarmCellWithWarmNeighborStaysWarm() {
        CustomStateExample.HeatDiffusionRule rule = new CustomStateExample.HeatDiffusionRule();

        DefaultCell warmCell = new DefaultCell(CustomStateExample.WARM, 5, 5);
        List<DefaultCell> neighbors = Arrays.asList(
            new DefaultCell(CustomStateExample.WARM, 4, 5),
            new DefaultCell(CustomStateExample.COLD, 6, 5)
        );

        DefaultCell result = rule.singleRun(warmCell, neighbors);
        assertNotNull(result);
        assertEquals(CustomStateExample.WARM, result.getCurrentStatus(),
            "WARM cell with at least one WARM neighbour should stay WARM");
    }

    /** WARM cell with no HOT neighbours and no WARM neighbours → becomes COLD. */
    @Test
    void heatDiffusionRuleWarmCellWithoutWarmNeighborsBecomesCold() {
        CustomStateExample.HeatDiffusionRule rule = new CustomStateExample.HeatDiffusionRule();

        DefaultCell warmCell = new DefaultCell(CustomStateExample.WARM, 5, 5);
        List<DefaultCell> neighbors = Arrays.asList(
            new DefaultCell(CustomStateExample.COLD, 4, 5),
            new DefaultCell(CustomStateExample.COLD, 6, 5)
        );

        DefaultCell result = rule.singleRun(warmCell, neighbors);
        assertNotNull(result);
        assertEquals(CustomStateExample.COLD, result.getCurrentStatus(),
            "WARM cell with no HOT or WARM neighbours should become COLD");
    }

    /** HOT cell with HOT neighbours → stays HOT. */
    @Test
    void heatDiffusionRuleHotCellWithHotNeighborsStaysHot() {
        CustomStateExample.HeatDiffusionRule rule = new CustomStateExample.HeatDiffusionRule();

        DefaultCell hotCell = new DefaultCell(CustomStateExample.HOT, 5, 5);
        List<DefaultCell> neighbors = Arrays.asList(
            new DefaultCell(CustomStateExample.HOT, 4, 5),
            new DefaultCell(CustomStateExample.COLD, 6, 5)
        );

        DefaultCell result = rule.singleRun(hotCell, neighbors);
        assertNotNull(result);
        assertEquals(CustomStateExample.HOT, result.getCurrentStatus(),
            "HOT cell should remain HOT even when surrounded by other HOT cells");
    }

    /** HOT cell with WARM neighbours → stays HOT. */
    @Test
    void heatDiffusionRuleHotCellWithWarmNeighborsStaysHot() {
        CustomStateExample.HeatDiffusionRule rule = new CustomStateExample.HeatDiffusionRule();

        DefaultCell hotCell = new DefaultCell(CustomStateExample.HOT, 5, 5);
        List<DefaultCell> neighbors = Arrays.asList(
            new DefaultCell(CustomStateExample.WARM, 4, 5),
            new DefaultCell(CustomStateExample.WARM, 6, 5)
        );

        DefaultCell result = rule.singleRun(hotCell, neighbors);
        assertNotNull(result);
        assertEquals(CustomStateExample.HOT, result.getCurrentStatus(),
            "HOT cell should remain HOT regardless of WARM neighbours");
    }

    /** WARM cell with HOT neighbour → becomes HOT. */
    @Test
    void heatDiffusionRuleWarmCellWithHotNeighborBecomesHot() {
        CustomStateExample.HeatDiffusionRule rule = new CustomStateExample.HeatDiffusionRule();

        DefaultCell warmCell = new DefaultCell(CustomStateExample.WARM, 5, 5);
        List<DefaultCell> neighbors = Arrays.asList(
            new DefaultCell(CustomStateExample.HOT, 4, 5),
            new DefaultCell(CustomStateExample.COLD, 6, 5)
        );

        DefaultCell result = rule.singleRun(warmCell, neighbors);
        assertNotNull(result);
        assertEquals(CustomStateExample.HOT, result.getCurrentStatus(),
            "WARM cell adjacent to a HOT cell should become HOT");
    }

    /** COLD cell with one HOT and multiple COLD neighbours → becomes WARM. */
    @Test
    void heatDiffusionRuleColdCellWithOneHotNeighborBecomesWarm() {
        CustomStateExample.HeatDiffusionRule rule = new CustomStateExample.HeatDiffusionRule();

        DefaultCell coldCell = new DefaultCell(CustomStateExample.COLD, 5, 5);
        List<DefaultCell> neighbors = Arrays.asList(
            new DefaultCell(CustomStateExample.HOT, 4, 5),
            new DefaultCell(CustomStateExample.COLD, 6, 5),
            new DefaultCell(CustomStateExample.COLD, 5, 4)
        );

        DefaultCell result = rule.singleRun(coldCell, neighbors);
        assertNotNull(result);
        assertEquals(CustomStateExample.WARM, result.getCurrentStatus(),
            "COLD cell with one HOT neighbour should become WARM");
    }

    /** COLD cell with exactly one WARM neighbour and no HOT → stays COLD. */
    @Test
    void heatDiffusionRuleColdCellWithOneWarmAndNoHotStaysCold() {
        CustomStateExample.HeatDiffusionRule rule = new CustomStateExample.HeatDiffusionRule();

        DefaultCell coldCell = new DefaultCell(CustomStateExample.COLD, 5, 5);
        List<DefaultCell> neighbors = Arrays.asList(
            new DefaultCell(CustomStateExample.WARM, 4, 5),
            new DefaultCell(CustomStateExample.COLD, 6, 5),
            new DefaultCell(CustomStateExample.COLD, 5, 4)
        );

        DefaultCell result = rule.singleRun(coldCell, neighbors);
        assertNotNull(result);
        assertEquals(CustomStateExample.COLD, result.getCurrentStatus(),
            "COLD cell with only one WARM neighbour should stay COLD");
    }

    /** COLD cell with three WARM neighbours → becomes WARM. */
    @Test
    void heatDiffusionRuleColdCellWithThreeWarmNeighborsBecomesWarm() {
        CustomStateExample.HeatDiffusionRule rule = new CustomStateExample.HeatDiffusionRule();

        DefaultCell coldCell = new DefaultCell(CustomStateExample.COLD, 5, 5);
        List<DefaultCell> neighbors = Arrays.asList(
            new DefaultCell(CustomStateExample.WARM, 4, 5),
            new DefaultCell(CustomStateExample.WARM, 6, 5),
            new DefaultCell(CustomStateExample.WARM, 5, 4)
        );

        DefaultCell result = rule.singleRun(coldCell, neighbors);
        assertNotNull(result);
        assertEquals(CustomStateExample.WARM, result.getCurrentStatus(),
            "COLD cell surrounded by 3 WARM cells should become WARM");
    }

    /** COLD cell with no neighbours → stays COLD. */
    @Test
    void heatDiffusionRuleColdCellWithNoNeighborsStaysCold() {
        CustomStateExample.HeatDiffusionRule rule = new CustomStateExample.HeatDiffusionRule();

        DefaultCell coldCell = new DefaultCell(CustomStateExample.COLD, 5, 5);
        List<DefaultCell> neighbors = Collections.singletonList(
            new DefaultCell(CustomStateExample.COLD, 4, 5)
        );

        DefaultCell result = rule.singleRun(coldCell, neighbors);
        assertNotNull(result);
        assertEquals(CustomStateExample.COLD, result.getCurrentStatus(),
            "COLD cell with no heating neighbours should stay COLD");
    }
}
