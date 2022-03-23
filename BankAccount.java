public class BankAccount {
    protected int balance=0;
    BankAccount(int sum){balance=sum;}

    //can decrease and increase
    public void balanceAction(int sum){balance+=sum;}
    @Override
    public String toString(){
        return "Balance: "+balance;
    }
}
