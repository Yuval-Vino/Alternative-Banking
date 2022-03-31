import java.util.*;
import java.util.stream.Collectors;

public class Bank {
    List<Loan> pending = new ArrayList<>();
    List<Loan> finished = new ArrayList<>();
    List<Loan> active = new ArrayList<>();
    List<Person> users = new ArrayList<>();
    TimeLine time=new TimeLine();



    public void newDay(){
        time.moveTime();
        //time=2
        for (Loan i : active) {
            if( i.isPayDay(time))
                payment(i);
        }
        active.removeAll(finished);
    }
    public void payment(Loan loan) {

        for (Map.Entry<Person, Integer> loaner : loan.loaners.entrySet()) {
            double repayAmount = (loaner.getValue() / (loan.loanDuration / loan.paymentFreq)) * (1 + loan.interest);
            //            (   Total investment / ( duration/freq ) ) * interest
            loaner.getKey().balanceAction(repayAmount, time.time);
            loan.borrower.balanceAction(-repayAmount,time.time);
            loan.payedAmount += repayAmount;
        }

        if(loan.payedAmount + 0.1 >=loan.desiredAmount*(1+loan.interest)) {
            loan.status=LoanStatus.FINISHED;
            finished.add(loan);
        }
    }

    public void createLoan(String loanName, Person borrower,String category, int desiredAmount, int loanDuration, int paymentFreq, double interest) {
        pending.add(new Loan(loanName, borrower, category,desiredAmount, loanDuration, paymentFreq, interest));
    }

    public List<Loan> searchLoanWithFilters(Person seeker,double minInterest, int maxDuration,int sumToInvest) {
        int count = 0;
        if(seeker.getBankBalance() < sumToInvest){
            //exception
            return null;
        }

        List<Loan> loans=pending.stream().
                filter(t->t.interest>=minInterest&& t.loanDuration<=maxDuration).
                collect(Collectors.toList());
        System.out.println(loans);
    return loans;}
     //                    A,B,C,D,E       B,D,E                                        0,2
    public void invest(Person loaner,int sum,List<Loan> LoansWithFilters,List<Integer>indexes) {
        Collections.sort(indexes);
        int singleInvestAmount = sum / indexes.size();

        for (int i : indexes){
            LoansWithFilters.get(i).addLoaner(loaner, singleInvestAmount,time);
            loaner.balanceAction(-singleInvestAmount,time.time);
            if (LoansWithFilters.get(i).status == LoanStatus.ACTIVE) {
                LoansWithFilters.get(i).initalYAZ=time.time;
                LoansWithFilters.get(i).finalYAZ=time.time+LoansWithFilters.get(i).loanDuration;
                active.add(LoansWithFilters.get(i));
                pending.remove(LoansWithFilters.get(i));
            }
        }
    }
}