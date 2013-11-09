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
package pb.persistence.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * Entity containing the votes from one Candidate to another.
 * 
 * @author oliver
 */
@Entity
@Table(name = Vote.TABLE_NAME)
@IdClass(VotePK.class)
public class Vote {

	public static final String TABLE_NAME = "VOTES";
	public static final String COLUMN_VOTER_ID = "VOTER_ID";
	public static final String COLUMN_VOTE_FOR_ID = "VOTE_FOR";
	public static final String COLUMN_LIKES = "LIKES";

	@Id
	@Column(name = COLUMN_VOTER_ID)
	protected int voterId;

	@Id
	@Column(name = COLUMN_VOTE_FOR_ID)
	protected int voteFor;

	@Basic
	@Column(name = COLUMN_LIKES)
	protected int likes;

	public Vote() {
		super();
	}

	public Vote(final int voterId, final int voteFor) {
		super();
		this.voterId = voterId;
		this.voteFor = voteFor;
		this.likes = 0;
	}

	/**
	 * @return the likes
	 */
	public int getLikes() {
		return likes;
	}

	/**
	 * @param likes
	 *            the likes to set
	 */
	public void setLikes(final int likes) {
		this.likes = likes;
	}

	/**
	 * @return the voterId
	 */
	public int getVoterId() {
		return voterId;
	}

	/**
	 * @return the voteFor
	 */
	public int getVoteFor() {
		return voteFor;
	}
}
