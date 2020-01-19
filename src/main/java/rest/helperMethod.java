package rest;

import java.util.UUID;

public class helperMethod {


    public UUID getOneTokenFromCustomer(DtuPayCustomerRepresentation customer){
        return customer.getCustomerTokens().get(0);
    }
}
