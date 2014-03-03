package com.examples.forms.domain;

import java.io.Serializable;

public class Interest implements Serializable {
	private static final long serialVersionUID = -4705858425718466621L;
	private final int interestId;
	private final String name;

	public Interest(int interestId, String name) {
		this.interestId = interestId;
		this.name = name;
	}

	public int getInterestId() {
		return interestId;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + interestId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Interest))
			return false;
		Interest other = (Interest) obj;
		if (interestId != other.interestId)
			return false;
		return true;
	}

}
