import java.util.ArrayList;
import java.util.List;
import collections.Banking.*;
public class Tester {


    public static void main(String[] args) {

        Bank bank=new Bank();
        bank.addNewClient("p1",1500);
        bank.addNewClient("p2",1500);
        bank.addNewClient("p3",1500);
        bank.addNewClient("p4",1500);
        bank.addNewClient("p5",1500);
        bank.createLoan("Hagiga","p1","Happy event",500,5,1,0.10);
        bank.createLoan("Hagiga","p2","Renovate",800,5,1,0.10);
        //bank.createLoan("Hagiga",p4,"Renovate",800,5,1,0.10);
        //bank.createLoan("Hagiga",p3,"Renovate",500,5,1,0.10);
      //  bank.createLoan("Hagiga",p4,"Renovate",500,5,1,0.10);
        List<Loan> a = bank.searchLoanWithFilters("p3",0.05,8,400);

        List<Integer> ind=new ArrayList<>();
        ind.add(0);
        ind.add(1);
        bank.invest("p3",100,a,ind);
        List<Integer> ind2=new ArrayList<>();
        ind2.add(0);
        bank.invest("p4",450,a,ind2);

        List<Loan> b = bank.searchLoanWithFilters("p5",0.05,8,350);
        bank.invest("p5",450,b,ind2);

       for (int i=0;i<7;i++)
       {
           for(Loan curr : bank.pending) {
               System.out.println("Pendings day number #" + bank.getTime());
               System.out.println(curr);
           }
        for(Loan curr : bank.active) {
            System.out.println("Actives day number #" + bank.getTime());
            System.out.println(curr);
        }
           for(Loan curr : bank.finished) {
               System.out.println("finished day number #" + bank.getTime());
               System.out.println(curr);
           }
            System.out.println(bank.getClientByName("p1").toStringWithBalance());
            System.out.println(bank.getClientByName("p2").toStringWithBalance());
            System.out.println(bank.getClientByName("p3").toStringWithBalance());
            System.out.println(bank.getClientByName("p4").toStringWithBalance());

           bank.newDay();
       }







}}