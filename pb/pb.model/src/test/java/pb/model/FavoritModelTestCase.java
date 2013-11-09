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
package pb.model;

import java.util.Hashtable;
import java.util.List;

import junit.framework.TestCase;

import org.apache.felix.scr.annotations.Activate;
import org.osgi.service.component.ComponentContext;

import pb.model.FavoriteModelException;
import pb.model.FavoriteModel.Voting;
import pb.model.help.TestComponentContext;
import pb.model.help.TestPBStorage;
import pb.model.impl.FavoritModelImpl;
import pb.persistence.PbStorage;
import pb.persistence.entity.Credit;

/**
 * @author oliver
 * 
 */
public class FavoritModelTestCase extends TestCase {

	protected TestFavoriteModel favoriteModel;
	protected TestComponentContext componentContext;
	protected TestPBStorage storage;
	protected int numberOfCandidates = 20;
	protected int maxLikes = 3;
	protected int numberOfCredits = 100;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final Hashtable<String, Object> props = new Hashtable<>();
		props.put(TestFavoriteModel.MAX_LIKES_NAME, maxLikes);
		props.put(TestFavoriteModel.NUMBER_OF_CANDIDATES_NAME, numberOfCandidates);
		props.put(TestFavoriteModel.START_CREDITS_NAME, numberOfCredits);
		favoriteModel = new TestFavoriteModel();
		componentContext = new TestComponentContext(props);
		storage = new TestPBStorage();
		favoriteModel.bind(storage);
		favoriteModel.activate(componentContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		favoriteModel.unbind(storage);
	}

	/**
	 * Test method for
	 * {@link pb.model.impl.FavoritModelImpl#voteForCandidate(int, int)}
	 * .
	 * 
	 * @throws FavoriteModelException
	 */
	public void testVoteForCandidate() throws FavoriteModelException {
		final int candidateId = 4;
		final int otherId = 4;
		// First the candidate votes for all other candidates
		for (int i = 0; i < numberOfCandidates; ++i) {
			if (i != candidateId) {
				favoriteModel.voteForCandidate(candidateId, i);
			}
		}
		// Now we should have numberOfCandidates - 1 votings
		List<Voting> votings = favoriteModel.getVotings(candidateId);
		assertEquals(numberOfCandidates - 1, votings.size());

		// Now each other candidate vote for the candidate
		for (int i = 0; i < numberOfCandidates; ++i) {
			if (i != candidateId) {
				favoriteModel.voteForCandidate(i, candidateId);
			}
		}
		// we still should have numberOfCandidates - 1 votings
		votings = favoriteModel.getVotings(candidateId);
		assertEquals(numberOfCandidates - 1, votings.size());

		// each voting should have 1 like in each direction

		for (final Voting voting : votings) {
			assertEquals(1, voting.ownLikes);
			assertEquals(1, voting.otherLikes);
		}

		// Now lets two candidates find each other

		for (int i = 1; i < maxLikes; i++) {
			favoriteModel.voteForCandidate(candidateId, otherId);
			favoriteModel.voteForCandidate(otherId, candidateId);
		}

		// there should only be one Vote left for both of the new pair
		votings = favoriteModel.getVotings(candidateId);
		assertEquals(1, votings.size());
		// Pair flag should be set
		assertTrue(votings.get(0).isPair());

		votings = favoriteModel.getVotings(otherId);
		assertEquals(1, votings.size());
		// Pair flag should be set
		assertTrue(votings.get(0).isPair());

		// and they shouldn't have any credits left

		Credit credit = storage.getCredit(candidateId);
		assertEquals(0, credit.getCredit());

		credit = storage.getCredit(otherId);
		assertEquals(0, credit.getCredit());
	}

	protected static class TestFavoriteModel extends FavoritModelImpl {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * pb.model.impl.FavoritModelImpl#activate(org.osgi.service
		 * .component.ComponentContext)
		 */
		@Override
		@Activate
		public void activate(final ComponentContext context) {
			super.activate(context);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * pb.model.impl.FavoritModelImpl#bind(pb.persistence
		 * .PbStorage)
		 */
		@Override
		public void bind(final PbStorage storage) {
			super.bind(storage);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * pb.model.impl.FavoritModelImpl#unbind(pb.persistence
		 * .PbStorage)
		 */
		@Override
		public void unbind(final PbStorage storage) {
			super.unbind(storage);
		}
	}
}
