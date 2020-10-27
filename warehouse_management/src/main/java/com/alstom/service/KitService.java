package com.alstom.service;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.alstom.connection.EntityManagerInitializer;
import com.alstom.model.Kit;
import com.alstom.model.Personnel;
import com.alstom.model.enums.EtatKit;

public class KitService {

	private EntityManager em = EntityManagerInitializer.getEntityManager();
	private static final int fetchLimit = 50;

	public List<Kit> getKits() {
		Query query = em.createQuery("SELECT k FROM Kit k ORDER BY k.dateEntree DESC");
		try {
			return query.getResultList();
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}

	public List<Kit> getKitsByZone(String zone) {
		Query query = em.createNativeQuery(
				"SELECT * FROM kits k, emplacements e, kits_emplacements ke WHERE k.id = ke.kit_id AND e.id = ke.emplacement_id AND e.coordonnee LIKE :zone",
				Kit.class);
		query.setParameter("zone", zone + "%");
		try {
			return query.getResultList();
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}

	public List<Kit> getKits(int limit) {
		if (limit <= 0)
			limit = fetchLimit;

		Query query = em.createQuery("SELECT k FROM Kit k ORDER BY k.dateEntree DESC").setMaxResults(limit);
		try {
			return query.getResultList();
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}

	}

	public List<Kit> getKits(EtatKit etat) {

		Query query = em.createQuery("SELECT k FROM Kit k WHERE k.etat = :etat ORDER BY k.dateEntree DESC");
		query.setParameter("etat", etat);
		try {
			return query.getResultList();
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}

	}

	public Kit getKitByOF(String OF) {
		Query query = em.createQuery("SELECT k FROM Kit k WHERE k.OF = :OF");
		query.setParameter("OF", OF);
		try {
			return (Kit) query.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}

	}

	public List<Kit> getKitsOrder(Date date) {
		Query query = em.createQuery("SELECT k FROM Kit k WHERE k.etat <> :etat AND k.dateSortiePrevue = :date");
		query.setParameter("etat", EtatKit.PLANNING);
		query.setParameter("date", date);
		try {
			return query.getResultList();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

	public List<Kit> getKitsManquants(Date date) {
		Query query = em.createQuery("SELECT k FROM Kit k WHERE k.etat = :etat AND k.dateSortiePrevue <= :date");
		query.setParameter("etat", EtatKit.PLANNING);
		query.setParameter("date", date);
		try {
			return query.getResultList();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

	public List<Kit> getKitsBloques(Date date) {
		Query query = em.createQuery("SELECT k FROM Kit k WHERE k.etat = :etat AND k.dateSortiePrevue < :date");
		query.setParameter("etat", EtatKit.ENSTOCK);
		query.setParameter("date", date);
		try {
			return query.getResultList();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

	public List<Kit> getKitByIdRes(Personnel res) {
		Query query = em.createQuery("SELECT k FROM Kit k WHERE k.resProduction = :ID");
		query.setParameter("ID", res);
		try {
			return query.getResultList();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}

	}

	public List<Kit> searchKit(String OF) {

		Query query = em
				.createQuery("SELECT k FROM Kit k WHERE k.OF LIKE CONCAT('%',:OF,'%') ORDER BY k.dateEntree DESC");
		query.setParameter("OF", OF);
		try {
			return query.getResultList();
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}

	}

	public void save(Kit kit) {
		em.getTransaction().begin();
		em.persist(kit);
		em.getTransaction().commit();
	}

	public void save(List<Kit> kits) {
		if (kits == null || kits.isEmpty())
			return;

		EntityTransaction tr = em.getTransaction();
		try {
			if (!tr.isActive()) {
				tr.begin();
				kits.stream().forEach(kit -> {
					em.persist(kit);
				});
				tr.commit();
			}
		} catch (Exception e) {
			System.err.println("---------> ROLLBACK <----------");
			tr.rollback();
		}
	}

	public boolean delete(Kit kit) {
		if (kit == null)
			return false;

		EntityTransaction tr = em.getTransaction();
		try {
			if (!tr.isActive()) {
				tr.begin();
				em.remove(kit);
				tr.commit();
				return true;
			}
		} catch (Exception e) {
			tr.rollback();
		}
		return false;
	}

	public void livrerKit(Kit kit) {
		em.getTransaction().begin();

		kit.setEmplacements(null);
		kit.setDateSortie(new Date());
		kit.setEtat(EtatKit.SORTIE);

		em.merge(kit);
		em.getTransaction().commit();
	}

	public void update(Kit kit) {
		em.getTransaction().begin();
		em.merge(kit);
		em.getTransaction().commit();
	}

	public void update(List<Kit> kits) {
		if (kits == null || kits.isEmpty())
			return;

		em.getTransaction().begin();
		kits.stream().forEach(kit -> {
			em.merge(kit);
		});
		em.getTransaction().commit();
	}

	// pour le chart
	public List<Date> getAllDateEntre() {
		Query query = em.createQuery("SELECT k.dateEntree FROM Kit k where k.dateEntree is not null ");
		try {
			return (List<Date>) query.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public List<Kit> getKitsBloques(Date date1, Date date2) {
//		Query query = em.createQuery("SELECT k FROM Kit k WHERE k.dateSortiePrevue < k.dateSortie AND k.dateSortiePrevue BETWEEN :date1 and :date2");
////		query.setParameter("etat", EtatKit.ENSTOCK);
		Query query = em.createQuery(
				"SELECT k FROM Kit k WHERE (k.etat = :etat OR k.dateSortiePrevue < k.dateSortie) AND k.dateSortiePrevue BETWEEN :date1 and :date2");
		query.setParameter("etat", EtatKit.ENSTOCK);
		query.setParameter("date1", date1);
		query.setParameter("date2", date2);
		try {
			System.out.println("Bloquer : " + query.getResultList());
			return query.getResultList();

		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

	public List<Kit> getKitsAnticipe(Date date1, Date date2) {
		Query query = em.createQuery(
				"SELECT k FROM Kit k WHERE k.dateSortiePrevue > k.dateSortie AND k.dateSortiePrevue BETWEEN :date1 and :date2");
//		query.setParameter("etat", EtatKit.ENSTOCK);
		query.setParameter("date1", date1);
		query.setParameter("date2", date2);
		try {
			System.out.println("Anticipe : " + query.getResultList());
			return query.getResultList();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

}
