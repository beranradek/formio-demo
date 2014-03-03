package com.examples.forms.domain;

import java.io.Serializable;

import javax.validation.constraints.Pattern;

import org.twinstone.formio.binding.ArgumentName;

public class Address implements Serializable {
	private static final long serialVersionUID = 2293884142466950281L;
	private String street;
	private String city;
	
	@Pattern(message="{constraints.psc.invalid}", regexp="(|\\d{5})") // empty or 5 digits
	private String zipCode;
	
	public static Address getInstance(
		@ArgumentName("street") String street, 
		@ArgumentName("city") String city, 
		@ArgumentName("zipCode") String zipCode) {
		return new Address(street, city, zipCode);
	}
	
	private Address(String street, String city, String zipCode) {
		this.street = street;
		this.city = city;
		this.zipCode = zipCode;
	}
	
	public boolean isEmpty() {
		return isEmptyString(street);
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
	private boolean isEmptyString(String str) {
		return str == null || str.isEmpty();
	}

}
