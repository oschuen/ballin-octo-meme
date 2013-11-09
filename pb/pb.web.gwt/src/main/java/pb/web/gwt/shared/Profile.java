/**
 * Copyright (C) 2013 Oliver Schünemann
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, 
 * Boston, MA 02110, USA 
 * 
 * @since 19.10.2013
 * @version 1.0
 * @author oliver
 */
package pb.web.gwt.shared;

import java.io.Serializable;

/**
 * The profile of the requester
 * 
 * @author oliver
 */
public class Profile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9210141342538871598L;
	public static final String male = "MALE";
	public static final String female = "FEMALE";

	protected String name = "";
	protected int age = 18;
	protected String description = "";
	protected String avatarUrl = "";
	protected String gender = male;
	protected int id = -1;

	public Profile() {
		super();
	}

	/**
	 * Constructor setting all fields
	 * 
	 * @param id
	 *            id of requesters
	 * @param name
	 *            name of the requester
	 * @param description
	 *            the requesters short description
	 * @param avatarUrl
	 *            the url of the requesters avatar photo
	 * @param age
	 *            the age of the requester
	 * @param gender
	 *            the gender of the requester
	 */
	public Profile(final int id, final String name, final String description,
			final String avatarUrl, final int age, final String gender) {
		super();
		this.id = id;
		this.name = name;
		this.age = age;
		this.description = description;
		this.avatarUrl = avatarUrl;
		this.gender = gender;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge(final int age) {
		this.age = age;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return the avatarUrl
	 */
	public String getAvatarUrl() {
		return avatarUrl;
	}

	/**
	 * @param avatarUrl
	 *            the avatarUrl to set
	 */
	public void setAvatarUrl(final String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender
	 *            the gender to set
	 */
	public void setGender(final String gender) {
		this.gender = gender;
	}

	/**
	 * @return the male
	 */
	public String getMale() {
		return male;
	}

	/**
	 * @return the female
	 */
	public String getFemale() {
		return female;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(final int id) {
		this.id = id;
	}

}
