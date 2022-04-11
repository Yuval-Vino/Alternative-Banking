package collections.Banking;

import java.util.*;
import Time.TimeLine;

public class Loan {
    protected Client borrower;
    Map<Client,Integer> loaners=new HashMap<>();

    private String name;
    private int desiredAmount;
    private double collectedAmount = 0;
    private double interest;
    private int currPayment = 1;
    private int paymentFreq;
    LoanStatus status = LoanStatus.PENDING;
    //dates
    private int initalYAZ;
    public int finalYAZ;
    private int loanDuration;
    private double payedAmount = 0;
    private int missPayments = 0;


    String category;
    //
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
        loaners.putIfAbsent(loaner,0);
        loaners.put(loaner,loaners.get(loaner)+sum);
        loaner.balanceAction(-sum, time.getTime());
        collectedAmount += sum;
        if(collectedAmount == desiredAmount){
            status = LoanStatus.ACTIVE;
            borrower.balanceAction(desiredAmount,time.getTime());
        }

    }
    public boolean isPayDay(TimeLine time){
        if(initalYAZ + (currPayment*paymentFreq) == time.getTime()){
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
      if(status== LoanStatus.PENDING){
            res+=addPendingInfo();
        }

        if(status== LoanStatus.ACTIVE){
            res+=addActiveInfo();
        }
    //   if(status==LoanStatus.RISK){
       //     res+=addRiskInfo()
     //   }
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
    }
    public int getPayedAmount(){return (int)payedAmount;}
    public int getDesiredAmount(){return desiredAmount;}
    public int getFreq(){return paymentFreq;}
    public int getLoanDuration(){return loanDuration;}
    public Client getBorrower(){return borrower;}
    public Map<Client, Integer> getLoaners() {return loaners;}
    public int getPaymentFreq() {return paymentFreq;}
    public int getInitalYAZ() {return initalYAZ;}

    public int getFinalYAZ() {return finalYAZ;}
    public int getCurrPayment() {return currPayment;}
    public double getCollectedAmount() {return collectedAmount;}
    public String getCategory() {return category;}
    public double amountEachPayment(){return (desiredAmount*((interest/100)+1))/(loanDuration/paymentFreq);}
    public LoanStatus getStatus() {return status;}
    public int getMissPayments() {return missPayments;}
    public String getName() {return name;}

}
