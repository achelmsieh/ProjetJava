package com.alstom.connection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerInitializer {

	private static EntityManagerFactory emFactory;
	private static EntityManager entityManager;

	public static EntityManager init() {
		emFactory = Persistence.createEntityManagerFactory("GscUnit");
		entityManager = emFactory.createEntityManager();

		return getEntityManager();
	}

	public static EntityManager getEntityManager() {
		if (entityManager == null || emFactory == null) {
			init();
		}

		return entityManager;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();

		closeConnection();
	}

	public void closeConnection() {
		getEntityManager().close();
		emFactory.close();
	}

}
