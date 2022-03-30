import java.util.ArrayList;
import java.util.List;

public class BankAccount {
    protected double balance=0;
    List<AccountMovement> movements=new ArrayList<>();
    BankAccount(double sum){balance=sum;}

    //can decrease and increase
    public void balanceAction(double sum,int YAZ){
        AccountMovement curr=new AccountMovement(YAZ,balance,sum);
        balance+=sum;
        movements.add(curr);
    }
    @Override
    public String toString(){
        return "Balance: "+balance;
    }
}
