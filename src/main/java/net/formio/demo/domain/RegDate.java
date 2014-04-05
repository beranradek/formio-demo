package net.formio.demo.domain;

import java.io.Serializable;

import net.formio.binding.ArgumentName;

public class RegDate implements Serializable {
	private static final long serialVersionUID = 8798832969912892639L;
	private final int month;
	private final int year;

	public RegDate(
		@ArgumentName("month") int month,
		@ArgumentName("year") int year) {
		this.month = month;
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}

}
