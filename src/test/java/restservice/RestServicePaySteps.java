package restservice;


import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.User;
import rest.*;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.*;

public class RestServicePaySteps {

    WebTarget baseUrl;
    String CPRNumber;
    String UuidNumber;
    Boolean gotTokens;
    Boolean paymentSuccess;
    List<UUID> tokenList;

    DtuPayCustomerRepresentation customer;
    DtuPayMerchantRepresentation merchant;
    PaymentRequest paymentRequest;
    TokenRequest tokenRequest;
    helperMethod helper = new helperMethod();
    Response tokenResponse;
    Response paymentResponse;

    Response createResponse;
    Response createMerchantResponse;


    BankFactory bankFactory;
    BankService bank;



    public RestServicePaySteps() {
        Client client = ClientBuilder.newClient();
        //baseUrl = client.target("http://fastmoney-22.compute.dtu.dk:8080/api/");
        baseUrl = client.target("http://fastmoney-22.compute.dtu.dk:8080/api/");
    }

    @Before
    public void createCustomer(){
        customer = new DtuPayCustomerRepresentation("Harry", "Potter", "93024832904209", null);
    }

    @Before
    public void createMerchant(){
        merchant = new DtuPayMerchantRepresentation("voldemort", "923840932840234", null);

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
        bankUser.setCprNumber(customer.getCprNumber());
        String accountID = bank.createAccountWithBalance(bankUser, BigDecimal.valueOf(initialBalance));
        Assert.assertNotNull(accountID);
        customer.setAccountId(accountID);

    }

    @Given("is registered with dtuPay")
    public void isRegisteredWithDtuPay() {
        createResponse = baseUrl.path("customer/create").request().post(Entity.entity(customer, MediaType.APPLICATION_JSON));
        Assert.assertEquals(201, createResponse.getStatus());
    }

    @Given("he has valid tokens")
    public void heHasValidTokens() {
        tokenRequest = new TokenRequest(customer.getCprNumber(), 5);
        tokenResponse = baseUrl.path("token/request").request(MediaType.APPLICATION_JSON).post(Entity.json(tokenRequest), Response.class);
        Assert.assertEquals(200, tokenResponse.getStatus());
        tokenList = tokenResponse.readEntity(new GenericType<List<UUID>>(){});
        customer.setCustomerTokens(tokenList);
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

        createMerchantResponse = baseUrl.path("merchant/create").request().post(Entity.entity(merchant,MediaType.APPLICATION_JSON));
        Assert.assertEquals(201, createMerchantResponse.getStatus());
    }

    @When("the customer tries to perform a payment with the amount of {int}")
    public void theCustomerTriesToPerformAPaymentWithTheAmountOf(int amount) {
        paymentRequest = new PaymentRequest(amount, merchant.getUuid(),"Payment for rare chocolate frog cards, 50 galleons", customer.getCprNumber(), helper.getOneTokenFromCustomer(customer));
        //paymentRequest.setUuid(UUID.fromString(customer.getCustomerTokens());
        paymentResponse = baseUrl.path("payment/pay").request().post(Entity.entity(paymentRequest, MediaType.APPLICATION_JSON));


    }

    @Then("the endpoint api\\/pay will return true")
    public void theEndpointApiPayWillReturnTrue() {
        Assert.assertEquals(200, paymentResponse.getStatus());

    }

    @Then("the customer has a balance of {int} and the merchant has a balance of {int}")
    public void theCustomerHasABalanceOfAndTheMerchantHasABalanceOf(int expectedCustomerBalance, int expectedMerchantBalance) throws Exception{
        Assert.assertEquals(BigDecimal.valueOf(expectedCustomerBalance), bank.getAccountByCprNumber(customer.getCprNumber()).getBalance());
        Assert.assertEquals(BigDecimal.valueOf(expectedMerchantBalance), bank.getAccountByCprNumber(merchant.getUuid()).getBalance());

    }

    @Given("he doesn't have tokens")
    public void heDoesnTHaveTokens() {
       // customer.deleteAllTokens();

    }

    @Then("the endpoint api\\/pay will return false")
    public void theEndpointApiPayWillReturnFalse() {
        Assert.assertNotEquals(200, paymentResponse.getStatus());

    }


    @After
    public void deleteFromBank() throws Exception{
        bank.retireAccount(customer.getAccountId());
        bank.retireAccount(merchant.getAccount());
    }


}
