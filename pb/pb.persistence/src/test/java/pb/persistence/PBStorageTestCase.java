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

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;

import junit.framework.TestCase;
import pb.persistence.entity.Candidate;
import pb.persistence.entity.Candidate.Gender;
import pb.persistence.entity.Credit;
import pb.persistence.entity.Vote;
import pb.persistence.entity.VotePK;
import pb.persistence.impl.PbStorageImpl;

/**
 * TestCase for the PB Storage Implementation
 * 
 * @author oliver
 */
public class PBStorageTestCase extends TestCase {
	protected TestPersistenceProvider persistenceProvider = null;
	protected TestPbStorageImpl testee = null;

	@Override
	protected void setUp() throws Exception {
		testee = new TestPbStorageImpl();
		persistenceProvider = new TestPersistenceProvider();
		testee.bind(persistenceProvider);
	}

	@Override
	protected void tearDown() throws Exception {
		testee.unbind(persistenceProvider);
	}

	/**
	 * Test method for
	 * {@link pb.persistence.impl.PbStorageImpl#storeCandidate(pb.persistence.entity.Candidate)}
	 * .
	 */
	public void testStoreCandidate() {
		final int id = 1;
		final BufferedImage image = new BufferedImage(160, 160, BufferedImage.TYPE_3BYTE_BGR);
		final String name = "Peter";
		final int age = 36;
		final String shortDescription = "Peter is really a nice guy";
		final Gender gender = Gender.MALE;
		final Candidate candidate = new Candidate(id, image, name, age, shortDescription, gender);
		assertTrue("Candidate was not stored corectly", testee.storeCandidate(candidate));

		final Candidate comparee = testee.getCandidate(id);
		assertEquals(name, comparee.getName());
		assertEquals(gender, comparee.getGender());
		assertEquals(age, comparee.getAge());
		assertEquals(shortDescription, comparee.getShortDescription());
	}

	/**
	 * Test method for
	 * {@link pb.persistence.impl.PbStorageImpl#storeCandidate(pb.persistence.entity.Candidate)}
	 * .
	 */
	public void testStoreCandidate2() {
		final int id = 2;
		final BufferedImage image = new BufferedImage(160, 160, BufferedImage.TYPE_3BYTE_BGR);
		final String name = "Regine";
		final int age = 19;
		final String shortDescription = "Regine is really a nice girl";
		final Gender gender = Gender.FEMALE;
		final Candidate candidate = new Candidate(id, image, name, age, shortDescription, gender);
		assertTrue("Candidate was not stored corectly", testee.storeCandidate(candidate));

		final Candidate comparee = testee.getCandidate(id);
		assertEquals(name, comparee.getName());
		assertEquals(gender, comparee.getGender());
		assertEquals(age, comparee.getAge());
		assertEquals(shortDescription, comparee.getShortDescription());
	}

	/**
	 * Test method for
	 * {@link pb.persistence.impl.PbStorageImpl#storeCredit(pb.persistence.entity.Credit)}
	 */
	public void testStoreCredit() {
		final int id = 1;
		final int amount = 25;

		final Credit credit = new Credit(id, amount);

		testee.storeCredit(credit);

		final Credit compare = testee.getCredit(id);

		assertNotNull(compare);
		assertEquals(credit.getCandidateId(), compare.getCandidateId());
		assertEquals(credit.getCredit(), compare.getCredit());
	}

	/**
	 * Test method for
	 * {@link pb.persistence.impl.PbStorageImpl#storeVote(pb.persistence.entity.Vote)}
	 */
	public void testStoreVotes() {
		final int testSize = 20;
		final int candidateId = 1;
		final int maxLikes = 5;
		final Vote votes[] = new Vote[testSize];
		for (int i = 0; i < testSize; ++i) {
			Vote vote;
			final int otherId = candidateId + 1 + i;
			if (Math.random() > 0.5) {
				vote = new Vote(candidateId, otherId);
			} else {
				vote = new Vote(otherId, candidateId);
			}
			votes[i] = vote;
			testee.storeVote(vote);
		}

		for (int i = 0; i < testSize; ++i) {
			final Vote vote = testee.getVote(new VotePK(votes[i].getVoterId(), votes[i]
					.getVoteFor()));
			assertNotNull(vote);
			vote.setLikes((int) (maxLikes * Math.random()));
			votes[i].setLikes(vote.getLikes());
			testee.storeVote(vote);
		}

		final List<Vote> candidateVotes = testee.getVotesByCandidateId(candidateId);
		for (final Vote vote : candidateVotes) {
			assertEquals(vote.getVoterId(), candidateId);
			final int voteNumber = vote.getVoteFor() - candidateId - 1;
			assertEquals(votes[voteNumber].getLikes(), vote.getLikes());
		}

		final List<Vote> otherVotes = testee.getVotesForCandidateId(candidateId);
		for (final Vote vote : otherVotes) {
			assertEquals(vote.getVoteFor(), candidateId);
			final int voteNumber = vote.getVoterId() - candidateId - 1;
			assertEquals(votes[voteNumber].getLikes(), vote.getLikes());
		}
	}

	public void testClientId() {
		final int first = testee.getId("First");
		final int second = testee.getId("Second");
		assertNotSame(first, second);
		assertEquals(first, testee.getId("First"));
		assertEquals(second, testee.getId("Second"));
	}

	/**
	 * Test Implementation of the PersistenceProvider
	 * 
	 * @author oliver
	 */
	protected final class TestPersistenceProvider implements PersistenceProvider {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.persistence.spi.PersistenceProvider#createEntityManagerFactory
		 * (java.lang.String, java.util.Map)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public EntityManagerFactory createEntityManagerFactory(final String emName, final Map map) {
			return Persistence.createEntityManagerFactory(emName);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.persistence.spi.PersistenceProvider#
		 * createContainerEntityManagerFactory
		 * (javax.persistence.spi.PersistenceUnitInfo, java.util.Map)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public EntityManagerFactory createContainerEntityManagerFactory(
				final PersistenceUnitInfo info, final Map map) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.persistence.spi.PersistenceProvider#getProviderUtil()
		 */
		@Override
		public ProviderUtil getProviderUtil() {
			return null;
		}
	}

	/**
	 * Helper class that makes protected methods from PbStorageImpl accessable.
	 * 
	 * @author oliver
	 */
	protected final class TestPbStorageImpl extends PbStorageImpl {

		/*
		 * (non-Javadoc)
		 * 
		 * @see pb.persistence.impl.PbStorageImpl#bind(javax.persistence
		 * .spi.PersistenceProvider)
		 */
		@Override
		public void bind(final PersistenceProvider provider) {
			super.bind(provider);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see pb.persistence.impl.PbStorageImpl#unbind(javax.persistence
		 * .spi.PersistenceProvider)
		 */
		@Override
		public void unbind(final PersistenceProvider provider) {
			super.unbind(provider);
		}
	}
}
