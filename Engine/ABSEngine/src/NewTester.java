import collections.Banking.Bank;
import collections.Banking.Exceptions;
import collections.Banking.Loan;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class NewTester {
    public static void main(String[] args) throws JAXBException, FileNotFoundException {
        Bank b = new Bank();

       try{ b.buildBankFromXML("C:/Users/Yuval/Desktop/Alternative-Banking/src/resources/ex1-big (2).xml");}
       catch(Exceptions err){
           System.out.println(err);
       }

      try{ b.createLoan("Avrum","Avrum","Happy 22Event",500,5,5,5);}
         catch(Exceptions err){
           System.out.println(err);
       }/*
        for (Loan curr:b.pending) {
            System.out.println(curr);
        }
        for(String name : b.users.keySet()){
            System.out.println(b.users.get(name).toStringWithBalance());
        }

        try {
            b.searchLoanWithFilters("Menash",1,100,15000);
           }
        catch(Exceptions err){
            System.out.println(err);
        }
    }*/
}}
