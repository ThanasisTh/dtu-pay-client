package rest;

import dtu.DtuPayCustomerRepresentation;

import java.util.UUID;

public class helperMethod {


    public String getOneTokenFromCustomer(DtuPayCustomerRepresentation customer){
        return customer.getCustomerTokens().get(0);
    }
}
