package rest;


public class PaymentRequest {
	private int amount;
	private String merchantUuid;

	private String description;

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
