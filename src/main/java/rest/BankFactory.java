package rest;


import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceService;

public class BankFactory
{
    private BankService bankService;

    public BankFactory()
    {
        bankService = new BankServiceService().getBankServicePort();
    }

    public BankService getBank()
    {
        return bankService;
    }
}
