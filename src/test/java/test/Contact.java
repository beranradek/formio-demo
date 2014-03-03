package test;

public class Contact {
	private String street;
	private int number;

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return "Contact [street=" + street + ", number=" + number + "]";
	}

}