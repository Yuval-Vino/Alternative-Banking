package collections.Banking;

import java.util.*;
import java.util.stream.Collectors;
import Time.TimeLine;
public class Bank {
    public List<Loan> pending = new ArrayList<>();
    public List<Loan> finished = new ArrayList<>();
    public  List<Loan> active = new ArrayList<>();
    public Map<String,Client> users = new HashMap<>();
    private TimeLine time=new TimeLine();

    public void addNewClient(String person,int bankBalance)
    {
        Client temp=new Client(person,bankBalance);
        users.put(person,temp);
    }

    public int getTime(){return time.getTime();}

    public void newDay(){
        time.moveTime();
        for (Loan i : active) {
            if( i.isPayDay(time))
                payment(i);
        }
        active.removeAll(finished);
    }
    public void payment(Loan loan) {

        for (Map.Entry<Client, Integer> loaner : loan.loaners.entrySet()) {
            double repayAmount = (loaner.getValue() / (loan.getLoanDuration() / loan.getFreq())) * (1 + loan.getInterest());
            //            (   Total investment / ( duration/freq ) ) * interest
            loaner.getKey().balanceAction(repayAmount, time.getTime());
            loan.getBorrower().balanceAction(-repayAmount,time.getTime());
            loan.addPayedAmount(repayAmount) ;
        }

        if(loan.getPayedAmount() + 0.1 >=loan.getDesiredAmount()*(1+loan.getInterest())) {
            loan.status = LoanStatus.FINISHED;
            finished.add(loan);
        }
    }

    public Client getClientByName(String name){
        return users.get(name);
    }

    public void createLoan(String loanName, String name, String category, int desiredAmount, int loanDuration, int paymentFreq, double interest) {
        //Client borrower=getClientByName(name);
        Client borrower=users.get(name);
        pending.add(new Loan(loanName, borrower, category,desiredAmount, loanDuration, paymentFreq, interest));
    }

    public List<Loan> searchLoanWithFilters(String clientName, double minInterest, int maxDuration, int sumToInvest) {
        int count = 0;
        Client seeker=users.get(clientName);
        if(seeker.getBankBalance() < sumToInvest){
            //exception
            return null;
        }

        List<Loan> loans=pending.stream().
                filter(t->t.getInterest()>=minInterest&& t.getLoanDuration()<=maxDuration).
                collect(Collectors.toList());
        System.out.println(loans);
    return loans;}
     //                    A,B,C,D,E       B,D,E                                        0,2
    public void invest(String clientName, int sum, List<Loan> LoansWithFilters, List<Integer>indexes) {
        Client loaner = users.get(clientName);
        Collections.sort(indexes);
        int singleInvestAmount = sum / indexes.size();

        for (int i : indexes){
            LoansWithFilters.get(i).addLoaner(loaner, singleInvestAmount,time);
            loaner.balanceAction(-singleInvestAmount,time.getTime());
            if (LoansWithFilters.get(i).status == LoanStatus.ACTIVE) {
                LoansWithFilters.get(i).turnToActive(time);
                active.add(LoansWithFilters.get(i));
                pending.remove(LoansWithFilters.get(i));
            }
        }
    }
}
