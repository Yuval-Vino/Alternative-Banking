package collections.Banking;

import java.util.ArrayList;
import java.util.List;

public class Client {

    private String  name;
    private BankAccount account;


    public Client(String name, int bankBalance)
    {
        this.name=name;
        this.account=new BankAccount(bankBalance);
    }

    public void balanceAction(double sum,int YAZ) {
        account.balanceAction(sum,YAZ);
    }

    public  String getName(){return name;}
    public  double getBankBalance(){return account.getBalance();}
    public BankAccount getBankAccount(){return account;}
    @Override
    public String toString(){
        return name;
    }
    public String toStringWithBalance(){return toString()+ " Balance: "+account.getBalance();}


}
