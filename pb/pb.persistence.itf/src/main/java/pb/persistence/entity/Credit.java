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
import javax.persistence.Table;

/**
 * Each candidate has only a limited credit of possible votes. This entity
 * stores the number of left likes of a candidate.
 * 
 * @author oliver
 */
@Entity
@Table(name = Credit.TABLE_NAME)
public class Credit {
	public static final String TABLE_NAME = "CREDIT";
	public static final String COLUMN_ID = "ID";
	public static final String COLUMN_CREDIT = "CREDIT";

	@Id
	@Column(name = COLUMN_ID)
	protected int candidateId;

	@Basic
	@Column(name = COLUMN_CREDIT)
	protected int credit;

	public Credit() {
		super();
	}

	public Credit(final int candidateId, final int credit) {
		super();
		this.candidateId = candidateId;
		this.credit = credit;
	}

	/**
	 * @return the credit
	 */
	public int getCredit() {
		return credit;
	}

	/**
	 * @param credit
	 *            the credit to set
	 */
	public void setCredit(final int credit) {
		this.credit = credit;
	}

	/**
	 * @return the candidateId
	 */
	public int getCandidateId() {
		return candidateId;
	}
}
