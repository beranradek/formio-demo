package net.formio.demo.domain;

import java.io.Serializable;

import net.formio.validation.constraints.Email;
import net.formio.validation.constraints.NotEmpty;

public class Collegue implements Serializable {
	private static final long serialVersionUID = -9179723481190386954L;

	@NotEmpty
	private String name;

	@NotEmpty
	@Email
	private String email;

	private RegDate regDate;
	
	public Collegue() {
		this.regDate = new RegDate(12, 2014);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public RegDate getRegDate() {
		return regDate;
	}

	public void setRegDate(RegDate regDate) {
		this.regDate = regDate;
	}

}
