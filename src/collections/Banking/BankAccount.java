package collections.Banking;
import java.util.ArrayList;

import java.util.List;

public class BankAccount {
    private double balance=0;
    private List<AccountMovement> movements=new ArrayList<>();

    public BankAccount(double sum){balance=sum;}

    //can decrease and increase
    public double getBalance(){return balance;}

    public void balanceAction(double sum,int YAZ){
        AccountMovement curr=new AccountMovement(YAZ,balance,sum);
        balance+=sum;
        movements.add(curr);
    }
    public void getAccountMovements(){
        for (AccountMovement curr : movements)
            System.out.println(curr);
    }

    @Override
    public String toString(){
        return "Balance: "+balance;
    }
}
