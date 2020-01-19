package rest;

import java.util.List;
import java.util.UUID;

public class DtuPayCustomerRepresentation
{
	private String firstName;
	private String lastName;
	private String cprNumber;
	private String accountId;

	public List<UUID> getCustomerTokens() {
		return customerTokens;
	}

	public void setCustomerTokens(List<UUID> customerTokens) {
		this.customerTokens = customerTokens;
	}

	private List<UUID> customerTokens;

	public DtuPayCustomerRepresentation()
	{
		super();
	}

	public DtuPayCustomerRepresentation(String firstName, String lastName, String cprNumber, String accountId)
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.cprNumber = cprNumber;
		this.accountId = accountId;
	}

	public String getFirstName() { return firstName; }
	public String getLastName()
	{
		return lastName;
	}
	public String getCprNumber()
	{
		return cprNumber;
	}
	public String getAccountId()
	{
		return accountId;
	}

	public void setAccountId(String accountId){ this.accountId = accountId;}
}
