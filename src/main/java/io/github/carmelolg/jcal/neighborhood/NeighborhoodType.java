package io.github.carmelolg.jcal.neighborhood;

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
 * {@link io.github.carmelolg.jcal.core.CellularAutomataConfiguration.CellularAutomataConfigurationBuilder#setNeighborhoodType(NeighborhoodType)}
 * when building a configuration, or provide a fully custom
 * {@link io.github.carmelolg.jcal.neighborhood.Neighborhood} implementation instead.
 *
 * @author Carmelo La Gamba
 * @see io.github.carmelolg.jcal.neighborhood.MooreNeighborhood
 * @see io.github.carmelolg.jcal.neighborhood.VonNeumannNeighborhood
 */
public enum NeighborhoodType {

	MOORE, VON_NEUMANN
}
