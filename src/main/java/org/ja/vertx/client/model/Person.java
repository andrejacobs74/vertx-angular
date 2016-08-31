package org.ja.vertx.client.model;

/**
 * 
 * For using of pojos as json objects use also the id field, because problems with saving and loading of the mongo value
 * 
 * @author andre
 *
 */
public class Person {

	private String name;
	private String lastName;
	private String _id;
	
	public String getName() {
		return name;
	}
	public void setName(String firstName) {
		this.name = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	
	@Override
	public String toString () {
		return "id: " + _id + " name: " + name + " Lastname: " + lastName; 
	}
	
}
