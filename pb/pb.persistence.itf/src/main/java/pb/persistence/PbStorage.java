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
 * @since 11.10.2013
 * @version 1.0
 * @author oliver
 */
package pb.persistence;

import java.util.List;

import pb.persistence.entity.Candidate;
import pb.persistence.entity.Credit;
import pb.persistence.entity.Vote;
import pb.persistence.entity.VotePK;
import pb.persistence.entity.Candidate.Gender;

/**
 * Service for storing all required Entities of the Application.
 * 
 * @author oliver
 * 
 */
public interface PbStorage {

	/**
	 * returns the candidate with the given id
	 * 
	 * @param id
	 *            the unique id of a candidate
	 * @return the candidate or null when candidate doesn't exist
	 */
	public Candidate getCandidate(int id);

	/**
	 * Stores a candidate to the database. The candidate must have a unique id
	 * set
	 * 
	 * @param candidate
	 *            the candidate to store.
	 * @return true when storing performed without problem
	 */
	public boolean storeCandidate(Candidate candidate);

	/**
	 * returns all candidates with the given gender
	 * 
	 * @param gender
	 *            the gender of the candidates searched for
	 * @return a list of Candidates with the requested gender
	 */
	public List<Candidate> getCandidateByGender(Gender gender);

	/**
	 * returns a Credit object with the requested id
	 * 
	 * @param id
	 *            id of the Credit object searched for
	 * @return the Credit object or null
	 */
	public Credit getCredit(int id);

	/**
	 * stores a credit object to the database
	 * 
	 * @param credit
	 *            the credit object to store
	 * @return true when object is stored
	 */
	public boolean storeCredit(Credit credit);

	/**
	 * returns a list of Votes the candidate with the given id made
	 * 
	 * @param id
	 *            the id of the candidate
	 * @return a list of Votes
	 */
	public List<Vote> getVotesByCandidateId(int id);

	/**
	 * returns a list of votes, where someone else voted for the candidate
	 * 
	 * @param id
	 *            the id of the candidate for which was voted
	 * @return a list of Votes
	 */
	public List<Vote> getVotesForCandidateId(int id);

	/**
	 * returns the vote with the given primary key
	 * 
	 * @param pk
	 *            the primary key of the vote
	 * @return the vote or null
	 */
	public Vote getVote(VotePK pk);

	/**
	 * stores a vote to the database
	 * 
	 * @param vote
	 *            the vote to store
	 * @return true when storing succeeded
	 */
	public boolean storeVote(Vote vote);

	/**
	 * removes all votes for and from a candidate
	 * 
	 * @param id
	 *            the id of the candidate
	 * @return true when remove operation succeeded
	 */
	public boolean removeAllVotes(int id);

	/**
	 * evaluates the id of a candidate by the id of his connection
	 * 
	 * @param ip
	 *            the ip as a string
	 * @return the id of the candidate
	 */
	public int getId(String ip);
}
