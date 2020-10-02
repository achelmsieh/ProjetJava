package com.alstom.service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.alstom.connection.EntityManagerInitializer;
import com.alstom.model.ResStock;

public class LoginService {

	public ResStock connecting(String username, String password) {
		ResStock rslt = null;
		EntityManager em = EntityManagerInitializer.getEntityManager();

		TypedQuery<ResStock> query = em
				.createQuery("SELECT c FROM ResStock c WHERE c.nom = :nom AND c.motDePasse = :pass", ResStock.class);
		query.setParameter("nom", username);
		query.setParameter("pass", password);

		try {
			rslt = query.getSingleResult();
		} catch (Exception e) {
			System.out.println(e);
		}

		return rslt;
	}

}
