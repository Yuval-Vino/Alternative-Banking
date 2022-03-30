public class Person {
    private String  name;
    private BankAccount account;

    Person(String name,int bankBalance)
    {
        this.name=name;
        this.account=new BankAccount(bankBalance);
    }

    public void balanceAction(double sum,int YAZ) {
        account.balanceAction(sum,YAZ);
    }

    public  String getName(){return name;}
    public  double getBankBalance(){return account.balance;}

    @Override
    public String toString(){
        return "Person: "+name;
    }
    public String personName(){return "Balance: "+account.balance;}

}
