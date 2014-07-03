package net.formio.demo.domain;

import java.io.Serializable;

import net.formio.validation.constraints.Email;

public class User implements Serializable {
	private static final long serialVersionUID = 8709892388361993955L;
	private String login;

	private String firstName;

	private String lastName;

	@Email
	private String email;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
