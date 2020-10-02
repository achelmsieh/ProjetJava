package com.alstom.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.alstom.connection.EntityManagerInitializer;
import com.alstom.model.ResProduction;

public class ResProductionService {
	private EntityManager em = EntityManagerInitializer.getEntityManager();

	public List<ResProduction> getResProduction() {

		Query query = em.createQuery("SELECT rp FROM ResProduction rp");
		try {
			return query.getResultList();
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}

	public ResProduction getResProduction(String nom) {

		Query query = em.createQuery("SELECT rp FROM ResProduction rp WHERE rp.nom = :nom");
		query.setParameter("nom", nom);

		try {
			return (ResProduction) query.getSingleResult();
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}

	public void save(ResProduction resProduction) {
		em.getTransaction().begin();
		em.persist(resProduction);
		em.getTransaction().commit();
	}

	public void save(List<ResProduction> resProduction) {
		if (resProduction == null || resProduction.isEmpty())
			return;

		em.getTransaction().begin();

		resProduction.stream().forEach(rp -> {
			em.persist(rp);
		});

		em.getTransaction().commit();
	}
}
