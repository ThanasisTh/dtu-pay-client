package restservice;

import dtu.*;
import dtu.ws.fastmoney.BankService;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import rest.BankFactory;
import rest.helperMethod;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

public class RestServiceTokenSteps {

    Response createCustomerResponse;
    Response createMerchantResponse;
    Response requestTokenResponse;
    Response deleteCustomerResponse;

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

    int numberOfTokensRequested;

    DtuPayCustomerRepresentation customer;
    TokenRequest tokenRequest;


    public RestServiceTokenSteps() {
        Client client = ClientBuilder.newClient();
        baseUrlMonolith = client.target(host + monolithPort + "/api/");
        baseUrlCustomer = client.target(host + customerPort + "/api/customer/");
        baseUrlMerchant = client.target(host + merchantPort + "/api/merchant/");
        baseUrlTokenManager = client.target(host + tokenManagerPort + "/api/token/");
        baseUrlDtuPay = client.target(host + dtuPayPort + "/api/dtupay/");
        baseUrl = client.target("http://localhost:8081");
        customer = new DtuPayCustomerRepresentation("Luna", "Lovegood", UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }

    @After
    public void deleteCustomer(){
        if (customer.getAccountId() == null){
        deleteCustomerResponse = baseUrlCustomer.path("delete/"+customer.getCprNumber()).request().get();
        Assert.assertEquals(200, deleteCustomerResponse.getStatus());
        }
    }



    @Given("a customer registered in dtuPay")
    public void aCustomerRegisteredInDtuPay() {
        createCustomerResponse = baseUrlCustomer.path("create").request().post(Entity.entity(customer, MediaType.APPLICATION_JSON));
        Assert.assertEquals(201, createCustomerResponse.getStatus());
    }

    @Given("doesnt have any valid tokens")
    public void doesntHaveAnyValidTokens() {
       Assert.assertNull(customer.getCustomerTokens());
    }

    @When("the customer asks for {int} tokens")
    public void theCustomerAsksForTokens(int number) {
        numberOfTokensRequested = number;
        tokenRequest = new TokenRequest(customer.getCprNumber(), numberOfTokensRequested);
        requestTokenResponse = baseUrlTokenManager.path("request").request(MediaType.APPLICATION_JSON).post(Entity.entity(tokenRequest, MediaType.APPLICATION_JSON));
    }

    @Then("he will recieve {int} tokens")
    public void heWillRecieveTokens(Integer int1) {
        Assert.assertEquals(201, requestTokenResponse.getStatus());
        List<String> tokenList = requestTokenResponse.readEntity(new GenericType<List<String>>(){});
        Assert.assertNotNull(tokenList);
        Assert.assertEquals(numberOfTokensRequested, tokenList.size());
        customer.setCustomerTokens(tokenList);
        Assert.assertNotNull(customer.getCustomerTokens());
    }

    @Then("he will not recieve any tokens")
    public void heWillNotRecieveAnyTokens() {
        Assert.assertNotEquals(201, requestTokenResponse.getStatus());
    }

    @Given("already have {int} tokens")
    public void alreadyHaveTokens(int number) {
        tokenRequest = new TokenRequest(customer.getCprNumber(), number);
        requestTokenResponse = baseUrlTokenManager.path("request").request(MediaType.APPLICATION_JSON).post(Entity.entity(tokenRequest, MediaType.APPLICATION_JSON));
        Assert.assertEquals(201, requestTokenResponse.getStatus());
        List<String> tokenList = requestTokenResponse.readEntity(new GenericType<List<String>>(){});
        Assert.assertNotNull(tokenList);
        customer.setCustomerTokens(tokenList);
        Assert.assertNotNull(customer.getCustomerTokens());
        Assert.assertEquals(number, customer.getCustomerTokens().size());
    }

    @Then("he will have {int} tokens")
    public void heWillHaveTokens(int int1) {
        Assert.assertEquals(int1, customer.getCustomerTokens().size());
    }
}
