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
package pb.party.sim.impl;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Dictionary;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.References;
import org.osgi.service.component.ComponentContext;

import pb.model.FavoriteModel;
import pb.model.FavoriteModelException;
import pb.model.FavoriteModel.Voting;
import pb.persistence.PbStorage;
import pb.persistence.entity.Candidate;
import pb.persistence.entity.Candidate.Gender;

/**
 * This component simulates a party with several Partitions
 * 
 * @author oliver
 */
@Component(name = "pb.party.sim", label = "PB PartySimulation", immediate = true, metatype = true)
@References({
		@Reference(name = "PBStorage", bind = "bind", unbind = "unbind", referenceInterface = PbStorage.class, cardinality = ReferenceCardinality.MANDATORY_UNARY, policy = ReferencePolicy.DYNAMIC),
		@Reference(name = "FavoritModel", bind = "bind", unbind = "unbind", referenceInterface = FavoriteModel.class, cardinality = ReferenceCardinality.MANDATORY_UNARY, policy = ReferencePolicy.DYNAMIC) })
@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public class PartySimulation {

	protected static final int defaultNumberOfCandidates = 20;
	protected static final int defaultNumberOfTimer = 10;

	@Property(intValue = defaultNumberOfCandidates)
	public static final String NUMBER_OF_CANDIDATES_NAME = "NumberOfCandidates";
	protected int numberOfCandidates = defaultNumberOfCandidates;
	protected PbStorage storage = null;
	protected FavoriteModel model = null;
	protected Timer timers[] = new Timer[defaultNumberOfTimer];
	protected Random random = new Random(System.currentTimeMillis());

	/**
	 * DS activate method. This reads all managed configurations and starts the
	 * party
	 * 
	 * @param context
	 */
	@Activate
	protected void activate(final ComponentContext context) {
		final Dictionary<?, ?> props = context.getProperties();
		numberOfCandidates = readInteger(props, NUMBER_OF_CANDIDATES_NAME,
				defaultNumberOfCandidates);
		for (int i = 0; i < timers.length; i++) {
			timers[i] = new Timer(true);
		}

		for (int i = 0; i < defaultNumberOfCandidates; ++i) {
			final int id = storage.getId("PartySim_" + i);
			final RegisterPartyMember member = new RegisterPartyMember(i % 2 == 0 ? Gender.MALE
					: Gender.FEMALE, id);
			scheduleTask(member, random.nextInt(5));
		}
	}

	/**
	 * DS modified Method. When Components are reconfigured during runtime, they
	 * would be stopped and restarted to read the new configuration, when they
	 * don't have a modified method. This Party shall not stop, when
	 * configuration is changed, it shall simply go on. That's why this method
	 * is empty.
	 * 
	 * @param context
	 */
	@Modified
	protected void modified(final ComponentContext context) {
		// this component can't be modified. It's needed to have this method
		// because otherwise SCR will stop the component when one of it's
		// settings is changed.
	}

	/**
	 * DS deactivate method, simply stops the party.
	 * 
	 * @param context
	 */
	@Deactivate
	protected void deactivate(final ComponentContext context) {
		for (int i = 0; i < timers.length; i++) {
			timers[i].cancel();
			timers[i] = null;
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

	/**
	 * Bind method for PBStorage Service
	 * 
	 * @param storage
	 */
	protected void bind(final PbStorage storage) {
		this.storage = storage;
	}

	/**
	 * Unbind method for PBStorage Service
	 * 
	 * @param storage
	 */
	protected void unbind(final PbStorage storage) {
		if (storage.equals(this.storage)) {
			this.storage = null;
		}
	}

	/**
	 * Bind method for FavoriteModel Service
	 * 
	 * @param model
	 */
	protected void bind(final FavoriteModel model) {
		this.model = model;
	}

	/**
	 * Unbind method for FavoriteModel Service
	 * 
	 * @param model
	 */
	protected void unbind(final FavoriteModel model) {
		if (model.equals(this.model)) {
			this.model = null;
		}
	}

	/**
	 * Schedules a task in random 0 to given amount minutes
	 * 
	 * @param task
	 *            task to schedule
	 * @param minutes
	 *            maximum time in minutes the execution may wait
	 */
	protected void scheduleTask(final TimerTask task, final int minutes) {
		final Timer timer = timers[random.nextInt(timers.length)];
		if (timer != null) {
			timer.schedule(task, Math.max(5, minutes * 6000));
		}
	}

	/**
	 * this simulates a registration of a party member
	 * 
	 * @author oliver
	 */
	protected class RegisterPartyMember extends TimerTask {
		protected final Gender gender;
		protected final int id;

		/**
		 * Constructor taking gender and id of party member
		 * 
		 * @param gender
		 * @param id
		 */
		public RegisterPartyMember(final Gender gender, final int id) {
			super();
			this.gender = gender;
			this.id = id;
		}

		/**
		 * Creates a random integer Value between 0 and given value included
		 * 
		 * @param max
		 *            max value included in possible random results
		 * @return a random int value
		 */
		protected int randomColor(final int max) {
			return random.nextInt(max + 1);
		}

		/**
		 * Creates a random avatar image
		 * 
		 * @return an avatar image
		 */
		protected BufferedImage getRandomAvatar() {
			final int hairRed = randomColor(255);
			final Color hairColor = new Color(hairRed, randomColor(hairRed), 0);
			final int skinGreenRed = randomColor(255);
			final Color skinColor = new Color(skinGreenRed, skinGreenRed, randomColor(skinGreenRed));
			final Color shirtColor = new Color(randomColor(255), randomColor(255), randomColor(255));
			final Avatar avatar = new Avatar(hairColor, shirtColor, skinColor);
			avatar.drawAvatar(Gender.MALE.equals(gender));
			return avatar.getImage();
		}

		/**
		 * Creates a random avatar name
		 * 
		 * @return a name
		 */
		protected String getRandomName() {
			final String consonant = "BCDFGHJKLMNPQRSTVWXYZ";
			final String vocals = "AEIOU";
			final StringBuilder builder = new StringBuilder(10);
			for (int i = 0; i < 3 + (int) (6 * Math.random()); ++i) {
				if (i % 2 == 0) {
					final int c = random.nextInt(consonant.length());
					builder.append(consonant.substring(c, c + 1));
				} else {
					final int c = random.nextInt(vocals.length());
					builder.append(vocals.substring(c, c + 1));
				}
			}
			if (Gender.FEMALE.equals(gender)) {
				builder.append("A");
			} else {
				builder.append("ER");
			}
			return builder.toString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			try {
				Candidate candidate = storage.getCandidate(id);
				if (candidate == null) {
					candidate = new Candidate();
					candidate.setId(id);
					candidate.setShortDescription("");
				}
				candidate.setName(getRandomName());
				candidate.setImage(getRandomAvatar());
				candidate.setAge(18 + random.nextInt(20));
				candidate.setGender(gender);
				storage.storeCandidate(candidate);
				scheduleTask(new VotePartyMember(gender, id), 1 + random.nextInt(5));
			} catch (final Throwable e) {
				Log.error("Error int Party Member timer Task", e);
			}
		}
	}

	/**
	 * This simulates the voting of a PartyMember
	 * 
	 * @author oliver
	 */
	protected class VotePartyMember extends TimerTask {
		protected final Gender gender;
		protected final int id;

		public VotePartyMember(final Gender gender, final int id) {
			super();
			this.gender = gender;
			this.id = id;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			try {
				boolean pair = false;
				final List<Voting> votings = model.getVotings(id);
				// check for pair
				for (final Voting voting : votings) {
					pair |= voting.isPair();
				}
				if (!pair) {
					final int rand = random.nextInt(votings.size() + 1);
					// when random is bigger or equal to voting size, select a
					// new Favorite from list of Candidates, Otherwise vote for
					// one of the favorites
					if (votings.size() <= rand) {
						final List<Candidate> candidates = storage.getCandidateByGender(Gender.MALE
								.equals(gender) ? Gender.FEMALE : Gender.MALE);
						if (!candidates.isEmpty()) {
							final Candidate theOne = candidates.get(random.nextInt(candidates
									.size()));
							model.voteForCandidate(id, theOne.getId());
							Log.debug("New vote ID %d votes for %d", id, theOne.getId());
						}
					} else {
						final Voting voting = votings.get(rand);
						model.voteForCandidate(id, voting.getOtherId());
						Log.debug("ID %d votes for %d", id, voting.getOtherId());
					}
					scheduleTask(new VotePartyMember(gender, id), random.nextInt(5));
				}
			} catch (final FavoriteModelException e) {
				Log.error("Error occured while voting", e);
			}
		}
	}
}
