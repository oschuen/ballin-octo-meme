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
 * A list containing all favorites of a candidate and his left credits
 * 
 * @author oliver
 */
@SuppressWarnings({ "PMD.ArrayIsStoredDirectly", "PMD.MethodReturnsInternalArray" })
public class FavoriteList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5093314387683685148L;
	protected int credits = 0;
	protected Favorite[] favorites = null;

	public FavoriteList() {
		super();
	}

	/**
	 * Constructor setting all values
	 * 
	 * @param credits
	 *            number of credits of the requester
	 * @param favorites
	 *            the favorites of the requester
	 */
	public FavoriteList(final int credits, final Favorite[] favorites) {
		super();
		this.credits = credits;
		this.favorites = favorites;
	}

	/**
	 * @return the Favorite that is paired with the requester or null
	 */
	public Favorite getPair() {
		Favorite ret = null;
		if (favorites != null) {
			for (int i = 0; i < favorites.length; i++) {
				if (favorites[i].isPaired()) {
					ret = favorites[i];
				}
			}
		}
		return ret;
	}

	/**
	 * Credits left of the requester
	 * 
	 * @return the credits
	 */
	public int getCredits() {
		return credits;
	}

	/**
	 * Credits left of the requester
	 * 
	 * @param credits
	 *            the credits to set
	 */
	public void setCredits(final int credits) {
		this.credits = credits;
	}

	/**
	 * List of all favorites of the requester
	 * 
	 * @return the favorites
	 */
	public Favorite[] getFavorites() {
		return favorites;
	}

	/**
	 * List of all favorites of the requester
	 * 
	 * @param favorites
	 *            the favorites to set
	 */
	public void setFavorites(final Favorite[] favorites) {
		this.favorites = favorites;
	}
}
