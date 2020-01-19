package rest;

public class DtuPayMerchantRepresentation
{
    private String name;
    private String uuid;
    private String account;

    public DtuPayMerchantRepresentation(){
        super();
    }

    public DtuPayMerchantRepresentation(String name, String uuid, String account)
    {
        this.name = name;
        this.uuid = uuid;
        this.account = account;
    }

    public String getName()
    {
        return name;
    }

    public String getUuid()
    {
        return uuid;
    }

    public String getAccount()
    {
        return account;
    }

    public void setAccount(String account){
        this.account = account;
    }
}
