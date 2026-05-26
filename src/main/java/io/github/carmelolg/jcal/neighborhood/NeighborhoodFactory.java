package io.github.carmelolg.jcal.neighborhood;

/**
 * Factory that resolves a concrete {@link Neighborhood} implementation from a
 * {@link NeighborhoodType} and a grid dimension count.
 *
 * <p>Keeps the {@code core} package decoupled from the concrete neighborhood classes.
 *
 * @author Carmelo La Gamba
 */
public final class NeighborhoodFactory {

    private NeighborhoodFactory() {}

    /**
     * Returns the appropriate {@link Neighborhood} for the given type and dimension count.
     *
     * @param type     the neighborhood strategy
     * @param dimCount the number of grid dimensions (2, 3, or 4)
     * @return a concrete {@link Neighborhood} instance
     * @throws IllegalArgumentException if {@code dimCount} is not supported
     */
    public static Neighborhood create(NeighborhoodType type, int dimCount) {
        return switch (dimCount) {
            case 2 -> type == NeighborhoodType.MOORE ? new MooreNeighborhood() : new VonNeumannNeighborhood();
            case 3 -> type == NeighborhoodType.MOORE ? new Moore3DNeighborhood() : new VonNeumann3DNeighborhood();
            case 4 -> type == NeighborhoodType.MOORE ? new Moore4DNeighborhood() : new VonNeumann4DNeighborhood();
            default -> throw new IllegalArgumentException("Unsupported dimension count: " + dimCount);
        };
    }
}
