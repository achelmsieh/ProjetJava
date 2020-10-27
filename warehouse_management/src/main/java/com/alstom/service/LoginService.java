package com.alstom.service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.alstom.connection.EntityManagerInitializer;
import com.alstom.model.Personnel;
import com.alstom.model.enums.PersonnelRole;

public class LoginService {

	public Personnel connecting(String username, String password) {
		Personnel rslt = null;
		EntityManager em = EntityManagerInitializer.getEntityManager();

		TypedQuery<Personnel> query = em.createQuery(
				"SELECT p FROM Personnel p WHERE (p.role = :admin OR p.role = :res_stock) AND p.matricule = :matricule AND p.motDePasse = :pass",
				Personnel.class);
		query.setParameter("admin", PersonnelRole.ADMIN);
		query.setParameter("res_stock", PersonnelRole.RES_STOCK);
		query.setParameter("matricule", username);
		query.setParameter("pass", password);

		try {
			rslt = query.getSingleResult();
		} catch (Exception e) {
			System.out.println(e);
		}

		return rslt;
	}

}
