package com.alstom.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.alstom.connection.EntityManagerInitializer;
import com.alstom.model.ResStock;

public class ResStockService {
	private EntityManager em = EntityManagerInitializer.getEntityManager();

	public List<ResStock> getResStock() {
		Query query = em.createQuery("SELECT rs FROM ResStock rs");
		try {
			return query.getResultList();
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}

	public ResStock getResStock(String nom_res_stock) {
		TypedQuery<ResStock> query = em.createQuery("SELECT rs FROM ResStock rs where rs.nom = :nom_res_stock",
				ResStock.class);
		query.setParameter("nom_res_stock", nom_res_stock);
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}

	public void save(ResStock resStock) {
		em.getTransaction().begin();
		em.persist(resStock);
		em.getTransaction().commit();
	}

	public void save(List<ResStock> resStock) {
		if (resStock == null || resStock.isEmpty())
			return;

		em.getTransaction().begin();

		resStock.stream().forEach(rs -> {
			em.persist(rs);
		});

		em.getTransaction().commit();
	}
}
