package rest;

import java.util.*;

public class DtuPayUserRepresentation {
	
	private String cpr;
	private String firstName;
	private String lastName;
	private String account;

	public Set<UUID> getUuid() {
		return uuid;
	}

	public void setUuid(Set<UUID> uuid) {
		this.uuid = uuid;
	}

	private Set<UUID> uuid = new HashSet<>();

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
		for (UUID token : uuid){
			String stringToken = token.toString();
			uuid.remove(token);
			return stringToken;
		}
		return "null";
	}

	public void deleteAllTokens(){
		this.uuid = new HashSet<>();
	}
}
