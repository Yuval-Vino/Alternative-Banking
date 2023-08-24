package collections.Banking.DTOS;

import collections.Banking.AccountMovement;
import collections.Banking.BankAccount;

import java.util.ArrayList;
import java.util.List;

public class BankAccountDummy {
    private double balance=0;
    private List<AccountMovementDummy> movements=new ArrayList<>();
    BankAccountDummy(BankAccount bankAccount){
        balance= bankAccount.getBalance();
        for(AccountMovement m: bankAccount.getMovements()){
            movements.add(new AccountMovementDummy(m));
        }
    }

    public List<AccountMovementDummy> getMovements() {
        return movements;
    }

    public double getBalance() {
        return balance;
    }
}
