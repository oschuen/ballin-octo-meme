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
package pb.web.gwt.shared;

import java.io.Serializable;

/**
 * Another candidate that was added to the favorites list.
 * 
 * @author oliver
 */
public class Favorite implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6547754413323954528L;
	protected int favId = -1;
	protected String avatarUrl = "";
	protected boolean paired = false;
	protected int myVotes = 0;
	protected int favVotes = 0;

	public Favorite() {
		super();
	}

	/**
	 * Constructor setting all values
	 * 
	 * @param favId
	 *            the id of the favorited candidate
	 * @param avatarUrl
	 *            the url to the avatar image
	 * @param paired
	 *            true when this is part of a couple with the candidate
	 *            requested the list
	 * @param myVotes
	 *            the votes the candidate requested the list gave to the
	 *            favorite
	 * @param favVotes
	 *            number of votes from the favorite to the candidate
	 */

	public Favorite(final int favId, final String avatarUrl, final boolean paired,
			final int myVotes, final int favVotes) {
		super();
		this.favId = favId;
		this.avatarUrl = avatarUrl;
		this.paired = paired;
		this.myVotes = myVotes;
		this.favVotes = favVotes;
	}

	/**
	 * @return the paired
	 */
	public boolean isPaired() {
		return paired;
	}

	/**
	 * True when favorite and requester are a couple
	 * 
	 * @param paired
	 *            the paired to set
	 */
	public void setPaired(final boolean paired) {
		this.paired = paired;
	}

	/**
	 * votes from the requester to the favorite
	 * 
	 * @return the myVotes
	 */
	public int getMyVotes() {
		return myVotes;
	}

	/**
	 * votes from the requester to the favorite
	 * 
	 * @param myVotes
	 *            the myVotes to set
	 */
	public void setMyVotes(final int myVotes) {
		this.myVotes = myVotes;
	}

	/**
	 * Votes from the Favorite to the requester
	 * 
	 * @return the favVotes
	 */
	public int getFavVotes() {
		return favVotes;
	}

	/**
	 * Votes from the Favorite to the requester
	 * 
	 * @param favVotes
	 *            the favVotes to set
	 */
	public void setFavVotes(final int favVotes) {
		this.favVotes = favVotes;
	}

	/**
	 * the candidates id of the favorite
	 * 
	 * @return the favId
	 */
	public int getFavId() {
		return favId;
	}

	/**
	 * the candidates id of the favorite
	 * 
	 * @param favId
	 *            the favId to set
	 */
	public void setFavId(final int favId) {
		this.favId = favId;
	}

	/**
	 * the url of the favorites photo
	 * 
	 * @return the avatarUrl
	 */
	public String getAvatarUrl() {
		return avatarUrl;
	}

	/**
	 * the url of the favorites photo
	 * 
	 * @param avatarUrl
	 *            the avatarUrl to set
	 */
	public void setAvatarUrl(final String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
}
