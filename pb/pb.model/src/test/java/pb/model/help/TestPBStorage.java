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
package pb.model.help;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pb.persistence.PbStorage;
import pb.persistence.entity.Candidate;
import pb.persistence.entity.Credit;
import pb.persistence.entity.Vote;
import pb.persistence.entity.VotePK;
import pb.persistence.entity.Candidate.Gender;

/**
 * @author oliver
 * 
 */
public class TestPBStorage implements PbStorage {

	protected HashMap<Integer, Candidate> candidates = new HashMap<>();
	protected HashMap<Integer, Credit> credits = new HashMap<>();
	protected HashMap<VotePK, Vote> votes = new HashMap<>();
	protected HashMap<String, Integer> clientIds = new HashMap<>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.persistence.PbStorage#getCandidate(int)
	 */
	@Override
	public Candidate getCandidate(final int id) {
		return candidates.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pb.persistence.PbStorage#storeCandidate(pb.persistence
	 * .entity.Candidate)
	 */
	@Override
	public boolean storeCandidate(final Candidate candidate) {
		candidates.put(candidate.getId(), candidate);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.persistence.PbStorage#getCandidateByGender(pb.
	 * persistence.entity.Candidate.Gender)
	 */
	@Override
	public List<Candidate> getCandidateByGender(final Gender gender) {
		final List<Candidate> ret = new ArrayList<>();
		for (final Candidate candidate : candidates.values()) {
			if (gender.equals(candidate.getGender())) {
				ret.add(candidate);
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.persistence.PbStorage#getCredit(int)
	 */
	@Override
	public Credit getCredit(final int id) {
		return credits.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pb.persistence.PbStorage#storeCredit(pb.persistence
	 * .entity.Credit)
	 */
	@Override
	public boolean storeCredit(final Credit credit) {
		credits.put(credit.getCandidateId(), credit);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.persistence.PbStorage#getVotesByCandidateId(int)
	 */
	@Override
	public List<Vote> getVotesByCandidateId(final int id) {
		final List<Vote> ret = new ArrayList<>();
		for (final VotePK pk : votes.keySet()) {
			if (pk.getVoterId() == id) {
				ret.add(getVote(pk));
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.persistence.PbStorage#getVotesForCandidateId(int)
	 */
	@Override
	public List<Vote> getVotesForCandidateId(final int id) {
		final List<Vote> ret = new ArrayList<>();
		for (final VotePK pk : votes.keySet()) {
			if (pk.getVoteFor() == id) {
				ret.add(getVote(pk));
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pb.persistence.PbStorage#getVote(pb.persistence.entity
	 * .VotePK)
	 */
	@Override
	public Vote getVote(final VotePK pk) {
		return votes.get(pk);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pb.persistence.PbStorage#storeVote(pb.persistence.entity
	 * .Vote)
	 */
	@Override
	public boolean storeVote(final Vote vote) {
		votes.put(new VotePK(vote.getVoterId(), vote.getVoteFor()), vote);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.persistence.PbStorage#removeAllVotes(int)
	 */
	@Override
	public boolean removeAllVotes(final int id) {
		final List<Vote> removes = getVotesForCandidateId(id);
		removes.addAll(getVotesByCandidateId(id));
		for (final Vote vote : removes) {
			votes.remove(new VotePK(vote.getVoterId(), vote.getVoteFor()));
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.persistence.PbStorage#getId(java.lang.String)
	 */
	@Override
	public int getId(final String ip) {
		Integer ret = clientIds.get(ip);
		if (ret == 0) {
			ret = clientIds.values().size();
		}
		return ret;
	}

}
