package collections.Banking.DTOS;
import collections.Banking.BankMessages;
import collections.Banking.Client;

import java.util.ArrayList;
import java.util.List;

public class ClientDummy {
    private String  name;
    private BankAccountDummy account;
    private List<BankMessagesDummy> messagesDummyList = new ArrayList<>();
    public ClientDummy(String name){
        this.name=name;
    }
    public ClientDummy(Client c){
        if(c == null)
            return;
        name = c.getName();

        account = new BankAccountDummy(c.getBankAccount());
        for(BankMessages msg : c.getMessages())
            messagesDummyList.add(new BankMessagesDummy(msg));
    }
    public String getName(){return name;}

    public BankAccountDummy getAccount() {
        return account;
    }

    @Override
    public String toString(){
        return name;
    }
    public String toStringWithBalance(){return this + " Balance: "+account.getBalance();}
    public double getBalance() {return account.getBalance();}

    public List<BankMessagesDummy> getMessagesDummyList() {
        return messagesDummyList;
    }
}
