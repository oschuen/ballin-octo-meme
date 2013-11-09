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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry Point for the Application doing all the page flipping stuff.
 * 
 * @author oliver
 */
public class gwt implements EntryPoint, ValueChangeHandler<String> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	@Override
	public void onModuleLoad() {
		History.addValueChangeHandler(this);
		if (History.getToken().isEmpty()) {
			History.newItem(AppPart.profileLink.getTargetHistoryToken());
		} else {
			changePage(History.getToken());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.event.logical.shared.ValueChangeHandler#onValueChange(
	 * com.google.gwt.event.logical.shared.ValueChangeEvent)
	 */
	@Override
	public void onValueChange(final ValueChangeEvent<String> event) {
		changePage(History.getToken());
	}

	/**
	 * Flips between one Application Page and an other.
	 * 
	 * @param appPart
	 *            New Page to present to the user
	 */
	protected void setAppPart(final AppPart appPart) {
		final String header = appPart.getHeader();

		RootPanel.get("Application").clear();
		RootPanel.get("HeadLine").clear();
		RootPanel.get("Menu").clear();

		if ((header != null) && (header.length() > 0)) {
			final HTML html = new HTML("<h1>" + header + "</h1>");
			RootPanel.get("HeadLine").add(html);
		} else {
			final HTML html = new HTML("</br>");
			RootPanel.get("HeadLine").add(html);
		}

		final HorizontalPanel menuPanel = new HorizontalPanel();
		final Widget links[] = appPart.getLinks();
		if (links != null) {
			for (int i = 0; i < links.length; i++) {
				menuPanel.add(links[i]);
			}
			RootPanel.get("Menu").add(menuPanel);
		}

		RootPanel.get("Application").add(appPart.getApp());

	}

	/**
	 * Reads the History and flips to the next requested page
	 * 
	 * @param token
	 *            Page token of the Page to flip to
	 */
	protected void changePage(final String token) {
		if (History.getToken().equals(AppPart.favoritesLink.getTargetHistoryToken())) {
			setAppPart(new FavoritesAppPart());
		} else if (History.getToken().equals(AppPart.profileLink.getTargetHistoryToken())) {
			setAppPart(new ProfileAppPart());
		} else {
			setAppPart(new CandidatesAppPart());
		}
	}
}
