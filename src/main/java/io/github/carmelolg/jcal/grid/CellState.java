package io.github.carmelolg.jcal.grid;

/**
 * Represents the state of a single cell in the Cellular Automata.
 *
 * <p>A status has two parts:
 * <ul>
 *   <li>{@link #key} - a human-readable identifier (e.g. {@code "dead"}, {@code "alive"})</li>
 *   <li>{@link #value} - the payload, which can be any {@link Object} (a string, integer,
 *       or a complex domain-specific value for Complex CA)</li>
 * </ul>
 *
 * <p><b>Extending for complex automata:</b>
 * <pre>{@code
 * // Simple two-state example
 * CellState dead  = new CellState("dead",  "0");
 * CellState alive = new CellState("alive", "1");
 *
 * // Rich state example (store arbitrary data in value)
 * CellState hotCell = new CellState("hot", Map.of("temp", 1000, "pressure", 3));
 * }</pre>
 *
 * <p>Two {@code CellState} instances are considered equal when both {@code key} and
 * {@code value} are equal ({@link #equals(Object)}).
 *
 * @author Carmelo La Gamba
 * @see Cell
 */
public class CellState {

	private final String key;
	private final Object value;

	public CellState(String name, Object value) {
		this.key = name;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof CellState status)) return false;
		return this.key.equals(status.key) && this.value.equals(status.value);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(key, value);
	}

	@Override
	public String toString() {
		return value.toString() + " ";
	}

	/**
	 * Returns a shallow copy of this {@code CellState}.
	 *
	 * <p>The {@code key} field is a {@link String} and is therefore safe to share.
	 * The {@code value} field is copied <em>by reference</em>: if {@code value} is a
	 * mutable object (e.g. a {@code Map} or custom domain object), both the original
	 * and the clone will refer to the same instance.
	 *
	 * <p><b>Contract:</b> for correct behaviour in a Cellular Automata, {@code value}
	 * must be <em>effectively immutable</em> (e.g. a {@link String}, a boxed primitive,
	 * or an unmodifiable collection).  Passing a mutable {@code value} and then
	 * modifying it after construction leads to undefined CA behaviour.
	 *
	 * @return a new {@code CellState} with the same {@code key} and {@code value}
	 */
	@Override
	public CellState clone() throws CloneNotSupportedException {
		return new CellState(key, value);
	}

}
