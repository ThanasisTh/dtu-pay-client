package restservice;


import dtu.ws.fastmoney.BankService;
import io.cucumber.java.After;
import rest.DtuPayMerchantRepresentation;
import rest.DtuPayCustomerRepresentation;
import rest.PaymentRequest;
import rest.TokenRequest;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class RestServiceSteps {  // This is not currently used because RestServicePaySteps covers all the cases

    WebTarget baseUrl;
    Response response;
    DtuPayCustomerRepresentation customer;
    DtuPayMerchantRepresentation merchant;
    PaymentRequest paymentRequest;
    String CPRNumber;
    String UuidNumber;
    boolean booleanResponse;
    TokenRequest tokenRequest;
    BankService bankService;
    List<UUID> tokenList;
    Response tokenResponse;

    public RestServiceSteps() {
        Client client = ClientBuilder.newClient();
        baseUrl = client.target("http://localhost:8080/api/");
    }


    @Given("there is a customer in the bank with credentials {string} {string} with cpr number {string} and account id {string}")
    public void thereIsACustomerInTheBankWithCredentialsWithCprNumberAndAccountId(String firstname, String lastname, String cprNumber, String accountID) {
         customer = new DtuPayCustomerRepresentation("Ron", "Weasly", "cpr82849", "43289048390");
         customer.setAccountId(accountID);
         CPRNumber = cprNumber;
    }

    @Given("all the credentials are valid")
    public void allTheCredentialsAreValid() {
    }

    @When("We register the customer to dtu pay using the rest service with source path customer\\/create")
    public void weRegisterTheCustomerToDtuPayUsingTheRestServiceWithSourcePathCustomerCreate() {
        response = baseUrl.path("customer/create").request().post(Entity.entity(customer, MediaType.APPLICATION_JSON));
        Assert.assertEquals(201, response.getStatus());


    }

    @Then("we get a confirmation that the customer was registered")
    public void weGetAConfirmationThatTheCustomerWasRegistered() {

    }


    @Given("there is a merchant in the bank with credentials {string} with uuid number {string} and account id {string}")
    public void thereIsAMerchantInTheBankWithCredentialsWithUuidNumberAndAccountId(String name, String uuid, String account) {
        merchant = new DtuPayMerchantRepresentation(name, uuid, account);
        UuidNumber = uuid;
    }

    @When("We register the merchant to dtu pay using the rest service with source path merchant\\/create")
    public void weRegisterTheMerchantToDtuPayUsingTheRestServiceWithSourcePathMerchantCreate() {
        response = baseUrl.path("merchant/create").request().post(Entity.entity(merchant, MediaType.APPLICATION_JSON));
        Assert.assertEquals(201, response.getStatus());
    }

    @Then("we get a confirmation that the merchant was registered")
    public void weGetAConfirmationThatTheMerchantWasRegistered() {
    }

    @Given("there is a registered customer in dtuPay with cprNumber {string}")
    public void thereIsARegisteredCustomerInDtuPayWithCprNumber(String cprNumber) {
        customer = new DtuPayCustomerRepresentation("Hermione", "Granger", cprNumber, "409432980428930");
        CPRNumber = cprNumber;
        response = baseUrl.path("customer/create").request().post(Entity.entity(customer, MediaType.APPLICATION_JSON));
        Assert.assertEquals(201, response.getStatus());
    }

    @When("he requests {int} tokens")
    public void heRequestsTokens(int numberOfTokens) {
        tokenRequest = new TokenRequest(CPRNumber, numberOfTokens);


        tokenResponse = baseUrl.path("token/request").request(MediaType.APPLICATION_JSON).post(Entity.json(tokenRequest), Response.class);
        Assert.assertEquals(200, tokenResponse.getStatus());

        tokenList = tokenResponse.readEntity(new GenericType<List<UUID>>(){});
        //System.out.println(tokenList);

        //tokenList = baseUrl.path("token/request2").request().post(Entity.entity(tokenRequest, MediaType.APPLICATION_JSON),List.class);
        Assert.assertNotNull(tokenList);
        Assert.assertEquals(numberOfTokens, tokenList.size());
        customer.setCustomerTokens(tokenList);
    }

    @Then("we get a confirmation that the system gave him tokens")
    public void weGetAConfirmationThatTheSystemGaveHimTokens() {

    }
//
//    @After
//    public void deleteData(){
//        if (customer != null) {
//            response = baseUrl.path("customer/delete").queryParam(customer.getCprNumber()).request().get();
//            Assert.assertEquals(200, response.getStatus());
//        }
//        if (merchant != null) {
//            response = baseUrl.path("merchant/delete").queryParam(merchant.getUuid()).request().get();
//            Assert.assertEquals(200, response.getStatus());
//        }
//    }
}
