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
 * @since 29.10.2013
 * @version 1.0
 * @author oliver
 */
package pb.web.gwt.client.comp;

import pb.web.gwt.client.Messages;
import pb.web.gwt.client.ProfileServiceAsync;
import pb.web.gwt.client.VoteDialog;
import pb.web.gwt.shared.Profile;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Composite containing the Candidates Page. Putting the content in an extra
 * Class derived from Composite allows to design it with the WindowBuilder.
 * 
 * Candidates page shows all currently available partitions of the other gender.
 * 
 * @author oliver
 */
@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public class CandidatesComposite extends Composite {

	private static final int REFRESH_INTERVAL = 60000;
	protected int page = 0;
	protected Profile[] profiles = null;
	protected int maxPerPage = 10;

	final FlexTable candidatesFlexTable;
	final Button prevButton;
	final Button nextButton;

	private final ProfileServiceAsync profileService;
	private final Timer refreshTimer;

	/**
	 * Constructor taking service and messages object from parent
	 * 
	 * @param profileService
	 * @param messages
	 */
	@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
	public CandidatesComposite(final ProfileServiceAsync profileService, final Messages messages) {
		this.profileService = profileService;
		final VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setStyleName("formPanel");
		initWidget(verticalPanel);
		verticalPanel.setSize("100%", "100%");

		candidatesFlexTable = new FlexTable();
		candidatesFlexTable.setStyleName("formPanel");
		verticalPanel.add(candidatesFlexTable);
		candidatesFlexTable.setSize("100%", "");

		final HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setStyleName("formPanel");
		buttonPanel.setSpacing(10);
		buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.add(buttonPanel);
		buttonPanel.setWidth("100%");
		verticalPanel.setCellHorizontalAlignment(buttonPanel, HasHorizontalAlignment.ALIGN_CENTER);

		final VerticalPanel verticalPanel_1 = new VerticalPanel();
		buttonPanel.add(verticalPanel_1);
		verticalPanel_1.setWidth("100%");

		prevButton = new Button(messages.previous());
		verticalPanel_1.add(prevButton);
		prevButton.addClickHandler(new ClickHandler() {

			/**
			 * Flip to previous page
			 */
			@Override
			public void onClick(final ClickEvent event) {
				--page;
				if (profiles == null) {
					refreshList();
				} else {
					updateTable(profiles);
				}
			}
		});

		final VerticalPanel verticalPanel_2 = new VerticalPanel();
		buttonPanel.add(verticalPanel_2);
		verticalPanel_2.setWidth("100%");

		nextButton = new Button(messages.next());
		verticalPanel_2.add(nextButton);
		nextButton.addClickHandler(new ClickHandler() {

			/**
			 * Flip to next page
			 */
			@Override
			public void onClick(final ClickEvent event) {
				++page;
				if (profiles == null) {
					refreshList();
				} else {
					updateTable(profiles);
				}
			}
		});

		verticalPanel_2.setCellHorizontalAlignment(nextButton, HasHorizontalAlignment.ALIGN_RIGHT);

		refreshList();

		// setup timer to refresh list automatically
		refreshTimer = new Timer() {
			@Override
			public void run() {
				refreshList();
			}
		};
		refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Widget#onUnload()
	 */
	@Override
	protected void onUnload() {
		super.onUnload();
		refreshTimer.cancel();
	}

	/**
	 * Requests the current list of available candidates and calls UpdateTable
	 * when it arrived.
	 */
	protected void refreshList() {
		// Set up the callback object.
		final AsyncCallback<Profile[]> callback = new AsyncCallback<Profile[]>() {
			/**
			 * Do nothing when call failed, just go on
			 */
			@Override
			public void onFailure(final Throwable caught) {
				// do nothing
			}

			/**
			 * if request succeded store the current list for page update and
			 * refresh the Flextable
			 * 
			 * @param result
			 *            the list of currently available candidates
			 */
			@Override
			public void onSuccess(final Profile[] result) {
				profiles = result;
				updateTable(result);
			}
		};
		profileService.getCandidates(callback);
	}

	/**
	 * updates the flextable with the new list of Profiles. It also checks that
	 * the page field shows to a valid page and resets the parameter if not.
	 * 
	 * @param profiles
	 *            list of active profiles
	 */
	protected void updateTable(final Profile[] profiles) {
		candidatesFlexTable.clear();

		int pages = profiles.length / maxPerPage;
		if (profiles.length % maxPerPage != 0) {
			pages++;
		}
		int lPage = Math.min(page, pages - 1);
		lPage = Math.max(lPage, 0);
		page = lPage;

		for (int i = lPage * maxPerPage; (i < profiles.length) && (i < (lPage + 1) * maxPerPage); i++) {
			final Profile profile = profiles[i];
			final PushButton button = new PushButton(new Image(profile.getAvatarUrl()));
			button.setWidth("160px");
			button.addClickHandler(new ImageButtonClickHandler(profile));

			candidatesFlexTable.setWidget(i, 0, button);
			final VerticalPanel verticalPanel = new VerticalPanel();
			final Label nameLabel = new Label();
			nameLabel.setStyleName("formPanel");
			nameLabel.setText(profile.getName() + " (" + profile.getAge() + ")");
			verticalPanel.add(nameLabel);
			final Label descriptionLabel = new Label();
			descriptionLabel.setSize("100%", "100%");
			descriptionLabel.setStyleName("formPanel");
			descriptionLabel.setText(profile.getDescription());
			verticalPanel.add(descriptionLabel);
			candidatesFlexTable.setWidget(i, 1, verticalPanel);
		}
		if (lPage == 0) {
			prevButton.setEnabled(false);
		} else {
			prevButton.setEnabled(true);
		}

		if (lPage >= pages - 1) {
			nextButton.setEnabled(false);
		} else {
			nextButton.setEnabled(true);
		}
	}

	/**
	 * Implementation of the VoteDialog simply voting for a candidate
	 * 
	 * @author oliver
	 */
	protected class VoteDialogImpl extends VoteDialog {

		protected final Profile profile;

		/**
		 * Overwritten Constructor from parent storing the profile for doing the
		 * vote.
		 * 
		 * @param profile
		 */
		public VoteDialogImpl(final Profile profile) {
			super(profile);
			this.profile = profile;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see pb.web.gwt.client.VoteDialog#voteFor(int)
		 */
		@Override
		public void voteFor(final int id) {
			profileService.voteFor(profile.getId(), null);
			hide();
		}
	}

	/**
	 * When the user selects an image the VoteDialog shall be shown. Therefore
	 * this ClickHandler handles the click.
	 * 
	 * @author oliver
	 */
	protected class ImageButtonClickHandler implements ClickHandler {

		protected final Profile profile;

		public ImageButtonClickHandler(final Profile profile) {
			super();
			this.profile = profile;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt
		 * .event.dom.client.ClickEvent)
		 */
		@Override
		public void onClick(final ClickEvent event) {
			final VoteDialog voteDialog = new VoteDialogImpl(profile);
			voteDialog.center();
		}
	}
}
