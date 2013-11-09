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
 * @since 12.10.2013
 * @version 1.0
 * @author oliver
 */
package pb.model;

import java.util.List;

/**
 * This is the Service for the Favorite Model, which will contain the logic of
 * this application
 * 
 * @author oliver
 */
public interface FavoriteModel {

	/**
	 * Voting contains the number of votes from one candidate to another and
	 * vice versa
	 * 
	 * @author oliver
	 */
	class Voting {
		protected final int otherId;
		protected final int ownLikes;
		protected int otherLikes;
		protected boolean pair = false;

		public Voting(final int otherId, final int ownLikes) {
			super();
			this.otherId = otherId;
			this.ownLikes = ownLikes;
			otherLikes = 0;
		}

		/**
		 * @return the otherLikes
		 */
		public int getOtherLikes() {
			return otherLikes;
		}

		/**
		 * @param otherLikes
		 *            the otherLikes to set
		 */
		public void setOtherLikes(final int otherLikes) {
			this.otherLikes = otherLikes;
		}

		/**
		 * @return the otherId
		 */
		public int getOtherId() {
			return otherId;
		}

		/**
		 * @return the ownLikes
		 */
		public int getOwnLikes() {
			return ownLikes;
		}

		/**
		 * @return the pair
		 */
		public boolean isPair() {
			return pair;
		}

		/**
		 * @param pair
		 *            the pair to set
		 */
		public void setPair(final boolean pair) {
			this.pair = pair;
		}
	}

	/**
	 * Add a like to the favorites of a candidate. If the the vote does not
	 * exist in the table a new one is added. The method also checks that the
	 * candidate still has enough credit to add a like.
	 * 
	 * @param voter
	 * @param voteFor
	 * @throws FavoriteModelException
	 *             when the candidate has not enough credit to give a vote, the
	 *             exception is thrown
	 */
	void voteForCandidate(int voter, int voteFor) throws FavoriteModelException;

	/**
	 * Get a list of all votes to and from the candidate
	 * 
	 * @param candidateId
	 * @return
	 * @throws FavoriteModelException
	 *             thrown when a malfunction occurs.
	 */
	List<Voting> getVotings(int candidateId) throws FavoriteModelException;
}
