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
package pb.persistence.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.spi.PersistenceProvider;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;

import pb.persistence.PbStorage;
import pb.persistence.entity.Candidate;
import pb.persistence.entity.Candidate.Gender;
import pb.persistence.entity.ClientId;
import pb.persistence.entity.Credit;
import pb.persistence.entity.Vote;
import pb.persistence.entity.VotePK;

/**
 * Implementation of the storage service as a service component.
 * 
 * @author oliver
 */
@Component(name = "pb.persistence", label = "PB Storage", immediate = true, metatype = false)
@Service(value = PbStorage.class)
@Reference(name = "PersistenceProvider", bind = "bind", unbind = "unbind", referenceInterface = PersistenceProvider.class, cardinality = ReferenceCardinality.MANDATORY_UNARY, policy = ReferencePolicy.DYNAMIC)
@SuppressWarnings("unchecked")
public class PbStorageImpl implements PbStorage {

	protected EntityManagerFactory entityMgrFactory = null;
	protected PersistenceProvider provider = null;
	protected Semaphore providerSem = new Semaphore(0);
	protected static final int MAX_ACCESS_COUNT = 100;
	protected Lock idLock = new ReentrantLock();

	/**
	 * Bind method for the PersistenceProvider. This method creates the
	 * EntityManagerFactory.
	 * 
	 * @param provider
	 *            A PersistenceProvider available in the System
	 */
	protected void bind(final PersistenceProvider provider) {
		try {
			this.provider = provider;
			entityMgrFactory = provider.createEntityManagerFactory("PbPersistence", null);
			providerSem.release(MAX_ACCESS_COUNT);
		} catch (final Throwable thr) {
			Log.error("Error while binding Provider", thr);
			this.provider = null;
		}
	}

