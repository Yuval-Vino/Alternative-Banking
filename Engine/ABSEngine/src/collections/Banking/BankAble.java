package collections.Banking;

import collections.Banking.DTOS.ClientDummy;
import collections.Banking.DTOS.LoanDummy;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.List;

public interface BankAble {

    //For option1
    void buildBankFromXML(String path)throws JAXBException, FileNotFoundException;

    void balanceAction(String clientName,int sum,int time);
    //For option 2
    List<ClientDummy> getAllUsers();

    List<Integer> searchLoanWithFilters(String clientName, double minInterest,List<Integer> category, int maxDuration, int sumToInvest) throws Exceptions;
    String getSpeceficLoan(int loanIndex);

    public void invest(String clientName, int sum, List<Integer>loansIndexes);
    public void newDay();
    public List<LoanDummy> getAllLoans();
    public int getTime();
    public List<String> getAllInvestCategories();
    public List<LoanDummy> getAllClientBorrow(ClientDummy client);
    public List<LoanDummy> getAllClientInvest(ClientDummy client);
}

