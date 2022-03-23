import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Loan {
    Person borrower;
    Map<Person,Integer> loaners;

    String name;
    int desiredAmount;
    int collectedAmount = 0;
    int totalRepayAmount = 0;
    int interest;
    int currPayment = 1;
    int paymentFreq;
    LoanStatus status = LoanStatus.PENDING;
    //dates
    int initalYAZ;
    int finalYAZ;
    int loanDuration;
    int payedAmount = 0;
    InvestCategories category;
    //
    Loan(String loanName, Person borrower,String category,int desiredAmount, int loanDuration, int paymentFreq, int interest) {
        this.borrower = borrower;
        this.name = loanName;
        this.desiredAmount = desiredAmount;
        this.loanDuration = loanDuration;
        this.paymentFreq = paymentFreq;
        this.interest = interest;
        this.category=InvestCategories.valueOf(category);
    }
    public void addLoaner(Person loaner,int sum,TimeLine time){
        loaners.put(loaner,sum);
        collectedAmount += sum;
        if(collectedAmount == desiredAmount){
            status = LoanStatus.ACTIVE;
            borrower.account.balanceAction(desiredAmount);
        }

    }
    public boolean isPayDay(TimeLine time){
        if(initalYAZ + currPayment*paymentFreq == time.time){
            currPayment++;
            return true;
        }
        return false;
    }
}
