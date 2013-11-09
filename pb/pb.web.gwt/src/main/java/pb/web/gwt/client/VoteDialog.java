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
 * @since 28.10.2013
 * @version 1.0
 * @author oliver
 */
package pb.web.gwt.client;

import pb.web.gwt.shared.Profile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog presenting the profile of another user and allows to vote for him.
 * 
 * @author oliver
 */
public abstract class VoteDialog extends DialogBox {

	final Button voteButton;
	final Button closeButton;
	protected final Messages messages = GWT.create(Messages.class);

	/**
	 * Constructor taking the profile to show
	 * 
	 * @param profile
	 *            Profile of the user to show
	 */
	public VoteDialog(final Profile profile) {
		setAnimationEnabled(true);
		setWidth("80%");
		final VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.setStyleName("formPanel");
		add(mainPanel);
		mainPanel.setWidth("100%");

		final HTML htmlNewHtml = new HTML("<center><h1>" + profile.getName() + "</h1></center>",
				true);
		mainPanel.add(htmlNewHtml);
		htmlNewHtml.setWidth("80%");
		mainPanel.setCellHorizontalAlignment(htmlNewHtml, HasHorizontalAlignment.ALIGN_CENTER);

		final Image image = new Image(profile.getAvatarUrl());
		mainPanel.add(image);
		mainPanel.setCellHorizontalAlignment(image, HasHorizontalAlignment.ALIGN_CENTER);
		image.setSize("160", "160");

		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setStyleName("formPanel");
		mainPanel.add(horizontalPanel);

		final Label lblNewLabel = new Label(messages.age());
		lblNewLabel.setStyleName("formPanel");
		horizontalPanel.add(lblNewLabel);

		final Label ageLabel = new Label(Integer.toString(profile.getAge()));
		ageLabel.setStyleName("formPanel");
		horizontalPanel.add(ageLabel);

		final Label descriptionLabel = new Label(profile.getDescription());
		descriptionLabel.setStyleName("formPanel");
		descriptionLabel.setWidth("80%");
		mainPanel.add(descriptionLabel);
		mainPanel.setCellHorizontalAlignment(descriptionLabel, HasHorizontalAlignment.ALIGN_LEFT);

		final HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setStyleName("formPanel");
		buttonPanel.setSpacing(10);
		buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainPanel.add(buttonPanel);
		buttonPanel.setWidth("80%");
		mainPanel.setCellHorizontalAlignment(buttonPanel, HasHorizontalAlignment.ALIGN_CENTER);

		final VerticalPanel verticalPanel_1 = new VerticalPanel();
		buttonPanel.add(verticalPanel_1);
		verticalPanel_1.setWidth("100%");

		voteButton = new Button(messages.vote());
		verticalPanel_1.add(voteButton);

		final VerticalPanel verticalPanel_2 = new VerticalPanel();
		buttonPanel.add(verticalPanel_2);
		verticalPanel_2.setWidth("100%");

		closeButton = new Button(messages.closeButton());
		verticalPanel_2.add(closeButton);
		verticalPanel_2.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
		buttonPanel.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);

		/**
		 * Adds a ClickHandler to the vote button that calls voteFor.
		 */
		voteButton.addClickHandler(new VoteForClickHandler(profile));

		/**
		 * Close button shall simply hide the dialog
		 */
		closeButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				hide();
			}
		});
	}

	public abstract void voteFor(int id);

	/**
	 * ClickHandler that calls the abstract method voteFor when Vote Button is
	 * selected
	 * 
	 * @author oliver
	 */
	protected class VoteForClickHandler implements ClickHandler {
		private final Profile profile;

		public VoteForClickHandler(final Profile profile) {
			this.profile = profile;
		}

		@Override
		public void onClick(final ClickEvent event) {
			voteFor(profile.getId());
		}
	}
}
