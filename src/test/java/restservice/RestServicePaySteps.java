package restservice;


import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.User;
import rest.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.After;
import org.junit.Assert;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;

public class RestServicePaySteps {

    WebTarget baseUrl;
    String CPRNumber;
    String UuidNumber;
    Boolean gotTokens;
    Boolean paymentSuccess;

    DtuPayUserRepresentation customer;
    DtuPayMerchantRepresentation merchant;
    PaymentRequest paymentRequest;
    TokenRequest tokenRequest;

    BankFactory bankFactory;
    BankService bank;



    public RestServicePaySteps() {
        Client client = ClientBuilder.newClient();
        baseUrl = client.target("http://localhost:8080/api/");
    }

    @Before
    public void createCustomer(){
        customer = new DtuPayUserRepresentation();
        customer.setFirstName("Harry");
        customer.setLastName("Potter");
        customer.setCpr("93024832904209");
    }

    @Before
    public void createMerchant(){
        merchant = new DtuPayMerchantRepresentation();
        merchant.setName("Voldemort");
        merchant.setUuid("923840932840234");
    }

    @Before
    public void createBank(){
        bank = new BankFactory().getBank();
    }

    @Given("a customer has a bank account with initial balance {int}")
    public void aCustomerHasABankAccountWithInitialBalance(int initialBalance) throws Exception {
        User bankUser = new User();
        bankUser.setFirstName(customer.getFirstName());
        bankUser.setLastName(customer.getLastName());
        bankUser.setCprNumber(customer.getCpr());
        String accountID = bank.createAccountWithBalance(bankUser, BigDecimal.valueOf(initialBalance));
        Assert.assertNotNull(accountID);
        customer.setAccount(accountID);

    }

    @Given("is registered with dtuPay")
    public void isRegisteredWithDtuPay() {
        CPRNumber = baseUrl.path("register/customer").request().post(Entity.entity(customer, MediaType.APPLICATION_JSON),String.class);
        Assert.assertEquals(customer.getCpr(), CPRNumber);
    }

    @Given("he has valid tokens")
    public void heHasValidTokens() {
        tokenRequest = new TokenRequest();
        tokenRequest.setCpr(customer.getCpr());
        tokenRequest.setNumber(5);
        gotTokens = baseUrl.path("request/tokens").request().post(Entity.entity(tokenRequest, MediaType.APPLICATION_JSON), Boolean.class);
        Assert.assertTrue(gotTokens);
    }

    @Given("there is a registered merchant that also has a bank account with initial balance {int}")
    public void thereIsARegisteredMerchantThatAlsoHasABankAccountWithInitialBalance(int initialBalance) throws Exception {
        User bankUser2 = new User();
        bankUser2.setFirstName(merchant.getName());
        bankUser2.setLastName(merchant.getName());
        bankUser2.setCprNumber(merchant.getUuid());
        String accountID2 = bank.createAccountWithBalance(bankUser2, BigDecimal.valueOf(initialBalance));
        Assert.assertNotNull(accountID2);
        merchant.setAccount(accountID2);

        UuidNumber = baseUrl.path("register/merchant").request().post(Entity.entity(merchant,MediaType.APPLICATION_JSON),String.class);
        Assert.assertEquals(merchant.getUuid(), UuidNumber);
    }

    @When("the customer tries to perform a payment with the amount of {int}")
    public void theCustomerTriesToPerformAPaymentWithTheAmountOf(int amount) {
        paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(amount);
        paymentRequest.setCustomerCpr(customer.getCpr());
        paymentRequest.setMerchantUuid(merchant.getUuid());
        paymentRequest.setDescription("Payment for rare chocolate frog cards, 50 galleons");

        paymentSuccess = baseUrl.path("pay").request().post(Entity.entity(paymentRequest, MediaType.APPLICATION_JSON), Boolean.class);


    }

    @Then("the endpoint api\\/pay will return true")
    public void theEndpointApiPayWillReturnTrue() {
        Assert.assertTrue(paymentSuccess);
    }

    @Then("the customer has a balance of {int} and the merchant has a balance of {int}")
    public void theCustomerHasABalanceOfAndTheMerchantHasABalanceOf(int expectedCustomerBalance, int expectedMerchantBalance) throws Exception{
        Assert.assertEquals(BigDecimal.valueOf(expectedCustomerBalance), bank.getAccountByCprNumber(customer.getCpr()).getBalance());
        Assert.assertEquals(BigDecimal.valueOf(expectedMerchantBalance), bank.getAccountByCprNumber(merchant.getUuid()).getBalance());

    }

    @Given("he doesn't have tokens")
    public void heDoesnTHaveTokens() {

    }

    @Then("the endpoint api\\/pay will return false")
    public void theEndpointApiPayWillReturnFalse() {
       Assert.assertFalse(paymentSuccess);
    }


    @After
    public void deleteFromBank() throws Exception{
        bank.retireAccount(customer.getAccount());
        bank.retireAccount(merchant.getAccount());
    }


}
