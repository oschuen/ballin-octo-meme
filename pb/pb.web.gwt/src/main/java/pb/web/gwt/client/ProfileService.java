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
 * @since 19.10.2013
 * @version 1.0
 * @author oliver
 */
package pb.web.gwt.client;

import pb.web.gwt.shared.FavoriteList;
import pb.web.gwt.shared.Profile;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * ProfileService is a bridge to the OSGi services PBStorage and FavoriteModel.
 * It does all the needed conversion from the OSGi side entities into those
 * implemented for the GWT side.
 * 
 * @author oliver
 */
@RemoteServiceRelativePath("profile")
public interface ProfileService extends RemoteService {

	/**
	 * returns the profile of the user belonging to ip of the request
	 * 
	 * @return the profile of the current user
	 * @throws Exception
	 */
	public Profile getProfile() throws ServiceException;

	/**
	 * Returns the profile belonging to the given id
	 * 
	 * @param id
	 *            id of the profile requested
	 * @return the Profile or null
	 * @throws Exception
	 */
	public Profile getProfileById(final int id) throws ServiceException;

	/**
	 * returns a list of possible partners for this user
	 * 
	 * @return a List of possible partners
	 * @throws Exception
	 */
	public Profile[] getCandidates() throws ServiceException;

	/**
	 * adds a vote to the candidate with the given id
	 * 
	 * @param id
	 *            id of the candidate to vote for
	 */
	public void voteFor(int id) throws ServiceException;

	/**
	 * Gets all Favorites and the credit for the current user in a single
	 * Object.
	 */
	public FavoriteList getFavoriteList() throws ServiceException;

	/**
	 * Exception class used for member functions
	 * 
	 * @author oliver
	 */
	class ServiceException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7537704973159216314L;

		public ServiceException() {
			super();
		}

		public ServiceException(final String message) {
			super(message);
		}

		public ServiceException(final Throwable cause) {
			super(cause);
		}
	}
}
