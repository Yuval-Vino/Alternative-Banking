package collections.Banking.DTOS;

import collections.Banking.Client;
import collections.Banking.Loan;
import collections.Banking.LoanStatus;

import java.util.HashMap;
import java.util.Map;

public class LoanDummy {
    protected ClientDummy borrower;
    public Map<ClientDummy,Integer> loaners=new HashMap<>();
    private String name;
    private int desiredAmount;
    private double collectedAmount = 0;
    private double interest;
    private int currPayment = 1;
    private int paymentFreq;
    LoanStatus status = LoanStatus.PENDING;
    //dates
    private int initalYAZ;
    private int finalYAZ;
    private int loanDuration;
    private double payedAmount = 0;
    private int missPayments = 0;
    private String category;
    public LoanDummy(Loan l){
        borrower = new ClientDummy(l.getBorrower());
        for(Client client:l.getLoaners().keySet()){
            loaners.put(new ClientDummy(client),l.getLoaners().get(client));
        }
        name = l.getName();
        desiredAmount = l.getDesiredAmount();
        collectedAmount = l.getCollectedAmount();
        interest = l.getInterest();
        currPayment = l.getCurrPayment();
        paymentFreq = l.getPaymentFreq();
        status = l.getStatus();
        initalYAZ = l.getInitalYAZ();
        finalYAZ = l.getFinalYAZ();
        loanDuration = l.getLoanDuration();
        payedAmount = l.getPayedAmount();
        category=l.getCategory();
        missPayments = l.getMissPayments();
    }
    public ClientDummy getBorrower(){return borrower;}
    Map<ClientDummy,Integer> getLoaners(){return loaners;}
    public String getName(){return name;}
    int getDesiredAmount(){return desiredAmount;}
    double getCollectedAmount(){return collectedAmount;}
    double getInterest(){return interest;}
    public String getStatus() {return status.toString();}
    public double getPayedAmount() {return payedAmount;}
    public int getCurrPayment() {return currPayment;}
    public int getFinalYAZ() {return finalYAZ;}
    public int getInitalYAZ() {return initalYAZ;}
    public int getLoanDuration() {return loanDuration;}
    public int getPaymentFreq() {return paymentFreq;}
    public int getMissPayments(){return missPayments;}
    public double amountEachPayment(){return (desiredAmount*((interest/100)+1))/(loanDuration/paymentFreq);}

    public String toString() {
        String res = "Loan name: " + name +
                " |Borrower name: " + borrower +
                " |Borrowing for : " + category +
                " |Loan Amount:" + desiredAmount +
                " |Loan duration: " + loanDuration +
                " |Interest :" + interest + "%" +
                " |Frequency every " + paymentFreq + " YAZ" +
                " |Loan Status: " + status+" ";

        if (status == LoanStatus.PENDING) {res += addPendingInfo();}
        if (status == LoanStatus.ACTIVE) {res += addActiveInfo();}
        if (status == LoanStatus.FINISHED) {res += addFinishedInfo();}
        return res;
    }
    private String addPendingInfo(){
        String info;
                if(loaners.size()==0)
                     info= "No investors yet ";
                else info=loaners.toString();
        return info+"| Collected: " +collectedAmount + " rest amount to collect " +(desiredAmount - collectedAmount);
    }
    private String addActiveInfo(){
        return " |Payed : "+payedAmount + " |next Pay day is: " + (paymentFreq*currPayment + initalYAZ) +
                " |the total payment left to pay " + (desiredAmount * (interest/100 + 1) - payedAmount);
    }
    private String addFinishedInfo(){
        return " |intial yaz: " + initalYAZ +" |finish yaz: " + finalYAZ;
    }
    private String addRiskedInfo(){
        return " |Missed payments: " + getMissPayments() +" |debt to pay: " + getMissPayments()*amountEachPayment();
    }
}

