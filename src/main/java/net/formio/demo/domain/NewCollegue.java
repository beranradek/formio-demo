package net.formio.demo.domain;

import java.io.Serializable;
import java.util.Calendar;

import net.formio.validation.constraints.Email;
import net.formio.validation.constraints.NotEmpty;

public class NewCollegue implements Serializable {
	private static final long serialVersionUID = 3829658946967880236L;

	public NewCollegue() {
		Calendar cal = Calendar.getInstance();
		this.regDate = new RegDate(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
	}

	public interface New {
		/** marker interface */
	}

	@NotEmpty(groups = New.class)
	private String name;

	@NotEmpty(groups = New.class)
	@Email(groups = New.class)
	private String email;

	private RegDate regDate;

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

	public Collegue toCollegue() {
		Collegue c = new Collegue();
		c.setName(this.name);
		c.setEmail(this.email);
		c.setRegDate(this.regDate != null ? new RegDate(this.regDate.getMonth(), this.regDate.getYear()) : null);
		return c;
	}

}
