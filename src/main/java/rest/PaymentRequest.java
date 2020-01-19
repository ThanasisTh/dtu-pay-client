package rest;


import java.util.UUID;

public class PaymentRequest
{
	private int amount;
	private String merchantUuid;
	private String description;
	private String customerCpr;
	private UUID token;

	public PaymentRequest(){

	}

	public PaymentRequest(int amount, String merchantUuid, String description, String customerCpr, UUID token)
	{
		this.amount = amount;
		this.merchantUuid = merchantUuid;
		this.description = description;
		this.customerCpr = customerCpr;
		this.token = token;
	}

	public String getCustomerCpr()
	{
		return customerCpr;
	}

	public int getAmount()
	{
		return amount;
	}

	public String getMerchantUuid()
	{
		return merchantUuid;
	}

	public String getDescription()
	{
		return description;
	}

	public UUID getToken() { return token; }
}