	/**
	 * Unbind method for the PersistenceProvider. This method closes the
	 * EntityManagerFactory, since it is no longer needed.
	 * 
	 * @param provider
	 *            The provider that is removed from the system.
	 */
	protected void unbind(final PersistenceProvider provider) {
		if (provider.equals(this.provider)) {
			try {
				providerSem.acquire(MAX_ACCESS_COUNT);
			} catch (final InterruptedException e) {
				Log.error("Error while unbinding Provider", e);
			}
			entityMgrFactory.close();
			this.provider = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.persistence.PbStorage#getCandidate(int)
	 */
	@Override
	public Candidate getCandidate(final int id) {
		Candidate ret = null;
		if (providerSem.tryAcquire()) {
			try {
				final EntityManager em = entityMgrFactory.createEntityManager();
				ret = em.find(Candidate.class, id);
				if (ret != null) {
					Log.debug("Candidate Requested ID = %d, Name = %s", ret.getId(), ret.getName());
				}
				em.close();
			} finally {
				providerSem.release();
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.persistence.PbStorage#storeCandidate(pb.persistence
	 * .entity.Candidate)
	 */
	@Override
	public boolean storeCandidate(final Candidate candidate) {
		boolean ret = true;
		if (providerSem.tryAcquire()) {
			try {
				final EntityManager em = entityMgrFactory.createEntityManager();
				em.getTransaction().begin();
				em.merge(candidate);
				em.getTransaction().commit();
				em.close();
			} catch (final Throwable thr) {
				Log.error("Error storing Candidate", thr);
				ret = false;
			} finally {
				providerSem.release();
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.persistence.PbStorage#getCandidateByGender(pb.
	 * persistence.entity.Candidate.Gender)
	 */
	@Override
	public List<Candidate> getCandidateByGender(final Gender gender) {
		List<Candidate> ret = new ArrayList<Candidate>();
		if (providerSem.tryAcquire()) {
			try {
				final EntityManager em = entityMgrFactory.createEntityManager();
				final Query query = em
						.createQuery("from Candidate x where x.gender = :gender and length(x.name) > 0 and x.available = true order by x.id");
				query.setParameter("gender", gender);
				ret = query.getResultList();
				em.close();
			} catch (final Throwable thr) {
				Log.error("Error Reading Candidates by Gender", thr);
			} finally {
				providerSem.release();
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
		Credit ret = null;
		if (providerSem.tryAcquire()) {
			try {
				final EntityManager em = entityMgrFactory.createEntityManager();
				ret = em.find(Credit.class, id);
				if (ret != null) {
					Log.debug("Credit Requested ID = %d, Credit = %d", ret.getCandidateId(),
							ret.getCredit());
				}
				em.close();
			} finally {
				providerSem.release();
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.persistence.PbStorage#storeCredit(pb.persistence .entity.Credit)
	 */
	@Override
	public boolean storeCredit(final Credit credit) {
		boolean ret = true;
		if (providerSem.tryAcquire()) {
			try {
				final EntityManager em = entityMgrFactory.createEntityManager();
				em.getTransaction().begin();
				em.merge(credit);
				em.getTransaction().commit();
				em.close();
			} catch (final Throwable thr) {
				Log.error("Error storing Credit", thr);
				ret = false;
			} finally {
				providerSem.release();
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.persistence.PbStorage#getVotesByCandidateId(int)
	 */
	@Override
	public List<Vote> getVotesByCandidateId(final int id) {
		List<Vote> ret = new ArrayList<Vote>();
		if (providerSem.tryAcquire()) {
			try {
				final EntityManager em = entityMgrFactory.createEntityManager();
				final Query query = em
						.createQuery("from Vote x where x.voterId = :id order by likes desc");
				query.setParameter("id", id);
				ret = query.getResultList();
				em.close();
			} catch (final Throwable thr) {
				Log.error("Error Reading Votes by Voter Id", thr);
			} finally {
				providerSem.release();
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
		List<Vote> ret = new ArrayList<Vote>();
		if (providerSem.tryAcquire()) {
			try {
				final EntityManager em = entityMgrFactory.createEntityManager();
				final Query query = em
						.createQuery("from Vote x where x.voteFor = :id order by likes desc");
				query.setParameter("id", id);
				ret = query.getResultList();
				em.close();
			} catch (final Throwable thr) {
				Log.error("Error Reading Votes for Candidate Id", thr);
			} finally {
				providerSem.release();
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.persistence.PbStorage#storeVote(pb.persistence.entity .Vote)
	 */
	@Override
	public boolean storeVote(final Vote vote) {
		boolean ret = true;
		if (providerSem.tryAcquire()) {
			try {
				final EntityManager em = entityMgrFactory.createEntityManager();
				em.getTransaction().begin();
				em.merge(vote);
				em.getTransaction().commit();
				em.close();
			} catch (final Throwable thr) {
				Log.error("Error storing Vote", thr);
				ret = false;
			} finally {
				providerSem.release();
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.persistence.PbStorage#getVote(pb.persistence.entity .VotePK)
	 */
	@Override
	public Vote getVote(final VotePK pk) {
		Vote ret = null;
		if (providerSem.tryAcquire()) {
			try {
				final EntityManager em = entityMgrFactory.createEntityManager();
				ret = em.find(Vote.class, pk);
				if (ret != null) {
					Log.debug("Vote Requested CandID = %d, VoteFor = %d, Likes = %d",
							ret.getVoterId(), ret.getVoteFor(), ret.getLikes());
				}
				em.close();
			} finally {
				providerSem.release();
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.persistence.PbStorage#removeAllVotes(int)
	 */
	@Override
	public boolean removeAllVotes(final int id) {
		boolean ret = true;
		if (providerSem.tryAcquire()) {
			try {
				final EntityManager em = entityMgrFactory.createEntityManager();
				em.getTransaction().begin();
				final Query query = em.createQuery("from Vote x where x.voterId = :id");
				query.setParameter("id", id);
				final List<Vote> removes = query.getResultList();
				final Query query2 = em.createQuery("from Vote x where x.voteFor = :id");
				query2.setParameter("id", id);
				removes.addAll(query2.getResultList());
				for (final Vote vote : removes) {
					em.remove(vote);
				}
				em.getTransaction().commit();
				em.close();
			} catch (final Throwable thr) {
				Log.error("Error removing All Votes for candidate", thr);
				ret = false;
			} finally {
				providerSem.release();
			}
		}
		return ret;
	}

	/**
	 * This creates a new ClientId in a threadSafe method.
	 * 
	 * @param ip
	 *            the ip of the requester that is still unknown
	 * @return the fresh created unique ClientId
	 */
	protected ClientId createId(final String ip) {
		ClientId ret = null;
		idLock.lock();
		try {
			final EntityManager em = entityMgrFactory.createEntityManager();
			em.getTransaction().begin();
			// First check whether the id wasn't created in the meanwhile
			final Query query = em.createQuery("from ClientId x where x.ip = :ip");
			query.setParameter("ip", ip);
			final List<ClientId> ids = query.getResultList();
			if (ids.size() >= 1) {
				ret = ids.get(0);
			} else {
				// Id doesn't exist so create a new one
				ret = new ClientId(ip);
				ret = em.merge(ret);
			}
			em.getTransaction().commit();
		} catch (final Throwable thr) {
			Log.error("Error getting an id for %s", thr, ip);
		} finally {
			idLock.unlock();
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pb.persistence.PbStorage#getId(java.lang.String)
	 */
	@Override
	public int getId(final String ip) {
		int ret = -1;
		if (providerSem.tryAcquire()) {
			try {
				ClientId clientId = null;
				final EntityManager em = entityMgrFactory.createEntityManager();
				final Query query = em.createQuery("from ClientId x where x.ip = :ip");
				query.setParameter("ip", ip);
				final List<ClientId> ids = query.getResultList();
				if (ids.size() > 1) {
					Log.error("there are more than one entries for this ip %s ", ip);
					clientId = ids.get(0);
				} else if (ids.size() == 1) {
					clientId = ids.get(0);
				} else {
					clientId = createId(ip);
				}
				ret = clientId.getId();
			} catch (final Throwable thr) {
				Log.error("Error getting an id for %s", thr, ip);
			} finally {
				providerSem.release();
			}
		}
		return ret;
	}
}
