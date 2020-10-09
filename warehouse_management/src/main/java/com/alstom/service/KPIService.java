package com.alstom.service;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.alstom.connection.EntityManagerInitializer;
import com.alstom.model.EtatKit;

public class KPIService {

	private EntityManager em = EntityManagerInitializer.getEntityManager();

	public Long getKitsCount() {

		Query query = em.createQuery("SELECT count(k) FROM Kit k ");
		try {
			return (Long) query.getSingleResult();
		} catch (Exception e) {
			System.err.println(e);
			return 0L;
		}

	}

	public Long getKitsEnStockCount() {
		Query query = em.createQuery("SELECT count(k) FROM Kit k WHERE k.etat = :etat");
		query.setParameter("etat", EtatKit.ENSTOCK);
		try {
			return (Long) query.getSingleResult();
		} catch (Exception e) {
			System.err.println(e);
			return 0L;
		}

	}

}
