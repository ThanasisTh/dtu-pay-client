package restservice;



import dtu.*;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.User;
import io.cucumber.java.af.En;
import rest.BankFactory;
import rest.helperMethod;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
    ReportRequest reportRequest;



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
    Response refundResponse;
    Response reportResonse;

    PaymentRequest paymentRequest;


    public RestServicePaySteps() {
        Client client = ClientBuilder.newClient();
        String host = CucumberTest.getHost();
        baseUrlMonolith = client.target(host + monolithPort + "/api/");
        baseUrlCustomer = client.target(host + customerPort + "/api/customer/");
        baseUrlMerchant = client.target(host + merchantPort + "/api/merchant/");
        baseUrlTokenManager = client.target(host + tokenManagerPort + "/api/token/");
        baseUrlDtuPay = client.target(host + dtuPayPort + "/api/dtupay/");
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
        if (customer.getAccountId() != null){
            bank.retireAccount(customer.getAccountId());
            deleteCustomerResponse = baseUrlCustomer.path("delete/"+customer.getCprNumber()).request().get();
            Assert.assertEquals(200, deleteCustomerResponse.getStatus());
        }
        if (merchant.getAccountId() != null) {
            bank.retireAccount(merchant.getAccountId());
            deleteMerchantResponse = baseUrlMerchant.path("delete/"+merchant.getUuid()).request().get();
            Assert.assertEquals(200, deleteMerchantResponse.getStatus());
        }
    }
//int amount, String merchantUuid, String description, String customerCpr, String token
    @When("the customer is granted a refund of {int}")
    public void theCustomerIsGrantedARefundOf(int amount) {
        testToken = helper.getOneTokenFromCustomer(customer);
        Assert.assertNotNull(testToken);
        paymentRequest = new PaymentRequest(amount, merchant.getUuid(), "This is a refund", customer.getCprNumber(), testToken);
        paymentResponse = baseUrlDtuPay.path("refund").request().post(Entity.entity(paymentRequest, MediaType.APPLICATION_JSON));

    }

    @Then("the api\\/dtupay\\/refund will return status code ok")
    public void theApiDtupayRefundWillReturnStatusCodeOk() {
        Assert.assertEquals(200, paymentResponse.getStatus());
    }

    @When("the customer performs {int} purchases with amounts between {int} and {int}")
    public void theCustomerPerformsPurchasesWithAmountsBetweenAnd(int amountOfPurchases, int beginRandom, int stopRandom) {
        paymentRequest = new PaymentRequest(0, merchant.getUuid(), null, customer.getCprNumber(), null);
        for (int i = 0; i < amountOfPurchases; i++){

            Random r = new Random();
            int low = beginRandom;
            int high = stopRandom;
            int randomInt= r.nextInt(high-low) + low;

            paymentRequest.setAmount(randomInt);
            paymentRequest.setDescription("this is purchase nr: " + Integer.toString(i));
            paymentRequest.setToken(helper.getOneTokenFromCustomer(customer));
            paymentResponse = baseUrlDtuPay.path("pay").request().post(Entity.entity(paymentRequest, MediaType.APPLICATION_JSON));
            Assert.assertEquals(200, paymentResponse.getStatus());
        }
    }

    @When("when he asks for reports between yesterday and tomorrow")
    public void whenHeAsksForReportsBetweenJanuaryAndJanuary(Integer int1, Integer int2, Integer int3, Integer int4) {
        SimpleDateFormat dateFormatStart = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormatEnd = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        dateFormatStart.format(date.before(date));
        dateFormatEnd.format(date.after(date));

        reportRequest = new ReportRequest(dateFormatStart, dateFormatEnd, customer.getCprNumber());
        reportResonse = baseUrlDtuPay.path("report").request().post(Entity.entity(reportRequest, MediaType.APPLICATION_JSON));


    }

    @Then("he gets {int} payment objects describing the purchases")
    public void heGetsPaymentObjectsDescribingThePurchases(Integer int1) {
       Assert.assertEquals(200, reportResonse);
    }


        // 200 ok
        // 201 created
        // 204
}
