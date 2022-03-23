public class Person {
    private String  name;
    BankAccount account;

    Person(String name,int bankBalance)
    {
        this.name=name;
        this.account(bankBalance);

    }

    private void account(int bankBalance) {
    }

    public  String getName(){return name;}
    public  int getBankBalance(){return account.balance;}

    @Override
    public String toString(){
        return "Person: "+name+" Balance:"+account.balance;
    }

}
