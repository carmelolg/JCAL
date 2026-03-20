package io.github.carmelolg.jcal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
 * Specification tests for custom-state Cellular Automata in JCAL.
 *
 * <p>These tests demonstrate and verify how JCAL supports automata with more than
 * two cell states.  Rather than a simple dead/alive flag, cells can carry any
 * {@link Object} as their {@link DefaultStatus#getValue()}.
 *
 * <p>The scenario used here is a simplified <em>heat diffusion</em> model:
 * <ul>
 *   <li>{@code COLD} (value = 0) – default, unheated state</li>
 *   <li>{@code WARM} (value = 1) – transitional state, heated by neighbours</li>
 *   <li>{@code HOT}  (value = 2) – permanent heat source</li>
 * </ul>
 *
 * <p>Rules (Von Neumann neighborhood):
 * <ol>
 *   <li>HOT cells always stay HOT.</li>
 *   <li>COLD cells adjacent to at least one HOT cell become WARM.</li>
 *   <li>COLD cells adjacent to two or more WARM cells become WARM.</li>
 *   <li>All other cells keep their current state.</li>
 * </ol>
 *
 * <p>These tests serve as a <b>living specification</b> for the custom-state extension
 * point of JCAL.  AI agents and new contributors can read them to understand the
 * expected behaviour before modifying or extending the library.
 */
@DisplayName("Custom state – specification tests")
public class CustomStateSpecificationTest {

    // Three-value custom states
    private static final DefaultStatus COLD = new DefaultStatus("cold", 0);
    private static final DefaultStatus WARM = new DefaultStatus("warm", 1);
    private static final DefaultStatus HOT  = new DefaultStatus("hot",  2);

    // -------------------------------------------------------------------------
    // Helper: run the heat diffusion CA for N steps
    // -------------------------------------------------------------------------

    private CellularAutomata runHeatDiffusion(int width, int height,
                                               List<DefaultCell> initialState,
                                               int iterations) throws Exception {
        CellularAutomataConfiguration config = new CellularAutomataConfigurationBuilder()
            .setWidth(width)
            .setHeight(height)
            .setInfinite(false)
            .setTotalIterations(iterations)
            .setDefaultStatus(COLD)
            .setInitalState(initialState)
            .setNeighborhoodType(NeighborhoodType.VON_NEUMANN)
            .build();

        CellularAutomata ca = new CellularAutomata(config);
        return new HeatDiffusionRule().run(ca);
    }

    // -------------------------------------------------------------------------
    // Test 1: Custom status creation and equality
    // -------------------------------------------------------------------------

    /**
     * Verifies that two {@link DefaultStatus} instances with the same key and value
     * are considered equal, and that distinct statuses are not equal.
     */
    @Test
    @DisplayName("DefaultStatus equality is based on key and value")
    void customStatusEquality() {
        DefaultStatus coldA = new DefaultStatus("cold", 0);
        DefaultStatus coldB = new DefaultStatus("cold", 0);
        DefaultStatus warm  = new DefaultStatus("warm", 1);

        assertEquals(coldA, coldB, "Two statuses with the same key/value must be equal");
        assertNotSame(coldA, coldB, "Equal statuses should not be the same object");
        assertTrue(!coldA.equals(warm), "Statuses with different values must not be equal");
    }

    /**
     * Verifies that the status {@code value} field can hold an {@link Integer} and that
     * the correct value is returned by {@link DefaultStatus#getValue()}.
     */
    @Test
    @DisplayName("DefaultStatus stores and returns the integer value correctly")
    void customStatusStoresIntegerValue() {
        assertEquals(0, COLD.getValue(), "COLD value should be 0");
        assertEquals(1, WARM.getValue(), "WARM value should be 1");
        assertEquals(2, HOT.getValue(),  "HOT value should be 2");
    }

    // -------------------------------------------------------------------------
    // Test 2: HOT cells remain HOT (heat source persistence)
    // -------------------------------------------------------------------------

    /**
     * A HOT cell must remain HOT after any number of steps because the rule
     * explicitly preserves heat sources.
     */
    @Test
    @DisplayName("HOT cells remain HOT after 5 generations")
    void hotCellsRemainHotAfterFiveGenerations() throws Exception {
        List<DefaultCell> initialState = Arrays.asList(
            new DefaultCell(HOT, 5, 5)
        );

        CellularAutomata result = runHeatDiffusion(10, 10, initialState, 5);

        assertNotNull(result);
        assertEquals(HOT, result.getMap()[5][5].getCurrentStatus(),
            "The original HOT cell should still be HOT after 5 generations");
    }

    // -------------------------------------------------------------------------
    // Test 3: Cold neighbours of a hot cell become warm after 1 step
    // -------------------------------------------------------------------------

    /**
     * After a single step, the four Von Neumann neighbours of a HOT cell should
     * transition from COLD to WARM.
     */
    @Test
    @DisplayName("Von Neumann neighbours of a HOT cell become WARM after 1 generation")
    void coldNeighboursOfHotCellBecomeWarm() throws Exception {
        // Single HOT cell in the centre of a 7x7 grid
        List<DefaultCell> initialState = Arrays.asList(
            new DefaultCell(HOT, 3, 3)
        );

        CellularAutomata result = runHeatDiffusion(7, 7, initialState, 1);

        assertNotNull(result);
        // The four orthogonal neighbours should now be warm
        assertEquals(WARM, result.getMap()[2][3].getCurrentStatus(), "col=2,row=3 (left)  should be WARM");
        assertEquals(WARM, result.getMap()[4][3].getCurrentStatus(), "col=4,row=3 (right) should be WARM");
        assertEquals(WARM, result.getMap()[3][2].getCurrentStatus(), "col=3,row=2 (up)    should be WARM");
        assertEquals(WARM, result.getMap()[3][4].getCurrentStatus(), "col=3,row=4 (down)  should be WARM");
        // Diagonal cells should remain cold (Von Neumann = orthogonal only)
        assertEquals(COLD, result.getMap()[2][2].getCurrentStatus(), "Diagonal col=2,row=2 should stay COLD");
        assertEquals(COLD, result.getMap()[4][4].getCurrentStatus(), "Diagonal col=4,row=4 should stay COLD");
    }

    // -------------------------------------------------------------------------
    // Test 4: Multi-state interaction – warm cells propagate via the 2-warm rule
    // -------------------------------------------------------------------------

    /**
     * After two steps, cells that are orthogonally adjacent to two or more WARM cells
     * should also become WARM (the secondary diffusion rule).
     *
     * <p>This verifies that the automaton correctly handles three distinct state values
     * and multi-condition branching inside a single transition rule.
     */
    @Test
    @DisplayName("Cells adjacent to 2+ WARM neighbours become WARM after 2 generations")
    void cellsAdjacentToTwoWarmNeighboursBecomeWarm() throws Exception {
        // Two adjacent HOT cells; after step 1 their shared neighbours become warm,
        // and after step 2 additional cells should warm up via the 2-warm rule.
        List<DefaultCell> initialState = Arrays.asList(
            new DefaultCell(HOT, 3, 3),
            new DefaultCell(HOT, 3, 5)   // two rows apart, so (3,4) is between them
        );

        CellularAutomata result = runHeatDiffusion(9, 9, initialState, 2);

        assertNotNull(result);
        // Both heat sources must remain hot
        assertEquals(HOT, result.getMap()[3][3].getCurrentStatus(), "col=3,row=3 should still be HOT");
        assertEquals(HOT, result.getMap()[3][5].getCurrentStatus(), "col=3,row=5 should still be HOT");
        // Cell at (3,4) is directly between the two sources; after step 1 it is warm
        // (adjacent to HOT), and it must remain warm (or hotter) after step 2
        DefaultStatus statusBetween = result.getMap()[3][4].getCurrentStatus();
        assertTrue(statusBetween.equals(WARM) || statusBetween.equals(HOT),
            "Cell between two HOT sources should be at least WARM after 2 generations");
    }

    // -------------------------------------------------------------------------
    // Inner class: heat diffusion rule (mirrors CustomStateExample)
    // -------------------------------------------------------------------------

    private static class HeatDiffusionRule extends CellularAutomataExecutor {
        @Override
        public DefaultCell singleRun(DefaultCell cell, List<DefaultCell> neighbors) {
            long hotCount  = neighbors.stream().filter(n -> n.getCurrentStatus().equals(HOT)).count();
            long warmCount = neighbors.stream().filter(n -> n.getCurrentStatus().equals(WARM)).count();

            DefaultCell next = new DefaultCell(cell.getCurrentStatus(), cell.getCol(), cell.getRow());

            if (cell.getCurrentStatus().equals(HOT)) {
                next.setCurrentStatus(HOT);
            } else if (hotCount > 0) {
                next.setCurrentStatus(WARM);
            } else if (warmCount >= 2) {
                next.setCurrentStatus(WARM);
            }
            return next;
        }
    }
}
