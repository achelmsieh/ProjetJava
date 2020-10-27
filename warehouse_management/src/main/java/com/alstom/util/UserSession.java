package com.alstom.util;

import com.alstom.model.Personnel;

public class UserSession {

	private static UserSession instance;
	private Personnel user;

	private UserSession(Personnel user) {
		this.user = user;
	}

	public static UserSession getInstance(Personnel user) {
		if (instance == null) {
			instance = new UserSession(user);
		}
		return instance;
	}

	public static Personnel getUser() {
		return instance.user;
	}

	public static void cleanUserSession() {
		instance = null;
	}

	@Override
	public String toString() {
		return "UserSession [user=" + user + "]";
	}

}
