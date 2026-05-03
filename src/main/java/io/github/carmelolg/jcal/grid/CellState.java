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

	String key;
	Object value;

	public CellState(String name, Object value) {
		this.key = name;
		this.value = value;
	}

	public Object getKey() {
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

	@Override
	public CellState clone() throws CloneNotSupportedException {
		return new CellState(key, value);
	}

}
