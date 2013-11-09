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
 * @since 22.10.2013
 * @version 1.0
 * @author oliver
 */
package pb.web.gwt.shared;

/**
 * String constants to identify the fields within the profile edit form
 * 
 * @author oliver
 */
public final class ProfileFormFields {

	private ProfileFormFields() {
		// Don't create an instance of this
	}

	public static final String AvatarField = "Avatar";
	public static final String NameField = "Name";
	public static final String AgeField = "Age";
	public static final String descriptionField = "Description";
	public static final String genderGroupField = "GenderGroup";
	public static final String maleValue = "Male";
	public static final String femaleValue = "Female";
}
