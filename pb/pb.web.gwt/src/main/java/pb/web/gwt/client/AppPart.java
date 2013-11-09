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
 * @since 26.10.2013
 * @version 1.0
 * @author oliver
 */
package pb.web.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * All Views of this application have the same structure. On top of the page is
 * the menu list, containing Widgets that are part of this class. Underneath the
 * menu is the title of the current page followed by the content
 * 
 * @author oliver
 */
public abstract class AppPart {

	protected static final Messages messages = GWT.create(Messages.class);

	protected ProfileServiceAsync profileService = GWT.create(ProfileService.class);

	/**
	 * Link to the Profile Page
	 */
	public static final Hyperlink profileLink = new Hyperlink(messages.profile_hyperlink(),
			"profile");

	/**
	 * Link to the candidates Page
	 */
	public static final Hyperlink othersLink = new Hyperlink(messages.other_hyperlink(), "others");

	/**
	 * Link to the Favorites Page
	 */
	public static final Hyperlink favoritesLink = new Hyperlink(messages.favorites_hyperlink(),
			"favorites");

	/**
	 * Placeholder when Profile Link is inactive
	 */
	public static final Label inactiveProfileLink = new Label(messages.profile_hyperlink());

	/**
	 * Placeholder when Candidates Link is inactive
	 */
	public static final Label inactiveOthersLink = new Label(messages.other_hyperlink());

	/**
	 * Placeholder when Favorites Link is inactive
	 */
	public static final Label inactiveFavoritesLink = new Label(messages.favorites_hyperlink());

	/**
	 * Set Style to all static widgets
	 */
	static {
		profileLink.setStyleName("formPanel");
		othersLink.setStyleName("formPanel");
		favoritesLink.setStyleName("formPanel");
		inactiveProfileLink.setStyleName("formPanel");
		inactiveOthersLink.setStyleName("formPanel");
		inactiveFavoritesLink.setStyleName("formPanel");
	}

	/**
	 * Must be overwritten by Application Page to return the menu belonging to
	 * it
	 * 
	 * @return List of Menu Widgets
	 */
	public abstract Widget[] getLinks();

	/**
	 * returns the headline for the page
	 * 
	 * @return Headline
	 */
	public abstract String getHeader();

	/**
	 * Returns the dynamic content of the page
	 * 
	 * @return the content
	 */
	public abstract Composite getApp();

}
