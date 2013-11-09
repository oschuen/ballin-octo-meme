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
 * @since 27.10.2013
 * @version 1.0
 * @author oliver
 */
package pb.web.gwt.client;

import pb.web.gwt.client.comp.CandidatesComposite;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * Configuration for the Candidates Application Page
 * 
 * @author oliver
 */
public class CandidatesAppPart extends AppPart {

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.web.gwt.client.AppPart#getLinks()
	 */
	@Override
	public Widget[] getLinks() {
		return new Widget[] { AppPart.profileLink, AppPart.inactiveOthersLink,
				AppPart.favoritesLink };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.web.gwt.client.AppPart#getApp()
	 */
	@Override
	public Composite getApp() {
		return new CandidatesComposite(profileService, messages);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.web.gwt.client.AppPart#getHeader()
	 */
	@Override
	public String getHeader() {
		return messages.headerOthers();
	}
}
