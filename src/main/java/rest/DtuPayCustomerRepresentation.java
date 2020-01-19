package rest;

import java.util.*;

public class DtuPayCustomerRepresentation {
	
	private String cpr;
	private String firstName;
	private String lastName;
	private String account;

	public List<UUID> getCustomerTokens() {
		return customerTokens;
	}

	public void setCustomerTokens(List<UUID> customerTokens) {
		this.customerTokens = customerTokens;
	}

	private List<UUID> customerTokens;

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

	public String StringToken(){
		for (UUID token : customerTokens){
			String stringToken = token.toString();
			customerTokens.remove(token);
			return stringToken;
		}
		return "null";
	}

	public void deleteAllTokens(){
		this.customerTokens = new ArrayList<>();
	}
}
