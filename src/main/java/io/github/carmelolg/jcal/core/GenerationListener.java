package io.github.carmelolg.jcal.core;

import io.github.carmelolg.jcal.grid.GridSnapshot;

/**
 * Callback interface invoked by {@link CellularAutomataRule} after each completed
 * generation.
 *
 * <p>Register one or more listeners via
 * {@link CellularAutomataRule#addGenerationListener(GenerationListener)} to receive
 * a {@link GridSnapshot} of the automaton's state after every iteration.  This is the
 * primary integration point for rendering, recording, or analysis systems.
 *
 * <p><b>Example — print state to console after every step:</b>
 * <pre>{@code
 * CellularAutomataRule rule = new GameOfLifeRule();
 * rule.addGenerationListener((gen, snap) ->
 *     System.out.printf("Generation %d: %d cells%n",
 *         gen, snap.getCellStates().size()));
 * rule.run(ca);
 * }</pre>
 *
 * <p>This interface is annotated with {@link FunctionalInterface} so it can be
 * supplied as a lambda or method reference.
 *
 * @author Carmelo La Gamba
 * @see CellularAutomataRule
 * @see GridSnapshot
 */
@FunctionalInterface
public interface GenerationListener {

    /**
     * Called once per completed generation.
     *
     * @param generation the index of the just-completed generation (1-based: the first
     *                   call has {@code generation == 1})
     * @param snapshot   an immutable snapshot of the grid after the transition was applied
     */
    void onGeneration(int generation, GridSnapshot snapshot);
}
