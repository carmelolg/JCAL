package io.github.carmelolg.jcal.model;

/**
 * @author Carmelo La Gamba
 * 
 *         © 2023 is licensed under CC BY-NC-SA 4.0
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
