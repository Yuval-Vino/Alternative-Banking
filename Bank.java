import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bank {
    List<Loan> pending = new ArrayList<>();
    List<Loan> finished = new ArrayList<>();
    List<Loan> active = new ArrayList<>();
    List<Person> users = new ArrayList<>();
    TimeLine time=new TimeLine();



    public void newDay(){
        time.moveTime();
        for (Loan i:active) {
            if( i.isPayDay(time))
                payment(i);

        }
    }
    public void payment(Loan loan){
        //for (Person name:loan.loaners.
         //    ) {

        }


















    public void createLoan(String loanName, Person borrower,String category, int desiredAmount, int loanDuration, int paymentFreq, int interest) {
        pending.add(new Loan(loanName, borrower, category,desiredAmount, loanDuration, paymentFreq, interest));
    }

    public List<Loan> searchLoanWithFilters(Person seeker,int minInterest, int minDuration,int sumToInvest) {
        int count = 0;
        if(seeker.getBankBalance() < sumToInvest){
            //exception
            return null;
        }
        List<Loan> loans = new ArrayList<>();
        for (Loan loan : pending)
            if (loan.interest >= minInterest && loan.loanDuration >= minDuration) {
                loans.add(loan);
                System.out.println(count + "." + loan.toString());
            }

    return loans;}
     //                    A,B,C,D,E       B,D,E                                        0,2
    public void invest(Person loaner,int sum,List<Loan> LoansWithFilters,List<Integer>indexes) {

        Collections.sort(indexes);
        int indexCN = 0;
        int singleInvestAmount = sum / indexes.size();
        for (Loan i : pending) {
            //Need to override equals in loans and person
            if (i.equals(LoansWithFilters.get(indexes.get(indexCN)))) {
                i.addLoaner(loaner, singleInvestAmount,time);
                loaner.account.balanceAction(-singleInvestAmount);
                if (i.status == LoanStatus.ACTIVE) {
                    active.add(i);
                    pending.remove(i);
                }
                indexCN++;
            }
        }
    }
}
