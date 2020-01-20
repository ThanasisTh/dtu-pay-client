package restservice;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dtu.*;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.User;
import rest.BankFactory;
import rest.helperMethod;
import rest.RestApplication;
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


    String CPRNumber;
    String UuidNumber;
    Boolean gotTokens;
    Boolean paymentSuccess;
    List<UUID> tokenList;


    helperMethod helper = new helperMethod();
    Response createResponse;
    Response createMerchantResponse;


    BankFactory bankFactory;
    BankService bank;

    String webServer = "http://fastmoney-22.compute.dtu.dk:";
    String localhost = "http://localhost:";
    String host = localhost;

    String monolithPort = Integer.toString(Config.DTU_PAY_PORT);
    String customerPort = Integer.toString(Config.CUSTOMER_PORT);
    String merchantPort = Integer.toString(Config.MERCHANT_PORT);
    String tokenManagerPort = Integer.toString(Config.TOKEN_PORT);
    String dtuPayPort = Integer.toString(Config.DTU_PAY_PORT);

    WebTarget baseUrlMonolith;
    WebTarget baseUrlCustomer;
    WebTarget baseUrlMerchant;
    WebTarget baseUrlTokenManager;
    WebTarget baseUrlDtuPay;

    WebTarget baseUrl;

    Response paymentResponse;
    Response tokenResponse;
    String tokenStringResponse;
    String testToken;

    DtuPayCustomerRepresentation customer;
    DtuPayMerchantRepresentation merchant;

    TokenRequest tokenRequest;

    Response deleteCustomerResponse;
    Response deleteMerchantResponse;
    Response verifyTokenResponse;

    PaymentRequest paymentRequest;


    public RestServicePaySteps() {
        Client client = ClientBuilder.newClient();
        baseUrlMonolith = client.target(host + monolithPort + "/api/");
        baseUrlCustomer = client.target(host + customerPort + "/api/customer/");
        baseUrlMerchant = client.target(host + merchantPort + "/api/merchant/");
        baseUrlTokenManager = client.target(host + tokenManagerPort + "/api/token/");
        baseUrlDtuPay = client.target(host + dtuPayPort + "/api/dtupay/");
        baseUrl = client.target("http://localhost:8081");
    }


    @Before
    public void createCustomer(){
        customer = new DtuPayCustomerRepresentation("Harry", "Potter", UUID.randomUUID().toString(),null);
    }

    @Before
    public void createMerchant(){
        merchant = new DtuPayMerchantRepresentation("voldemort", UUID.randomUUID().toString(), null);

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
        System.out.println(baseUrlCustomer.path("create").toString());
        createResponse = baseUrlCustomer.path("create").request().post(Entity.entity(customer, MediaType.APPLICATION_JSON));
        Assert.assertEquals(201, createResponse.getStatus());
    }

    @Given("he has valid tokens")
    public void heHasValidTokens() {
        tokenRequest = new TokenRequest(customer.getCprNumber(), 5);
        tokenResponse = baseUrlTokenManager.path("request").request(MediaType.APPLICATION_JSON).post(Entity.entity(tokenRequest, MediaType.APPLICATION_JSON));
        Assert.assertEquals(201, tokenResponse.getStatus());

        List<String> tokenList = tokenResponse.readEntity(new GenericType<List<String>>(){});
        Assert.assertNotNull(tokenList);
        Assert.assertEquals(5, tokenList.size());
        customer.setCustomerTokens(tokenList);
        testToken = helper.getOneTokenFromCustomer(customer);
        Assert.assertNotNull(testToken);
    }

    @Given("there is a registered merchant that also has a bank account with initial balance {int}")
    public void thereIsARegisteredMerchantThatAlsoHasABankAccountWithInitialBalance(int initialBalance) throws Exception {
        User bankUser2 = new User();
        bankUser2.setFirstName(merchant.getName());
        bankUser2.setLastName(merchant.getName());
        bankUser2.setCprNumber(merchant.getUuid());
        String accountID2 = bank.createAccountWithBalance(bankUser2, BigDecimal.valueOf(initialBalance));
        Assert.assertNotNull(accountID2);
        merchant.setAccountId(accountID2);

        createMerchantResponse = baseUrlMerchant.path("create").request().post(Entity.entity(merchant,MediaType.APPLICATION_JSON));
        Assert.assertEquals(201, createMerchantResponse.getStatus());
    }

    @When("the customer tries to perform a payment with the amount of {int}")
    public void theCustomerTriesToPerformAPaymentWithTheAmountOf(int amount) {


        paymentRequest = new PaymentRequest(amount, merchant.getUuid(),"Payment for rare chocolate frog cards, 50 galleons", customer.getCprNumber(), testToken);
        Assert.assertNotNull(paymentRequest);
        paymentResponse = baseUrlDtuPay.path("pay").request().post(Entity.entity(paymentRequest, MediaType.APPLICATION_JSON));
        System.out.println(baseUrlDtuPay.path("pay").toString());
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
        testToken = null;
    }

    @Then("the endpoint api\\/pay will return false")
    public void theEndpointApiPayWillReturnFalse() {
        Assert.assertEquals(409, paymentResponse.getStatus());
    }


    @After
    public void deleteFromBank() throws Exception{
        bank.retireAccount(customer.getAccountId());
        bank.retireAccount(merchant.getAccountId());
    }

    @After
    public void deleteFromMicroServices() throws Exception{
        deleteCustomerResponse = baseUrlCustomer.path("delete/"+customer.getCprNumber()).request().get();
        Assert.assertEquals(200, deleteCustomerResponse.getStatus());
        deleteMerchantResponse = baseUrlMerchant.path("delete/"+merchant.getUuid()).request().get();
        Assert.assertEquals(200, deleteMerchantResponse.getStatus());
    }

}
