package dtu;

public class DtuPayUserRepresentation {
	
	private String cpr;
	private String firstName;
	private String lastName;
	private String account;

	public String getCpr() {
		return cpr;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getAccount() {
		return account;
	}

	public void setCpr(String cpr) {
		this.cpr = cpr;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setAccount(String account) {
		this.account = account;
	}

}
