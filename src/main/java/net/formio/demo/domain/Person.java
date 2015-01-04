package net.formio.demo.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import net.formio.binding.ArgumentName;
import net.formio.validation.SeverityPayload;
import net.formio.validation.constraints.NotEmpty;

public class Person implements Serializable {
	private static final long serialVersionUID = -1568960763694971728L;
	private long personId;
	
	private final String firstName;

	@NotEmpty
	private final String lastName;

	@Min(value=8000, payload=SeverityPayload.Warning.class)
	private int salary;

	@NotEmpty
	@Pattern(regexp="\\d{9}")
	private String phone;
	private boolean male;
	private Date birthDate;
	private Nation nation;

	public Person(
		@ArgumentName("firstName") String firstName,
		@ArgumentName("lastName") String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public long getPersonId() {
		return personId;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public int getSalary() {
		return salary;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public boolean isMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Nation getNation() {
		return nation;
	}

	public void setNation(Nation nation) {
		this.nation = nation;
	}

}
