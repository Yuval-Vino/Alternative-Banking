import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.*;

public class Loan {
    Person borrower;
    Map<Person,Integer> loaners=new HashMap<>();

    String name;
    int desiredAmount;
    double collectedAmount = 0;
    double interest;
    int currPayment = 1;
    int paymentFreq;
    LoanStatus status = LoanStatus.PENDING;
    //dates
    int initalYAZ;
    int finalYAZ;
    int loanDuration;
    double payedAmount = 0;
    InvestCategories category;
    //
    Loan(String loanName, Person borrower,String category,int desiredAmount, int loanDuration, int paymentFreq, double interest) {
        this.borrower = borrower;
        this.name = loanName;
        this.desiredAmount = desiredAmount;
        this.loanDuration = loanDuration;
        this.paymentFreq = paymentFreq;
        this.interest = interest;
        this.category = InvestCategories.valueOf(category.toUpperCase());

    }
    public void addLoaner(Person loaner,int sum,TimeLine time){
        loaners.put(loaner,sum);
        collectedAmount += sum;
        if(collectedAmount == desiredAmount){
            status = LoanStatus.ACTIVE;
            borrower.balanceAction(desiredAmount,time.time);
        }

    }
    public boolean isPayDay(TimeLine time){
        if(initalYAZ + (currPayment*paymentFreq) == time.time){
            currPayment++;
            return true;
        }
        return false;
    }
    @Override
    public String toString()
    {
        String res= "Borrower name: "+name+
                "|Borrowing for : "+category+
                "|Interest :" +interest+
                "|Loan duration: "+ loanDuration+
                "|Loan Amount:" + desiredAmount+
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

        return loaners.toString()+"| Collected: " +collectedAmount + "rest amount to collect " +(desiredAmount - collectedAmount);
        // need to orginaze the to string not to print the bank account/
    }
    private String addActiveInfo(){
        return " Payed : "+payedAmount;
    }

}
