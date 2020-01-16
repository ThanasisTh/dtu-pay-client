package restservice;

import dtu.DtuPayMerchantRepresentation;
import dtu.DtuPayUserRepresentation;
import dtu.PaymentRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

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

       String returnedCprNumber;
       response = baseUrl.path("register/customer").request().post(Entity.entity(customer, MediaType.APPLICATION_JSON),String.class);
       Assert.assertEquals(CPRNumber, response);


    }

    @Then("we get a confirmation that the customer was registered")
    public void weGetAConfirmationThatTheCustomerWasRegistered() {

    }


}
