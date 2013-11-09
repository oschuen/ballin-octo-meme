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

import java.io.Serializable;

/**
 * Primary Key Class for the Votes table, containing the ID of the one who votes
 * and the id of the one he votes for.
 * 
 * @author oliver
 */
public class VotePK implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3144381584041470879L;
	protected int voterId;
	protected int voteFor;

	public VotePK() {
		super();
		voterId = -1;
		voteFor = -1;
	}

	public VotePK(final int voterId, final int voteFor) {
		super();
		this.voterId = voterId;
		this.voteFor = voteFor;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + voteFor;
		result = prime * result + voterId;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final VotePK other = (VotePK) obj;
		if (voteFor != other.voteFor) {
			return false;
		}
		if (voterId != other.voterId) {
			return false;
		}
		return true;
	}
}
