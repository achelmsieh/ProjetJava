package com.alstom.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.alstom.connection.EntityManagerInitializer;
import com.alstom.model.Emplacement;

public class EmplacementService {

	private EntityManager em = EntityManagerInitializer.getEntityManager();

	public List<Emplacement> getEmplacements() {

		Query query = em.createQuery("SELECT e FROM Emplacement e ORDER BY e.id ASC");
		try {
			return query.getResultList();
		} catch (Exception e) {
			System.err.println(e);
			return null;

		}
	}

	public Emplacement getEmplacement(String coordonnee) {
		Query query = em.createQuery("SELECT e FROM Emplacement e WHERE e.coordonnee=:coord");
		query.setParameter("coord", coordonnee);

		try {
			return (Emplacement) query.getSingleResult();
		} catch (Exception e) {
			System.err.println(e);
			return null;

		}

	}

	public List<Emplacement> getAllZoneEmplacements(String zone) {
		Query query = em.createQuery(
				"SELECT e FROM Emplacement e WHERE e.coordonnee LIKE :coord ORDER BY e.coordonnee");
		query.setParameter("coord", zone + "%");

		try {
			return query.getResultList();
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}

	public Long countAllEmplacement() {
		Query query = em.createQuery("SELECT count(E) FROM Emplacement E");
		try {
			return (Long) query.getSingleResult();
		} catch (Exception e) {
			System.err.println(e);
			return 0L;
		}
	}

	public Long countOccupedEmplacement() {
		Query query = em.createNativeQuery("SELECT COUNT (DISTINCT KE.emplacement_id) FROM kits_emplacements KE");
		try {
			return Long.valueOf(query.getSingleResult().toString());
		} catch (Exception e) {
			System.err.println(e);
			return 0L;
		}
	}

	public void save(Emplacement emp) {
		em.getTransaction().begin();
		em.persist(emp);
		em.getTransaction().commit();
	}

	public void save(List<Emplacement> emps) {
		if (emps == null || emps.isEmpty())
			return;

		em.getTransaction().begin();

		emps.stream().forEach(emp -> {
			em.persist(emp);
		});
		em.getTransaction().commit();
	}

}
