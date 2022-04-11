package collections.Banking.DTOS;

import collections.Banking.Client;

public class ClientDummy {
    private String  name;
    private BankAccountDummy account;

    public ClientDummy(Client c){
        name = c.getName();
        account = new BankAccountDummy(c.getBankAccount());
    }
    public String getName(){return name;}

    public BankAccountDummy getAccount() {
        return account;
    }

    @Override
    public String toString(){
        return name;
    }
    public String toStringWithBalance(){return toString()+ " Balance: "+account.getBalance();}

}
