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
package pb.model.impl;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;

import pb.model.FavoriteModel;
import pb.model.FavoriteModelException;
import pb.persistence.PbStorage;
import pb.persistence.avatar.Images;
import pb.persistence.entity.Candidate;
import pb.persistence.entity.Candidate.Gender;
import pb.persistence.entity.Credit;
import pb.persistence.entity.Vote;
import pb.persistence.entity.VotePK;

/**
 * The Favorite Mdoel service contains the logic of the game. It stores the
 * favorites of a candidate, the votes and the left credits.
 * 
 * @author oliver
 */
@Component(name = "pb.model", label = "PB Model", immediate = true, metatype = true)
@Service(value = FavoriteModel.class)
@Reference(name = "PBStorage", bind = "bind", unbind = "unbind", referenceInterface = PbStorage.class, cardinality = ReferenceCardinality.MANDATORY_UNARY, policy = ReferencePolicy.DYNAMIC)
@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public class FavoritModelImpl implements FavoriteModel {

	protected PbStorage storage = null;
	protected ReentrantLock mutex = new ReentrantLock();
	protected Semaphore storageSem = new Semaphore(0);
	protected int maxParallel = 10;
	protected static final int defaultNumberOfCandidates = 400;
	protected static final int defaultStartCredits = 25;
	protected static final int defaultMaxLikes = 3;

	@Property(intValue = defaultNumberOfCandidates)
	public static final String NUMBER_OF_CANDIDATES_NAME = "NumberOfCandidates";
	protected int numberOfCandidates = defaultNumberOfCandidates;

	@Property(intValue = defaultStartCredits)
	public static final String START_CREDITS_NAME = "StartCreditsAmount";
	protected int startCredit = defaultNumberOfCandidates;

	@Property(intValue = defaultMaxLikes)
	public static final String MAX_LIKES_NAME = "MaxLikes";
	protected int maxLikes = defaultMaxLikes;

	/**
	 * Activate method creating the database with all candidate objects and
	 * fills their credits
	 * 
	 * @param context
	 */
	@Activate
	protected void activate(final ComponentContext context) {
		final Dictionary<?, ?> props = context.getProperties();
		numberOfCandidates = readInteger(props, NUMBER_OF_CANDIDATES_NAME,
				defaultNumberOfCandidates);
		startCredit = readInteger(props, START_CREDITS_NAME, defaultStartCredits);
		maxLikes = readInteger(props, MAX_LIKES_NAME, defaultMaxLikes);
		try {
			for (int i = 0; i < numberOfCandidates; ++i) {
				// We have a 50 Percent chance to guess right, let's use it.
				Candidate candidate;
				if (Math.random() > 0.5) {
					candidate = new Candidate(i, Images.getFemaleAvatar(), "", 18, "",
							Gender.FEMALE);
				} else {
					candidate = new Candidate(i, Images.getMaleAvatar(), "", 18, "", Gender.MALE);
				}
				storage.storeCandidate(candidate);
				final Credit credit = new Credit(i, startCredit);
				storage.storeCredit(credit);
			}
		} catch (final Exception e) {
			Log.error("Error initiaing DataBase", e);
		}
	}

	/**
	 * Modified method that changes the number of candidates when a bigger value
	 * than the original one is created and add some credits when the number of
	 * default credits is increased.
	 * 
	 * @param context
	 */
	@Modified
	protected void modified(final ComponentContext context) {
		final Dictionary<?, ?> props = context.getProperties();
		final int modifiedNumberOfCandidates = readInteger(props, NUMBER_OF_CANDIDATES_NAME,
				defaultNumberOfCandidates);
		final int modifiedStartCredit = readInteger(props, START_CREDITS_NAME, defaultStartCredits);

		if (modifiedNumberOfCandidates > numberOfCandidates) {
			try {
				for (int i = numberOfCandidates; i < modifiedNumberOfCandidates; ++i) {
					// We have a 50 Percent chance to guess right, let's use it.
					Candidate candidate;
					if (Math.random() > 0.5) {
						candidate = new Candidate(i, Images.getFemaleAvatar(), "", 18, "",
								Gender.FEMALE);
					} else {
						candidate = new Candidate(i, Images.getMaleAvatar(), "", 18, "",
								Gender.MALE);
					}
					storage.storeCandidate(candidate);
					final Credit credit = new Credit(i, startCredit);
					storage.storeCredit(credit);
				}
				numberOfCandidates = modifiedNumberOfCandidates;
			} catch (final Exception e) {
				Log.error("Error Extending DataBase", e);
			}
		}
		if (modifiedStartCredit > startCredit) {
			final int diff = modifiedStartCredit - startCredit;
			for (int i = 0; i < numberOfCandidates; ++i) {
				mutex.lock();
				try {
					final Credit credit = storage.getCredit(i);
					credit.setCredit(credit.getCredit() + diff);
					storage.storeCredit(credit);
				} catch (final Throwable e) {
					Log.error("Error Extending DataBase", e);
				} finally {
					mutex.unlock();
				}
			}
			startCredit = modifiedStartCredit;
		}
	}

	/**
	 * Reads an integer Value from the dictionary
	 * 
	 * @param props
	 *            Dictionary containg settings data
	 * @param name
	 *            name of the property
	 * @param defaultValue
	 *            default value for the property if property can't be red or
	 *            does not exist
	 * @return the value from the dictionary or the default value
	 */
	protected int readInteger(final Dictionary<?, ?> props, final String name,
			final int defaultValue) {
		int ret = defaultValue;
		try {
			final Object rawValue = props.get(name);
			if (rawValue != null) {
				ret = (Integer) rawValue;
			}
		} catch (final ClassCastException cce) {
			ret = defaultValue;
			Log.error("Error reading " + name + "  Property", cce);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.model.FavoriteModel#voteForCandidate(int, int)
	 */
	@Override
	public void voteForCandidate(final int voter, final int voteFor) throws FavoriteModelException {
		if (storageSem.tryAcquire()) {
			mutex.lock();
			try {
				final Credit credit = storage.getCredit(voter);
				final Candidate rCandidate = storage.getCandidate(voteFor);

				if (credit.getCredit() <= 0) {
					throw new FavoriteModelException(FavoriteModelException.Cause.NOT_ENOUGH_CREDIT);
				}
				if (rCandidate == null) {
					throw new FavoriteModelException(FavoriteModelException.Cause.UNKNOWN);
				} else {
					if (!rCandidate.isAvailable()) {
						throw new FavoriteModelException(
								FavoriteModelException.Cause.ALLREADY_PAIRED);
					}

					final VotePK pk = new VotePK(voter, voteFor);
					final VotePK rpk = new VotePK(voteFor, voter);

					Vote vote = storage.getVote(pk);
					Vote rVote = storage.getVote(rpk);
					if (vote == null) {
						vote = new Vote(voter, voteFor);
					}
					if (rVote == null) {
						rVote = new Vote(voteFor, voter);
					}
					if (vote.getLikes() < maxLikes) {
						vote.setLikes(vote.getLikes() + 1);
						credit.setCredit(credit.getCredit() - 1);
					}
					// Check for Partner found
					if ((vote.getLikes() == maxLikes) && (rVote.getLikes() == maxLikes)) {
						final Credit rCredit = storage.getCredit(voteFor);
						final Candidate candidate = storage.getCandidate(voter);

						storage.removeAllVotes(voter);
						storage.removeAllVotes(voteFor);
						credit.setCredit(0);
						rCredit.setCredit(0);
						candidate.setAvailable(false);
						rCandidate.setAvailable(false);
						storage.storeCredit(rCredit);
						storage.storeCandidate(candidate);
						storage.storeCandidate(rCandidate);
					}
					storage.storeVote(vote);
					storage.storeVote(rVote);
					storage.storeCredit(credit);
				}
			} catch (final FavoriteModelException fme) {
				throw (fme);
			} catch (final Exception thr) {
				Log.error("Error occured while voting", thr);
			} finally {
				mutex.unlock();
				storageSem.release();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.model.FavoriteModel#getVotings(int)
	 */
	@Override
	public List<Voting> getVotings(final int candidateId) throws FavoriteModelException {
		final List<Voting> ret = new ArrayList<>();
		if (storageSem.tryAcquire()) {
			try {
				final List<Vote> votes = storage.getVotesByCandidateId(candidateId);
				for (final Vote vote : votes) {
					final Voting voting = new Voting(vote.getVoteFor(), vote.getLikes());
					final VotePK pk = new VotePK(vote.getVoteFor(), candidateId);
					final Vote rVote = storage.getVote(pk);
					if (rVote == null) {
						voting.setOtherLikes(0);
					} else {
						voting.setOtherLikes(rVote.getLikes());
					}
					if ((voting.getOwnLikes() == maxLikes) && (voting.getOtherLikes() == maxLikes)) {
						voting.setPair(true);
					} else {
						voting.setPair(false);
					}
					ret.add(voting);
				}
			} finally {
				storageSem.release();
			}
		}
		return ret;
	}

	/**
	 * SCR Bind Method for the PBStorage service.
	 * 
	 * @param storage
	 *            storage service for persisting credits votes etc.
	 */
	protected void bind(final PbStorage storage) {
		this.storage = storage;
		storageSem.release(maxParallel);
	}

	/**
	 * SCR unbind method for the PBStorage service.
	 * 
	 * @param storage
	 *            storage service for persisting credits votes etc.
	 */
	protected void unbind(final PbStorage storage) {
		try {
			if (storage.equals(this.storage)) {
				storageSem.acquire(maxParallel);
				this.storage = null;
			}
		} catch (final InterruptedException e) {
			Log.error("Error occured while unbinding PBStorage", e);
		}
	}
}
