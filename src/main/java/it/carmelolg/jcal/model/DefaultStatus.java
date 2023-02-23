package it.carmelolg.jcal.model;

/**
 * @author Carmelo La Gamba
 * 
 *         © 2023 is licensed under CC BY-NC-SA 4.0
 */
public class DefaultStatus {

	String key;
	Object value;

	public DefaultStatus(String name, Object value){
		this.key = name;
		this.value = value;
	}
	
	
	public Object getValue() {
		return value;
	}
	
	

}
