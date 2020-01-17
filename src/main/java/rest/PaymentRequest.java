package rest;


import java.util.UUID;

public class PaymentRequest {

	private int amount;
	private String merchantUuid;
	private String description;
	private UUID uuid;

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}



	public String getCustomerCpr() {
		return customerCpr;
	}

	public void setCustomerCpr(String customerCpr) {
		this.customerCpr = customerCpr;
	}

	private String customerCpr;
	
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getMerchantUuid() {
		return merchantUuid;
	}
	public void setMerchantUuid(String merchantUuid) {
		this.merchantUuid = merchantUuid;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
