package rest;

import dtu.DtuPayCustomerRepresentation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class helperMethod {


    public String getOneTokenFromCustomer(DtuPayCustomerRepresentation customer){
        String stringToken = customer.getCustomerTokens().get(0);
        List<String> customerTokens = customer.getCustomerTokens();
        customerTokens.remove(stringToken);
        customer.setCustomerTokens(customerTokens);
        return stringToken;
    }


    public static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
}
