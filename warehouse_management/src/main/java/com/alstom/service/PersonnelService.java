package com.alstom.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.alstom.connection.EntityManagerInitializer;
import com.alstom.model.Personnel;
import com.alstom.model.enums.PersonnelRole;

public class PersonnelService {
	private EntityManager em = EntityManagerInitializer.getEntityManager();

	public List<Personnel> getPersonnels() {
		Query query = em.createQuery("SELECT rs FROM Personnel rs");
		try {
			return query.getResultList();
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}

	public List<Personnel> getPersonnels(PersonnelRole role) {
		Query query = em.createQuery("SELECT rs FROM Personnel rs WHERE rs.role = :role");
		query.setParameter("role", role);
		try {
			return query.getResultList();
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}

	public Personnel getPersonnel(String matricule) {
		TypedQuery<Personnel> query = em.createQuery("SELECT rs FROM Personnel rs where rs.matricule = :matricule",
				Personnel.class);
		query.setParameter("matricule", matricule);
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}

	public void save(Personnel personnel) {
		em.getTransaction().begin();
		em.persist(personnel);
		em.getTransaction().commit();
	}

	public void save(List<Personnel> personnels) {
		if (personnels == null || personnels.isEmpty())
			return;

		em.getTransaction().begin();

		personnels.stream().forEach(rs -> {
			em.persist(rs);
		});

		em.getTransaction().commit();
	}

	public void remove(Personnel personne) {
		if (personne == null)
			return;

		em.getTransaction().begin();
		em.remove(personne);
		em.getTransaction().commit();
	}
}
