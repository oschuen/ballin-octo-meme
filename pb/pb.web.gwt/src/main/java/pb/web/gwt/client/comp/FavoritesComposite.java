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
package pb.web.gwt.client.comp;

import pb.web.gwt.client.Messages;
import pb.web.gwt.client.ProfileServiceAsync;
import pb.web.gwt.client.VoteDialog;
import pb.web.gwt.shared.Favorite;
import pb.web.gwt.shared.FavoriteList;
import pb.web.gwt.shared.Profile;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Composite containing the Favorites Page. Putting the content in an extra
 * Class derived from Composite allows to design it with the WindowBuilder
 * 
 * Favorite Page displays all Favorites of the current User and allows to add
 * new vote to them.
 * 
 * @author oliver
 */
@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public class FavoritesComposite extends Composite {
	private final VerticalPanel verticalPanel;
	private final FlexTable candidatesFlexTable;
	private final HTML html;
	private final Timer refreshTimer;
	private static final int REFRESH_INTERVAL = 60000;

	private final Messages messages;

	private final ProfileServiceAsync profileService;

	/**
	 * Constructor taking service and messages object from parent
	 * 
	 * @param profileService
	 * @param messages
	 */
	@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
	public FavoritesComposite(final ProfileServiceAsync profileService, final Messages messages) {
		this.profileService = profileService;
		this.messages = messages;
		verticalPanel = new VerticalPanel();
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setStyleName("formPanel");
		initWidget(verticalPanel);
		verticalPanel.setSize("100%", "100%");

		html = new HTML("<h2>" + messages.creditsLeft() + "</h2>");
		verticalPanel.add(html);
		verticalPanel.setCellHorizontalAlignment(html, HasHorizontalAlignment.ALIGN_CENTER);

		candidatesFlexTable = new FlexTable();
		candidatesFlexTable.setStyleName("formPanel");
		verticalPanel.add(candidatesFlexTable);
		candidatesFlexTable.setSize("100%", "");

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
	 * Requests the current Favorites list and causes an update of the FlexTable
	 * when the list arrived.
	 */
	protected void refreshList() {
		final AsyncCallback<FavoriteList> callback = new AsyncCallback<FavoriteList>() {

			/**
			 * When the request for the list succeeded update the table or
			 * present the new Partner in case a pair was found
			 * 
			 * @param result
			 *            the current Favorite list
			 */
			@Override
			public void onSuccess(final FavoriteList result) {
				final Favorite pair = result.getPair();
				// Check for pair
				if (pair == null) {
					updateTable(result);
				} else {
					refreshTimer.cancel();
					presentPartner(pair);
				}
			}

			/**
			 * When list couldn't be evaluated just go on
			 */
			@Override
			public void onFailure(final Throwable caught) {
				// do nothing
			}
		};
		profileService.getFavoriteList(callback);
	}

	/**
	 * when two candidates found each other the Favorite objects paired field is
	 * set to true. In this case this function changes the view to show the new
	 * partner of the current candidate
	 * 
	 * @param favorite
	 *            the other candidate of the new pair
	 */
	protected void presentPartner(final Favorite favorite) {
		verticalPanel.clear();

		final HTML html = new HTML("<h2>" + messages.partnerFound() + "</h2>");
		verticalPanel.add(html);
		verticalPanel.setCellHorizontalAlignment(html, HasHorizontalAlignment.ALIGN_CENTER);

		final Image favImage = new Image(favorite.getAvatarUrl());
		favImage.setSize("160", "160");
		verticalPanel.add(favImage);
		verticalPanel.setCellHorizontalAlignment(favImage, HasHorizontalAlignment.ALIGN_CENTER);
	}

	/**
	 * the current votes are displayed as images. This function returns the url
	 * of a number image
	 * 
	 * @param voteNumber
	 *            the current vote count
	 * @return the url to the image representing the number
	 */
	protected String getVoteImageUrl(final int voteNumber) {
		return "pict/" + voteNumber + ".png";
	}

	/**
	 * updates the flex table showing the current list of favorites
	 * 
	 * @param favoriteList
	 *            all favorites of the current candidate
	 */
	protected void updateTable(final FavoriteList favoriteList) {
		candidatesFlexTable.clear();
		html.setHTML("<h2>" + messages.creditsLeft() + " " + favoriteList.getCredits() + "</h2>");
		final Favorite favorites[] = favoriteList.getFavorites();
		for (int i = 0; i < favorites.length; i++) {
			final Favorite favorite = favorites[i];
			final Image buttonImage = new Image(favorite.getAvatarUrl());
			buttonImage.setSize("100px", "100px");
			final PushButton button = new PushButton(buttonImage);
			button.setSize("100px", "100px");
			button.addClickHandler(new RequestProfileClickHandler(favorite.getFavId()));
			candidatesFlexTable.setWidget(i, 0, button);

			final Image myVoteImage = new Image(getVoteImageUrl(favorite.getMyVotes()));
			myVoteImage.setSize("100px", "100px");
			candidatesFlexTable.setWidget(i, 1, myVoteImage);

			final Image otherVoteImage = new Image(getVoteImageUrl(favorite.getFavVotes()));
			otherVoteImage.setSize("100px", "100px");
			candidatesFlexTable.setWidget(i, 2, otherVoteImage);
		}
	}

	/**
	 * When the image of a Favorite is clicked, this Clickhandler handles it.
	 * Since the favorite object only contains the candidates id of the
	 * favorated candidate, it is needed to request his profile before
	 * presenting it.
	 * 
	 * @author oliver
	 */
	protected class RequestProfileClickHandler implements ClickHandler {

		protected final int id;

		public RequestProfileClickHandler(final int id) {
			super();
			this.id = id;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google
		 * .gwt .event.dom.client.ClickEvent)
		 */
		@Override
		public void onClick(final ClickEvent event) {
			final AsyncCallback<Profile> callback = new AsyncCallback<Profile>() {

				@Override
				public void onFailure(final Throwable caught) {
					// do nothing
				}

				/**
				 * The profile of the favorited candidate could be evaluated, so
				 * show the VoteDialog containing it
				 * 
				 * @param result
				 *            The Profile of the favorited Candidate
				 */
				@Override
				public void onSuccess(final Profile result) {
					final VoteDialog voteDialog = new VoteDialogImpl(result);
					voteDialog.center();
				}
			};
			profileService.getProfileById(id, callback);
		}
	}

	/**
	 * Implementation of the VoteDialog refreshing the list of favorites when a
	 * vote took place
	 * 
	 * @author oliver
	 */
	protected class VoteDialogImpl extends VoteDialog {

		protected final Profile profile;

		/**
		 * Overwriting parents constructor to store the profile
		 * 
		 * @param profile
		 *            Profile of a Candidate that can be voted war
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
			final AsyncCallback<Void> callback = new AsyncCallback<Void>() {

				@Override
				public void onFailure(final Throwable caught) {
					// do nothing
				}

				/**
				 * If vote succeeded refresh the list of favorites to show the
				 * new intermediate result
				 * 
				 * @param result
				 */
				@Override
				public void onSuccess(final Void result) {
					refreshList();
				}
			};

			profileService.voteFor(profile.getId(), callback);
			hide();
		}
	}
}
