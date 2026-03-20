package io.github.carmelolg.jcal.model;

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
 * DefaultStatus dead  = new DefaultStatus("dead",  "0");
 * DefaultStatus alive = new DefaultStatus("alive", "1");
 *
 * // Rich state example (store arbitrary data in value)
 * DefaultStatus hotCell = new DefaultStatus("hot", Map.of("temp", 1000, "pressure", 3));
 * }</pre>
 *
 * <p>Two {@code DefaultStatus} instances are considered equal when both {@code key} and
 * {@code value} are equal ({@link #equals(Object)}).
 *
 * @author Carmelo La Gamba
 * @see DefaultCell
 */
public class DefaultStatus {

	String key;
	Object value;

	public DefaultStatus(String name, Object value) {
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
		DefaultStatus status = (DefaultStatus) object;
		return this.key.equals(status.key) && this.value.equals(status.value);
	}

	@Override
	public String toString() {
		return value.toString() + " ";
	}

	@Override
	public DefaultStatus clone() throws CloneNotSupportedException {
		return new DefaultStatus(key, value);
	}

}
