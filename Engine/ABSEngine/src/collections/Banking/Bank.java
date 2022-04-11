package collections.Banking;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import Time.TimeLine;
import XMLReader.SchemaBasedXML;
import collections.Banking.DTOS.ClientDummy;
import collections.Banking.DTOS.LoanDummy;
import schema.generated.AbsCustomer;
import schema.generated.AbsLoan;

import javax.xml.bind.JAXBException;

public class Bank implements  BankAble {
    SchemaBasedXML schema;
    public List<Loan> pending = new ArrayList<>();
    public List<Loan> finished = new ArrayList<>();
    public List<Loan> active = new ArrayList<>();
    public List<Loan> risk = new ArrayList<>();
    public List<String> investCategories= new ArrayList();
    public Map<String, Client> users = new HashMap<>();

    private TimeLine time = new TimeLine();


    //#1 Functions
    @Override
    public void buildBankFromXML(String Path) throws JAXBException, FileNotFoundException {
        try {
            schema = new SchemaBasedXML(Path);
            resetBank();
            buildFromSchema();
        }
        catch (Exceptions err) {
            throw err;
        }
        catch (JAXBException err) {
            throw new Exceptions("Not xml file!");
        }
        catch(FileNotFoundException err){
            throw new Exceptions("Cant find this file!");
        }
    }

    private void buildFromSchema(){
        List<AbsCustomer> ClientList=schema.getAllCustomers();
        List<AbsLoan> LoanList = schema.getAllLoans();
        investCategories=schema.getAllCategories();
        for (AbsCustomer client:ClientList) {
            addNewClient(client.getName(),client.getAbsBalance());
        }
        for (AbsLoan loan:LoanList) {
            createLoan(loan.getId(),loan.getAbsOwner(),
                    loan.getAbsCategory(),loan.getAbsCapital(),
                    loan.getAbsTotalYazTime(),loan.getAbsPaysEveryYaz(),
                    loan.getAbsIntristPerPayment());
        }

        pending.sort(Comparator.comparing(Loan::amountEachPayment));
    }

    //#2 Functions
@Override
    public List<LoanDummy> getAllLoans(){
        List<LoanDummy>res = new ArrayList<>();
        for(Loan loan : pending)
            res.add(new LoanDummy(loan));
        for(Loan loan : active)
            res.add(new LoanDummy(loan));
        for(Loan loan : risk)
            res.add(new LoanDummy(loan));
        for(Loan loan : finished)
            res.add(new LoanDummy(loan));
        return res;
    }
    public void addNewClient(String person, int bankBalance) {
        Client temp = new Client(person, bankBalance);
        users.put(person, temp);
    }
@Override
    public int getTime() {
        return time.getTime();
    }

    @Override
    public List<String> getAllInvestCategories() {
     return investCategories;
    }

    public void newDay() {
        time.moveTime();
        for (Loan i : active) {
            if (i.isPayDay(time))
                payment(i,false);
        }
        active.removeAll(finished);
        active.removeAll(risk);
        for(Loan l:risk){
            if(l.isPayDay(time)){
                l.addToDebt();
                if(l.getBorrower().getBankBalance() > l.getMissPayments()*l.amountEachPayment()){
                    l.status=LoanStatus.ACTIVE;
                    payment(l,true);
                    active.add(l);

                }
            }
        }
        risk.removeAll(active);
        risk.removeAll(finished);
    }

    private void payment(Loan loan,boolean isDebt) {
        if(loan.amountEachPayment()>loan.getBorrower().getBankBalance()) {
            risk.add(loan);
            loan.status=LoanStatus.RISK;
            return;
        }
        int numOfpays=1;
        if(isDebt){
            numOfpays=loan.getMissPayments()+1;
        }
        for (Map.Entry<Client, Integer> loaner : loan.loaners.entrySet()) {
            double repayAmount = ((loaner.getValue() / (loan.getLoanDuration() / loan.getFreq())) * ((loan.getInterest() / 100) + 1))*(numOfpays);
            //            (   Total investment / ( duration/freq ) ) * interest
            loaner.getKey().balanceAction(repayAmount, time.getTime());
            loan.getBorrower().balanceAction(-repayAmount, time.getTime());
            loan.addPayedAmount(repayAmount);
        }

        if (loan.getPayedAmount() + 0.1 >= loan.getDesiredAmount() * (1 + (loan.getInterest())/100)) {
            loan.status = LoanStatus.FINISHED;
            loan.finalYAZ = time.getTime();
            finished.add(loan);
        }
    }

