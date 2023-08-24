package collections.Banking;
import collections.Banking.DTOS.ClientDummy;
import collections.Banking.DTOS.LoanDummy;
import java.util.List;

public interface BankAble {

    void buildBankFromXML(String path, String userName)throws Exceptions;

    void balanceAction(String clientName,int sum,int time);

    List<ClientDummy> getAllUsers();

    List<Integer> searchLoanWithFilters(String clientName, double minInterest,
                                               List<String> chosenCategories, int minDuration,
                                               int sumToInvest, int maxOpenLoans,
                                               int maxOwnerShip) throws Exceptions;

    int investFX(String clientName, int sum, List<LoanDummy> loanDummyList);

    ClientDummy getDummyClientByName(String name);

    void newDay();

    List<LoanDummy> getAllLoans();

    int getTime();

    List<String> getAllInvestCategories();

    List<LoanDummy> getAllClientBorrow(ClientDummy client);

    List<LoanDummy> getAllClientInvest(ClientDummy client);

    int payment(LoanDummy loanDummy);

    void payFullLoanAmount(LoanDummy loanDummy);


    LoanDummy createLoan(String loanName, String borrowerName, String category,
                         int desiredAmount, int loanDuration, int paymentFreq, double interest);

    void sellLoan(String clientName,LoanDummy loanDummy);

    Boolean getRewindMode();

    void setRewindMode(int YAZ);

    void stopRewindMode();

    String getHistory();

    List<LoanForSale> getLoanForSales();

    void buyLoan(String buyerName,LoanDummy loanDummy,String sellerName);
}