import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.*;

public class Loan {
    private Person borrower;
    Map<Person,Integer> loaners=new HashMap<>();

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


    InvestCategories category;
    //
    Loan(String loanName, Person borrower,String category,int desiredAmount, int loanDuration, int paymentFreq, double interest) {
        this.borrower = borrower;
        this.name = loanName;
        this.desiredAmount = desiredAmount;
        this.loanDuration = loanDuration;
        this.paymentFreq = paymentFreq;
        this.interest = interest;

        boolean invalidCategory=true;
        for(InvestCategories curr: InvestCategories.values()) {
            if (curr.displayName().equals(category)) {
                this.category = curr;
                invalidCategory = false;
            }
        }
        //Need to throw exception if invalidCategory==true!
    }
    public void addLoaner(Person loaner,int sum,TimeLine time){
        loaners.put(loaner,sum);
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
                "|Borrower name: "+borrower+
                "|Borrowing for : "+category+
                "|Loan Amount:" + desiredAmount+
                "|Loan duration: "+ loanDuration+
                "|Interest :" +interest+
                "|Frequency every "+paymentFreq+" YAZ"+
                "|Loan Status:" + status;
      if(status==LoanStatus.PENDING){
            res+=addPendingInfo();
        }

        if(status==LoanStatus.ACTIVE){
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
        return " Payed : "+payedAmount;
    }


    //Getters&Movers
    public void turnToActive(TimeLine time){
        initalYAZ=time.getTime();
        finalYAZ=initalYAZ+loanDuration;
    }
    public double getInterest(){return interest;}
    public void addPayedAmount(double sum){
        payedAmount+=sum;
    }
    public double getPayedAmount(){return payedAmount;}
    public int getDesiredAmount(){return desiredAmount;}
    public int getFreq(){return paymentFreq;}
    public int getLoanDuration(){return loanDuration;}
    public Person getBorrower(){return borrower;}

}
