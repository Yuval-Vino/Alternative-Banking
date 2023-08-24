package collections.Banking;

import collections.Banking.DTOS.LoanDummy;

public class LoanForSale {
    Client owner;
    Loan loan;
    LoanForSale(Loan loan,Client owner){
        this.owner = owner;
        this.loan = loan;
    }
    public int getAmountOfSale(){
       int totalAmount = loan.loaners.get(owner.getName());
       double rePayedAmount = totalAmount / loan.getDesiredAmount() * loan.amountEachPayment()*loan.getNumOfPaymentsMade();
       return (int) (totalAmount-rePayedAmount);
    }
    public String getSellerName(){return owner.getName();}
    public String getLoanOwner(){return loan.getBorrower().getName();}
    public int getProfit() {return (int) (getAmountOfSale()*(1+(loan.getInterest()/100)));}
    public LoanDummy getLoan() {return new LoanDummy(loan);}

}
