package io.github.carmelolg.jcal.model;

/**
 * Enumeration of the built-in neighborhood strategies.
 *
 * <p>A neighborhood defines which cells are considered "neighbours" of a given cell when
 * computing the transition function.
 *
 * <ul>
 *   <li>{@link #MOORE} - 8 neighbours (orthogonal + diagonal); the most common choice for
 *       Game-of-Life-style automata.</li>
 *   <li>{@link #VON_NEUMANN} - 4 neighbours (orthogonal only); commonly used for
 *       diffusion-based or flow models.</li>
 * </ul>
 *
 * <p>Pass the desired value to
 * {@link io.github.carmelolg.jcal.configuration.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder#setNeighborhoodType(NeighborhoodType)}
 * when building a configuration, or provide a fully custom
 * {@link io.github.carmelolg.jcal.core.DefaultNeighborhood} implementation instead.
 *
 * @author Carmelo La Gamba
 * @see io.github.carmelolg.jcal.core.MooreNeighborhood
 * @see io.github.carmelolg.jcal.core.VonNeumannNeighborhood
 */
public enum NeighborhoodType {

	MOORE, VON_NEUMANN
}
