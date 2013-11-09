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
package pb.web.gwt.server;

import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import pb.model.FavoriteModel;
import pb.model.FavoriteModel.Voting;
import pb.model.FavoriteModelException;
import pb.persistence.PbStorage;
import pb.persistence.entity.Candidate;
import pb.persistence.entity.Candidate.Gender;
import pb.web.gwt.client.ProfileService;
import pb.web.gwt.server.impl.Log;
import pb.web.gwt.server.impl.ServiceRequester;
import pb.web.gwt.shared.Favorite;
import pb.web.gwt.shared.FavoriteList;
import pb.web.gwt.shared.Profile;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of the RPC Servlet. This Servlet is a kind of bridge to the
 * PBStorage and the FavoriteModel Service from the OSGi side of the
 * implementation.
 * 
 * @author oliver
 */
@SuppressWarnings({ "serial", "PMD.AvoidInstantiatingObjectsInLoops" })
public class ProfileServiceImpl extends RemoteServiceServlet implements ProfileService {

	protected PbStorage storage = null;
	protected FavoriteModel model = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		ServiceRequester.bindClient(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	@Override
	public void destroy() {
		super.destroy();
		ServiceRequester.unbindClient(this);
	}

	/**
	 * @param avatarId
	 *            the id of the candidate the image url is searched for
	 * @param count
	 *            the version number of the image
	 * @return the url of an avatar image of an candidate
	 */
	protected String getAvatarImageUrl(final int avatarId, final int count) {
		return "../simple/avatar.png?id=" + avatarId + "&count=" + count;
	}

	/**
	 * Converts a Candidate to an Profile object.
	 * 
	 * @param candidate
	 *            the candidate that shall be presented in the Web page
	 * @return a profile containing the copied informations from the candidates
	 *         object
	 */
	protected Profile convertCandidate(final Candidate candidate) {
		if (candidate == null) {
			return null;
		}
		final Profile ret = new Profile();
		ret.setId(candidate.getId());
		ret.setName(candidate.getName());
		ret.setAge(candidate.getAge());
		ret.setDescription(candidate.getShortDescription());
		Log.info("Current gender is : %s", candidate.getGender().name());
		ret.setGender(Gender.MALE.equals(candidate.getGender()) ? Profile.male : Profile.female);
		ret.setAvatarUrl(getAvatarImageUrl(candidate.getId(), candidate.getImageCount()));
		return ret;
	}

	@Override
	public Profile getProfileById(final int id) throws ServiceException {
		Profile ret = null;
		if (storage == null) {
			throw new ServiceException("Storage Service is not available");
		} else {
			final Candidate candidate = storage.getCandidate(id);
			if (candidate == null) {
				Log.info("Someone requests unknown Profile %d ", id);
			} else {
				ret = convertCandidate(candidate);
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.web.gwt.client.ProfileService#getProfile()
	 */
	@Override
	public Profile getProfile() throws ServiceException {
		Profile ret = null;

		final HttpServletRequest request = this.getThreadLocalRequest();
		if (storage == null) {
			throw new ServiceException("Storage Service is not available");
		} else {
			final int id = storage.getId(request.getRemoteAddr());
			ret = getProfileById(id);
		}
		return ret;
	}

	/**
	 * Bind method for the PBStorage instance. Is called by the ServiceBroker
	 * 
	 * @param storage
	 */
	public void bind(final PbStorage storage) {
		Log.info("Profile Service received PBStorage");
		this.storage = storage;
	}

	/**
	 * Unbind method for the PBStorage instance. Is called by the ServiceBroker
	 * 
	 * @param storage
	 */
	public void unbind(final PbStorage storage) {
		if (storage.equals(this.storage)) {
			Log.info("Profile Service lost PBStorage");
			this.storage = null;
		}
	}

	/**
	 * Bind method for the FavoriteModel instance. Is called by the
	 * ServiceBroker
	 * 
	 * @param model
	 */
	public void bind(final FavoriteModel model) {
		Log.info("Profile Service received FavoriteModel");
		this.model = model;
	}

	/**
	 * Unbind method for the FavoriteModel instance. Is called by the
	 * ServiceBroker
	 * 
	 * @param model
	 */
	public void unbind(final FavoriteModel model) {
		if (model.equals(this.model)) {
			Log.info("Profile Service lost FavoriteModel");
			this.model = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.web.gwt.client.ProfileService#getCandidates()
	 */
	@Override
	public Profile[] getCandidates() throws ServiceException {
		Profile ret[] = null;

		final HttpServletRequest request = this.getThreadLocalRequest();
		if (storage == null) {
			throw new ServiceException("Storage Service is not available");
		} else {
			final int id = storage.getId(request.getRemoteAddr());
			final Candidate candidate = storage.getCandidate(id);
			final List<Candidate> candidates = storage.getCandidateByGender(Gender.FEMALE
					.equals(candidate.getGender()) ? Gender.MALE : Gender.FEMALE);
			ret = new Profile[candidates.size()];
			final Iterator<Candidate> iter = candidates.iterator();
			for (int i = 0; i < ret.length && iter.hasNext(); ++i) {
				final Candidate other = iter.next();
				ret[i] = convertCandidate(other);
			}
		}
		Log.info("Return a list of %d candidates", ret.length);

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.web.gwt.client.ProfileService#voteFor(int)
	 */
	@Override
	public void voteFor(final int id) throws ServiceException {
		final HttpServletRequest request = this.getThreadLocalRequest();
		if (storage == null) {
			throw new ServiceException("Storage Service is not available");
		} else if (model == null) {
			throw new ServiceException("Favorite Model is not available");
		} else {
			final int myId = storage.getId(request.getRemoteAddr());
			try {
				model.voteForCandidate(myId, id);
			} catch (final FavoriteModelException e) {
				throw new ServiceException(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.web.gwt.client.ProfileService#getFavoriteList()
	 */
	@Override
	public FavoriteList getFavoriteList() throws ServiceException {
		Favorite favorites[] = null;
		int credit;
		final HttpServletRequest request = this.getThreadLocalRequest();
		if (storage == null) {
			throw new ServiceException("Storage Service is not available");
		} else if (model == null) {
			throw new ServiceException("Favorite Model is not available");
		} else {
			try {
				final int id = storage.getId(request.getRemoteAddr());
				credit = storage.getCredit(id).getCredit();
				final List<Voting> votings = model.getVotings(id);
				favorites = new Favorite[votings.size()];
				for (int i = 0; i < favorites.length && i < votings.size(); ++i) {
					final Voting voting = votings.get(i);
					final Favorite favorite = new Favorite();
					favorites[i] = favorite;
					favorite.setFavId(voting.getOtherId());
					favorite.setMyVotes(voting.getOwnLikes());
					favorite.setFavVotes(voting.getOtherLikes());
					favorite.setPaired(voting.isPair());
					favorite.setAvatarUrl(getAvatarImageUrl(voting.getOtherId(), 0));
				}
			} catch (final FavoriteModelException e) {
				throw new ServiceException(e);
			}
		}
		return new FavoriteList(credit, favorites);
	}
}
