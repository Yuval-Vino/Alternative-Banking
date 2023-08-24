package collections.Banking;
import java.util.ArrayList;

import java.util.List;

public class BankAccount {
    private double balance=0;
    private List<AccountMovement> movements=new ArrayList<>();

    public BankAccount(double sum){balance=sum;}

    public double getBalance(){return balance;}

    public List<AccountMovement> getMovements() {
        return movements;
    }

    public void balanceAction(double sum, int YAZ){
        AccountMovement curr=new AccountMovement(YAZ,balance,sum);
        balance+=sum;
        movements.add(curr);
    }
    List<AccountMovement> getAccountMovements(){
       return movements;
    }

    @Override
    public String toString(){
        return "Balance: "+balance;
    }
}
