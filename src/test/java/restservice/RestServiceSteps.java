package restservice;

import dtu.DtuPayMerchantRepresentation;
import dtu.DtuPayUserRepresentation;
import dtu.PaymentRequest;
import dtu.TokenRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import io.cucumber.java.Before;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class RestServiceSteps {

    WebTarget baseUrl;
    String response;
    DtuPayUserRepresentation customer;
    DtuPayMerchantRepresentation merchant;
    PaymentRequest paymentRequest;
    String CPRNumber;
    String UuidNumber;
    boolean booleanResponse;
    TokenRequest tokenRequest;

    public RestServiceSteps() {
        Client client = ClientBuilder.newClient();
        baseUrl = client.target("http://localhost:8080/api/");
    }


    @Given("there is a customer in the bank with credentials {string} {string} with cpr number {string} and account id {string}")
    public void thereIsACustomerInTheBankWithCredentialsWithCprNumberAndAccountId(String firstname, String lastname, String cprNumber, String accountID) {
         customer = new DtuPayUserRepresentation();
         customer.setFirstName(firstname);
         customer.setLastName(lastname);
         customer.setCpr(cprNumber);
         customer.setAccount(accountID);
         CPRNumber = cprNumber;
    }

    @Given("all the credentials are valid")
    public void allTheCredentialsAreValid() {
    }

    @When("We register the customer to dtu pay using the rest service with source path \\/register\\/customer")
    public void weRegisterTheCustomerToDtuPayUsingTheRestServiceWithSourcePathRegisterCustomer() {
        response = baseUrl.path("register/customer").request().post(Entity.entity(customer, MediaType.APPLICATION_JSON),String.class);
        Assert.assertEquals(CPRNumber, response);


    }

    @Then("we get a confirmation that the customer was registered")
    public void weGetAConfirmationThatTheCustomerWasRegistered() {

    }

    @Given("there is a merchant in the bank with credentials {string} with uuid number {string} and account id {string}")
    public void thereIsAMerchantInTheBankWithCredentialsWithUuidNumberAndAccountId(String name, String uuid, String account) {
        merchant = new DtuPayMerchantRepresentation();
        merchant.setName(name);
        merchant.setUuid(uuid);
        merchant.setAccount(account);
        UuidNumber = uuid;
    }

    @When("We register the merchant to dtu pay using the rest service with source path \\/register\\/merchant")
    public void weRegisterTheMerchantToDtuPayUsingTheRestServiceWithSourcePathRegisterMerchant() {
        response = baseUrl.path("register/merchant").request().post(Entity.entity(merchant, MediaType.APPLICATION_JSON),String.class);
        Assert.assertEquals(UuidNumber, response);
    }

    @Then("we get a confirmation that the merchant was registered")
    public void weGetAConfirmationThatTheMerchantWasRegistered() {
    }

    @Given("there is a registered customer in dtuPay with cprNumber {string}")
    public void thereIsARegisteredCustomerInDtuPayWithCprNumber(String cprNumber) {
        customer = new DtuPayUserRepresentation();
        customer.setFirstName("name");
        customer.setLastName("navn");
        customer.setCpr(cprNumber);
        customer.setAccount("9201210933");
        CPRNumber = cprNumber;
        response = baseUrl.path("register/customer").request().post(Entity.entity(customer, MediaType.APPLICATION_JSON),String.class);
        Assert.assertEquals(cprNumber, response);
    }

    @When("he requests {int} tokens")
    public void heRequestsTokens(int numberOfTokens) {
        tokenRequest = new TokenRequest();
        tokenRequest.setCpr(CPRNumber);
        tokenRequest.setNumber(numberOfTokens);
        booleanResponse = baseUrl.path("request/tokens").request().post(Entity.entity(tokenRequest, MediaType.APPLICATION_JSON),Boolean.class);
    }

    @Then("we get a confirmation that the system gave him tokens")
    public void weGetAConfirmationThatTheSystemGaveHimTokens() {
        Assert.assertTrue(booleanResponse);
    }

//    @Before
//    public void test() {
//        String accountNr = baseUrl.path("get/account").queryParam("id", "1234").request().get(String.class);
//        Assert.assertEquals("51a5837a-322b-4acd-a4b4-8318d032ea8a", accountNr);
//
//    }


}
