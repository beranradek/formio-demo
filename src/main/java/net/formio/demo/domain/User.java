package net.formio.demo.domain;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 8709892388361993955L;
	private String login;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

}
