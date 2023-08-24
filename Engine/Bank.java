package collections.Banking;

import Time.TimeLine;
import collections.Banking.DTOS.ClientDummy;
import collections.Banking.DTOS.LoanDummy;
import com.google.gson.Gson;
import resources.src.jaxb.generated.AbsLoan;
import resources.src.jaxb.generated.SchemaV2;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Bank implements  BankAble {
    SchemaV2 schema;
    private Boolean rewindMode = false;
    private int rewindYAZ;
    public List<Loan> pending = new ArrayList<>();
    public List<Loan> finished = new ArrayList<>();
    public List<Loan> active = new ArrayList<>();
    public List<Loan> risk = new ArrayList<>();
    public List<LoanForSale> loanForSales = new ArrayList<>();
    public List<String> investCategories= new ArrayList<>();
    public Map<String, Client> users = new HashMap<>();
    private List<String> bankHistory = new ArrayList<>();
    private List<String> loanNames = new ArrayList<>();
    private TimeLine time = new TimeLine();

    @Override
    public Boolean getRewindMode() {
        return rewindMode;
    }
    @Override
    public void setRewindMode(int YAZ){
       // if(YAZ>getTime())
          //  return;
        rewindMode=true;
        rewindYAZ = YAZ;
    }
    @Override
    public void stopRewindMode(){
        rewindMode=false;
    }
    @Override
    public String getHistory(){
        return bankHistory.get(rewindYAZ);
    }
    public List<LoanForSale> getLoanForSales() {
        return loanForSales;
    }

    public void sellLoan(String clientName, LoanDummy loanDummy){
        LoanForSale loanForSale = new LoanForSale(loanDummyToLoan(loanDummy),getClientByName(clientName));
        for(LoanForSale loan : loanForSales)
            if(loan.owner.getName().equals(clientName) && loan.getProfit()==loanForSale.getProfit())
                return;
        loanForSales.add(loanForSale);
    }

    public void buyLoan(String buyerName,LoanDummy loanDummy,String sellerName){
        Loan relevantLoan = null;
        int movementAmount=0;
        LoanForSale toRemove = null;
        for(LoanForSale loan : loanForSales)
            if(sellerName.equals(loan.owner.getName()) && loanDummy.getName().equals(loanDummy.getName())) {
                relevantLoan = loan.loan;
                movementAmount = loan.getAmountOfSale();
                toRemove = loan;
            }
        int sellerAmount = relevantLoan.loaners.get(sellerName);
        relevantLoan.loaners.remove(sellerName);
        relevantLoan.loaners.put(buyerName,sellerAmount);
        balanceAction(buyerName,-movementAmount,time.getTime());
        balanceAction(sellerName,movementAmount,time.getTime());
        loanForSales.remove(toRemove);
    }
    //#1 Functions
    @Override
    public void buildBankFromXML(String path, String userName)  throws Exceptions {
        try {
            schema = new SchemaV2(path,investCategories);
            buildFromSchema(userName);
            schema = null;

        }
        catch (Exceptions err) {
            throw err;
        }
        catch (JAXBException err) {
            throw new Exceptions("Not XML file!");
        }
        catch(FileNotFoundException err){
            throw new Exceptions("Cant find this file!");
        }
    }


    private  void buildFromSchema(String userName) {
        List<AbsLoan> LoanList = schema.getAllLoans();
        investCategories.addAll(schema.getAllCategories());
        for (AbsLoan loan : LoanList) {
            if (investCategories.contains(loan.getAbsCategory()))
               try {
                   createLoan(loan.getId(), userName,
                           loan.getAbsCategory(), loan.getAbsCapital(),
                           loan.getAbsTotalYazTime(), loan.getAbsPaysEveryYaz(),
                           loan.getAbsIntristPerPayment());
               } catch (Exceptions err){
                throw err;
               }
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
    public void addNewClient(String userName, int bankBalance) {
        Client newClient = new Client(userName, bankBalance);
        newClient.sendMessage("Alternative-Banking System", getTime(),"Welcome to our app!");
        users.put(userName, newClient);
    }
    @Override
    public int getTime() {
        return time.getTime();
    }

    @Override
    public List<String> getAllInvestCategories() {
     return investCategories;
    }

    @Override
    public void newDay() {
        Boolean lastRewind = rewindMode;
        rewindMode = true;
        bankHistory.add(new Gson().toJson(this));
        rewindMode = lastRewind;
        time.moveTime();
        TimeLine prevDay = new TimeLine(time.getTime()-1);
        for(Loan loan:risk)
            if(loan.isPayDay(time)) {
                loan.addToDebt();
                loan.getBorrower().sendMessage(loan.getName(), getTime(), "This loan should be payed today!");
            }

        for (Loan loan : active){
            if (loan.isPayDay(time)) {
                loan.payedToday(false);
                loan.getBorrower().sendMessage(loan.getName(), getTime(), "This loan should be payed today!");
            }
            if (loan.isPayDay(prevDay) && loan.getCurrPayment() > loan.getNumOfPaymentsMade()){
                loan.status = LoanStatus.RISK;
                risk.add(loan);
                loan.addToDebt();
            }
        }
        active.removeAll(risk);
}
    @Override
    public int payment(LoanDummy loanDummy) {
        Loan loan = LoanDummyToLoan(loanDummy);
        loan.status = LoanStatus.ACTIVE;
        loan.payedToday(true);
        int numOfpays=1;
        if(loan.getMissPayments()>0)
            numOfpays = loan.getMissPayments();

        loan.addNumOfPaymentsMade(numOfpays);
       return paymentHelper(loan,numOfpays);
    }

    private int paymentHelper(Loan loan , int numOfpays){
        int totalSum = 0 ;
        if(risk.contains(loan)) {
            risk.remove(loan);
            active.add(loan);
        }
        for (Map.Entry<String, Integer> loaner : loan.loaners.entrySet()) {
            double repayAmount = (loaner.getValue()/loan.getDesiredAmount()) * loan.amountEachPayment();
            repayAmount*= numOfpays;

            getClientByName(loaner.getKey()).balanceAction(repayAmount, time.getTime());
            loan.getBorrower().balanceAction(-repayAmount, time.getTime());
            loan.addPayedAmount(repayAmount);
            totalSum+= repayAmount;
        }
        if (loan.getPayedAmount() + 0.1 >= loan.getDesiredAmount() * (1 + (loan.getInterest())/100)) {
            loan.status = LoanStatus.FINISHED;
            loan.finalYAZ = time.getTime();
            finished.add(loan);
            active.remove(loan);
        }
        return totalSum;
    }
    @Override
    public void payFullLoanAmount(LoanDummy loanDummy) {
        Loan loan = loanDummyToLoan(loanDummy);
        paymentHelper(loan,loan.getNumOfPaymentsLeft());
    }

    public Client getClientByName(String name) {
        return users.get(name);
    }
    public ClientDummy getDummyClientByName(String name) {
        if (users.get(name) == null)
            throw new Exceptions("Client:"+name+" does not exist!");

        return new ClientDummy(users.get(name));
    }

    public LoanDummy createLoan(String loanName, String borrowerName, String category,
                                int desiredAmount, int loanDuration, int paymentFreq, double interest) throws Exceptions {
            if(loanNames.contains(loanName))
                throw new Exceptions("Name of loan " +loanName+" is taken!");
            if(!investCategories.contains(category))
                investCategories.add(category);
            Client borrower = getClientByName(borrowerName);
            Loan loan =new Loan(loanName, borrower, category, desiredAmount, loanDuration, paymentFreq, interest);
            loanNames.add(loanName);
            pending.add(loan);
            return new LoanDummy(loan);
    }

    @Override
    public List<Integer> searchLoanWithFilters(String clientName, double minInterest,
                                               List<String> chosenCategories, int minDuration, int sumToInvest,
                                               int maxOpenLoans,int maxOwner) throws Exceptions {
        List<Integer> res = new ArrayList<>();
        if(maxOpenLoans == 0)
            maxOpenLoans = pending.size();

       if(chosenCategories.size()==0)
          chosenCategories = getAllInvestCategories();

        int finalMaxOpenLoans = maxOpenLoans;
        List<String> finalChosenCategories = chosenCategories;
        System.out.println(finalChosenCategories);
        List<Loan> loans = pending.stream().
                filter(t -> t.getInterest() >= minInterest &&
                        t.getLoanDuration() >= minDuration &&
                        !(t.getBorrower().getName().equals(clientName)) &&
                        t.ownerShipPrecantgePerPrice(sumToInvest) >= maxOwner &&
                        finalChosenCategories.contains(t.getCategory()) &&
                        getAmountOfOpenLoansByClientName(t.getBorrower().getName())<= finalMaxOpenLoans).
                        collect(Collectors.toList());
        for (Loan l : loans)
            res.add(pending.indexOf(l));

        Collections.sort(res);
        return res;
    }

    @Override
    public int investFX(String clientName, int sum, List<LoanDummy> loanDummyList) {
        Client loaner = getClientByName(clientName);
        List<Integer> loansIndexes = getLoanFromLoanDummyList(loanDummyList);

        int singleInvestAmount = sum / loansIndexes.size();
        int extra = splitEqually(sum, loansIndexes.size());
        int totalInvestSum = 0;
        for (int i : loansIndexes) {
            Loan loan = pending.get(i);
            int payAmount = singleInvestAmount;
            if (singleInvestAmount > (loan.getDesiredAmount() - loan.getCollectedAmount())) {
                payAmount = (int) (loan.getDesiredAmount()-loan.getCollectedAmount());
                extra = 0;
            }
            loan.addLoaner(loaner, payAmount + extra, time);
            totalInvestSum += payAmount + extra;
        }
        removeAllActiveFromPending();
        return totalInvestSum;
    }
    private Loan loanDummyToLoan(LoanDummy dummy){
        Loan res = null;
        for(Loan loan : active)
            if(loan.getName().equals(dummy.getName()) &&
                    loan.getBorrower().getName().equals(dummy.getBorrower().getName()))
                res = loan;
        for(Loan loan : risk)
            if(loan.getName().equals(dummy.getName()) &&
                    loan.getBorrower().getName().equals(dummy.getBorrower().getName()))
                res = loan;

        return res;
    }
    private List<Integer> getLoanFromLoanDummyList(List<LoanDummy> loanDummyList){
        List<Integer> indx = new ArrayList<>();
        List<String> names = new ArrayList<>();

        for(LoanDummy loan : loanDummyList)
            names.add(loan.getName());

        for(Loan loan : pending)
            if(names.contains(loan.getName()))
                indx.add(pending.indexOf(loan));

        return indx;
    }

    private void removeAllActiveFromPending(){
            for(Loan loan : pending)
                if (loan.getStatus() == LoanStatus.ACTIVE) {
                    loan.turnToActive(time);
                    active.add(loan);
                }
            pending.removeAll(active);
        }

    @Override
    public void balanceAction(String clientName, int sum, int time) throws Exceptions {
          Client client=getClientByName(clientName);
          if(client.getBankBalance()+sum<0)
              throw new Exceptions("Client :"+clientName +" can withdraw maximum of : "+client.getBankBalance());
          client.balanceAction(sum,time);
    }

    @Override
    public List<ClientDummy> getAllUsers() {
        List<ClientDummy> res=new ArrayList<>();
        for(Client c: users.values())
            res.add(new ClientDummy(c));
        return res;
    }
    private int splitEqually(int sum,int numOfInvests){
        if (sum % numOfInvests != 0)
            return sum % numOfInvests;

        return 0;
    }

    @Override
    public List<LoanDummy> getAllClientInvest(ClientDummy client){
        List<LoanDummy> res = new ArrayList<>();
        for(LoanDummy loan:getAllLoans())
            for(String cl:loan.loaners.keySet())
                if(cl.equals(client.getName()))
                    res.add(loan);
        return res;
    }
    @Override
    public List<LoanDummy> getAllClientBorrow(ClientDummy client){
        List<LoanDummy> res = new ArrayList<>();
        for(LoanDummy loan:getAllLoans())
            if(loan.getBorrower().getName().equals(client.getName()))
                res.add(loan);

        return res;
    }
    private Loan LoanDummyToLoan(LoanDummy loanDummy) {
        Loan loan = null;
        for(Loan temp : risk)
            if(temp.getName().equals(loanDummy.getName()) &&
                    temp.getBorrower().getName().equals(loanDummy.getBorrower().getName()))
                loan = temp;
        for(Loan temp : active)
            if(temp.getName().equals(loanDummy.getName()) &&
                    temp.getBorrower().getName().equals(loanDummy.getBorrower().getName()))
                loan = temp;
        return loan;
    }
    public int getAmountOfOpenLoansByClientName(String name)
    {
        int cn =0;
        for(Loan loan : pending)
            if(loan.getBorrower().getName().equals(name))
                cn++;
        for(Loan loan : risk)
            if(loan.getBorrower().getName().equals(name))
                cn++;
        for(Loan loan : active)
            if(loan.getBorrower().getName().equals(name))
                cn++;

        return cn;
    }
}
