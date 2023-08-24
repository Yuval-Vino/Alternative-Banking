package collections.Banking;

import java.util.*;
import Time.TimeLine;

import static java.lang.Math.min;

public class Loan {
    protected Client borrower;
    Map<String,Integer> loaners=new HashMap<>();
    private String name;
    private int desiredAmount;
    private double collectedAmount = 0;
    private double interest;
    private int currPayment = 1;
    private int paymentFreq;
    LoanStatus status = LoanStatus.PENDING;
    private int initalYAZ;
    public int finalYAZ;
    private int loanDuration;
    private double payedAmount = 0;
    private int missPayments = 0;
    private boolean payedToday = false;
    private int numOfPaymentsMade = 0;
    String category;
    
    public Loan(String loanName, Client borrower, String category, int desiredAmount, int loanDuration, int paymentFreq, double interest) {
        this.borrower = borrower;
        this.name = loanName;
        this.desiredAmount = desiredAmount;
        this.loanDuration = loanDuration;
        this.paymentFreq = paymentFreq;
        this.interest = interest;
        this.category=category;
    }

    public void addLoaner(Client loaner, int sum, TimeLine time){
        loaners.put(loaner.getName(),sum);
        loaner.balanceAction(-sum, time.getTime());
        collectedAmount += sum;
        if(collectedAmount >= desiredAmount){
            status = LoanStatus.ACTIVE;
            borrower.balanceAction(desiredAmount,time.getTime());
        }

    }
    public boolean isPayDay(TimeLine time){
        if(time.getTime()==0)
            return false ;
        if((time.getTime() - initalYAZ) % paymentFreq == 0 &&  time.getTime() - initalYAZ != 0){
            currPayment++;
            return true;
        }
        return false;
    }
    @Override
    public String toString()
    {
        String res= "Loan name: "+name+
                " |Borrower name: "+borrower+
                " |Borrowing for : "+category+
                " |Loan Amount:" + desiredAmount+
                " |Loan duration: "+ loanDuration+
                " |Interest :" +interest+"%"+
                " |Frequency every "+paymentFreq+" YAZ"+
                " |Loan Status:" + status;
      if(status== LoanStatus.PENDING)
            res+=addPendingInfo();

        if(status== LoanStatus.ACTIVE)
            res+=addActiveInfo();

        return res;
    }
   private String addPendingInfo(){

        return loaners.toString()+"| Collected: " +collectedAmount + " rest amount to collect " +(desiredAmount - collectedAmount);
        // need to orginaze the to string not to print the bank account/
    }
    private String addActiveInfo(){
        return " Payed : "+payedAmount + "next Pay day is: " + (paymentFreq*currPayment + initalYAZ) +
                "the total payment left to pay " + (desiredAmount * (interest/100 + 1) - payedAmount);
    }
    private String addFinishedInfo(){
        return "|intial yaz: " + initalYAZ +"|finish yaz: " + finalYAZ;
    }

    //Getters&Movers
    public void turnToActive(TimeLine time){
        initalYAZ=time.getTime();
        finalYAZ=initalYAZ+loanDuration;
    }
    public void addToDebt(){
        if(payedAmount+amountEachPayment()*missPayments < desiredAmount*(1+(interest/100)))
        missPayments ++
        ;}
    public double getInterest(){return interest;}
    public void addPayedAmount(double sum){
        payedAmount+=sum;
        missPayments = 0;
    }
    public double getPayedAmount(){return payedAmount;}
    public int getDesiredAmount(){return desiredAmount;}
    public int getFreq(){return paymentFreq;}
    public int getLoanDuration(){return loanDuration;}
    public Client getBorrower(){return borrower;}
    public Map<String, Integer> getLoaners() {return loaners;}
    public int getPaymentFreq() {return paymentFreq;}
    public int getInitialYAZ() {return initalYAZ;}

    public int getFinalYAZ() {return finalYAZ;}
    public int getCurrPayment() {return currPayment;}
    public double getCollectedAmount() {return collectedAmount;}
    public String getCategory() {return category;}
    public double amountEachPayment(){return (desiredAmount*((interest/100)+1))/(loanDuration/paymentFreq);}
    public LoanStatus getStatus() {return status;}
    public int getMissPayments() {return missPayments;}
    public String getName() {return name;}
    public boolean isPayedToday(){return payedToday;}
    public void payedToday(boolean val){payedToday=val;}
    public int getNumOfPaymentsMade() {return numOfPaymentsMade;}
    public void addNumOfPaymentsMade(int amount){numOfPaymentsMade+= amount;}
    public int getNumOfPaymentsLeft(){return  (loanDuration/paymentFreq) - numOfPaymentsMade;}
    public double ownerShipPrecantgePerPrice(int price){
        double realPrice = min(price,desiredAmount-collectedAmount);
        return desiredAmount/realPrice;

    }
}
