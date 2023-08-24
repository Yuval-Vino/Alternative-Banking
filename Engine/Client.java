package collections.Banking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Client {

    private String  name;
    private BankAccount account;
    private List<BankMessages> messages = new ArrayList<>();

    public Client(String name, int bankBalance) {
        this.name=name;
        this.account=new BankAccount(bankBalance);
    }

    public void sendMessage(String loanName,int YAZ, String message){
        messages.add(new BankMessages(loanName,YAZ,message));
    }
    public void balanceAction(double sum,int YAZ) {
        account.balanceAction(sum,YAZ);
    }

    public List<BankMessages> getMessages(){return messages;}

    public  String getName(){return name;}

    public  double getBankBalance(){return account.getBalance();}

    public BankAccount getBankAccount(){return account;}

    @Override
    public String toString(){
        return name;
    }

    public String toStringWithBalance(){return this + " Balance: "+account.getBalance();}
}
