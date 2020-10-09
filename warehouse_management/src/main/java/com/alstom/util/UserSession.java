package com.alstom.util;

import com.alstom.model.ResStock;

public class UserSession {

	private static UserSession instance;
	private ResStock user;

	private UserSession(ResStock user) {
		this.user = user;
	}

	public static UserSession getInstance(ResStock user) {
		if (instance == null) {
			instance = new UserSession(user);
		}
		return instance;
	}

	public static ResStock getUser() {
		return instance.user;
	}

	public static void cleanUserSession() {
		instance.user = null;
	}

	@Override
	public String toString() {
		return "UserSession [user=" + user + "]";
	}

}