    private Client getClientByName(String name) {
        if (users.get(name) == null)
            throw new Exceptions("Client:"+name+" does not exist!");

        return users.get(name);
    }

    public void createLoan(String loanName, String borrowerName, String category,
                           int desiredAmount, int loanDuration, int paymentFreq, double interest) throws Exceptions {

            if(investCategories.indexOf(category)==-1){
                throw new Exceptions(category +" is not a valid category!");
            }
            Client borrower = getClientByName(borrowerName);

            Loan loan =new Loan(loanName, borrower, category, desiredAmount, loanDuration, paymentFreq, interest);
            pending.add(loan);
    }

    @Override
    public List<Integer> searchLoanWithFilters(String clientName, double minInterest,List<Integer> category, int minDuration, int sumToInvest) throws Exceptions {
        List<Integer> res = new ArrayList<>();
        int count = 0;
        Client seeker = getClientByName(clientName);

        if (seeker.getBankBalance() < sumToInvest) {
            throw new Exceptions("Client: "+seeker.toStringWithBalance()+" cant invest "+sumToInvest);
        }
        List<String> chosenCategories = new ArrayList<>();
        if(category.size()==0)
            chosenCategories=getAllInvestCategories();
        else{
            for (int indx:category){
                chosenCategories.add(getAllInvestCategories().get(indx));
            }
        }
//                    t.getCategory().displayName().equals(category) &&
        List<String> finalChosenCategories = chosenCategories;
        List<Loan> loans = pending.stream().
                filter(t -> t.getInterest() >= minInterest &&
                        t.getLoanDuration() >= minDuration &&
                        !(t.getBorrower().getName().equals(clientName)) &&
                        finalChosenCategories.indexOf(t.getCategory())!=-1).
                        collect(Collectors.toList());
        for (Loan l : loans) {
            res.add(pending.indexOf(l));
        }
        Collections.sort(res);
        return res;
    }

    @Override
    public String getSpeceficLoan(int loanIndex) {
        return null;
    }

    @Override
    public void invest(String clientName, int sum, List<Integer> loansIndexes) {
        int overTheDisierdAmount = 0;
        int toInvest = 0;
        Client loaner = getClientByName(clientName);
        int singleInvestAmount = sum / loansIndexes.size();
        int extra = splitEqually(sum, loansIndexes.size());
        for (int i : loansIndexes) {

            if (singleInvestAmount > (pending.get(i).getDesiredAmount() - pending.get(i).getCollectedAmount())) {
                overTheDisierdAmount = (int) (singleInvestAmount - pending.get(i).getDesiredAmount()+   pending.get(i).getCollectedAmount());
                extra = 0;
            }
            pending.get(i).addLoaner(loaner,
                    singleInvestAmount + extra - overTheDisierdAmount, time);

        }
        removeAllActiveFromPending();
    }

    private void removeAllActiveFromPending(){
            for(Loan loan : pending){
                if (loan.getStatus() == LoanStatus.ACTIVE) {
                    loan.turnToActive(time);
                    active.add(loan);
                }
            }
            pending.removeAll(active);
        }




    @Override
    public void balanceAction(String clientName, int sum, int time) {

          Client client=getClientByName(clientName);
          if(client.getBankBalance()+sum<0)
              throw new Exceptions("Client :"+clientName +" can withdraw maximum of : "+client.getBankBalance());
          client.balanceAction(sum,time);
    }

    @Override
    public List<ClientDummy> getAllUsers() {
        List<ClientDummy> res=new ArrayList<>();
        for(Client c: users.values()){
            res.add(new ClientDummy(c));
        }
        return res;
    }
    private int splitEqually(int sum,int numOfInvests){
        if (sum % numOfInvests != 0){
            return sum % numOfInvests;
        }
        return 0;
    }
    private void resetBank()
    {
        pending.clear();
        finished.clear();
        active.clear();
        risk.clear();
        users.clear();
        time.resetTime();
    }
    @Override
    public List<LoanDummy> getAllClientInvest(ClientDummy client){
        List<LoanDummy> res = new ArrayList<>();
        for(LoanDummy loan:getAllLoans()){
            for(ClientDummy cl:loan.loaners.keySet())
            if(cl.getName().equals(client.getName())){
                res.add(loan);
            }
        }
        return res;
    }
    @Override
    public List<LoanDummy> getAllClientBorrow(ClientDummy client){
        List<LoanDummy> res = new ArrayList<>();
        for(LoanDummy loan:getAllLoans()){
            if(loan.getBorrower().getName().equals(client.getName())){
                res.add(loan);
            }
        }
        return res;
    }
}
