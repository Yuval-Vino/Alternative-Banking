package UI;

import collections.Banking.*;
import collections.Banking.DTOS.*;


import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Ui2Engine {
    public void callingToEngineFunc(char option, BankAble b) throws JAXBException, FileNotFoundException {
        switch (option) {
            case '1':
                option1(b);
                break;
            case '2':
                option2(b);
                break;
            case '3':
                option3(b);
                break;
            case '4':
                option4(b, true);
                break;
            case '5':
                option4(b, false);
                break;
            case '6':
                option6(b);
                break;
            case '7':
                option7(b);
                break;
            case '8':
                System.exit(1);
        }

    }

    public void option1(BankAble b) throws JAXBException, FileNotFoundException {
        System.out.println("Please enter a valid XML path:\n");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
       try{ b.buildBankFromXML(input);}
       catch(Exceptions err){
           System.out.println(err);
           option1(b);
           return;
        }
    }

    public void option2(BankAble b) {
        for(LoanDummy loan : b.getAllLoans())
        System.out.println(loan);
    }

    public void option3(BankAble b) {
        for (ClientDummy client : b.getAllUsers()) {
            System.out.println("Client name: " + client.getName());

            if(client.getAccount().getMovements().size()==0){
                System.out.println("No movements yet\n");
            }

            else{
                System.out.println("Movements: ");
            for(AccountMovementDummy movementDummy:client.getAccount().getMovements())
            System.out.println(movementDummy);
        }
            if(b.getAllClientBorrow(client).size()==0){
                System.out.println("Not borrowing yet\n");
            }
            else{
                System.out.println("Borrowing from: ");
            for(LoanDummy loan: b.getAllClientBorrow(client)) {
                System.out.println(loan);
            }
            }
            if(b.getAllClientInvest(client).size()==0){
                System.out.println("Not investing yet\n");
            }
            else{
                System.out.println("investing in: ");
                for(LoanDummy loan: b.getAllClientInvest(client)) {
                    System.out.println(loan);
                }
            }
        }
    }

    public void option4(BankAble b, boolean deposit) {
        ClientDummy invester=   getClientNameFromUser(b);
        printRelevantQuestion("Enter sum",false);
        int amount = getValidNumber();

        if (!deposit)
           amount=-amount;

           try{ b.balanceAction(invester.getName(), amount, b.getTime());}
           catch(Exceptions err){
               System.out.println(err+"\n try again");
               option4(b,deposit);
               return;
           }
    }


    public void option6(BankAble b) {
        ClientDummy invester=   getClientNameFromUser(b);
        //-----get sum to invest
        printRelevantQuestion("sum to invest", true);
        int totalAmount = checkValidAmount((int)invester.getAccount().getBalance(),false);
        //--------------------------------------------------------------

        // get how many categoris for invest
        pr1intAllInvestCategories(b);
        printRelevantQuestion("how many categories you like to pick (if you want them all insert 0)",false);
        int totalCategoriesAmount = checkValidAmount(b.getAllInvestCategories().size(),true);
        //--------------------------------------------------------------

        //get specific indexs of categoris for invesment
            List<Integer> categoryID = new ArrayList<>();
            if(totalCategoriesAmount>0){
            printRelevantQuestion("index of invest Category", false);
            for (int j = 0; j <totalCategoriesAmount;j++){
               categoryID.add(checkValidAmount((b.getAllInvestCategories().size()-1),false));
            }}
            else {
            for(int j=0; j<b.getAllInvestCategories().size();j++)
                categoryID.add(j);
            }
        //--------------------------------------------------------------

       // get interest
        printRelevantQuestion("min interest", false);
        int minInterest = getValidNumber();
       //--------------------------------------------------------------

        // get min Yaz
        printRelevantQuestion("min Yaz for investment", false);
        int minYaz = getValidNumber();
        List<Integer> loansWithFilter;
       try {
             loansWithFilter = b.searchLoanWithFilters(invester.getName(), minInterest,
                   categoryID, minYaz, totalAmount);
       }
       catch(Exceptions err) {
           System.out.println(err + "Try again!");
           option6(b);
           return;
       }

           for (int i = 0; i < loansWithFilter.size(); i++) {
               LoanDummy loan = b.getAllLoans().get(loansWithFilter.get(i));
               System.out.println("#" + i + " " + loan);
           }

           if(loansWithFilter.size()==0){
               System.out.println("Couldn't find any investments with those filters try again!");
               option6(b);
               return;
           }
           List<Integer> temp = new ArrayList<>();
           getIndexesFromUserInput(temp,loansWithFilter.size());
           if(temp.size()==0) //dont want to invest
               return;

           List<Integer> res = new ArrayList<>();
           for(int j:temp){
               res.add(loansWithFilter.get(j));
           }
           b.invest(invester.getName(), totalAmount, res);
       }



    public void option7(BankAble bank){
        System.out.println("Day #"+bank.getTime()+" has ended good night!\n");
        bank.newDay();
        System.out.println("Day #"+bank.getTime()+" has started good morning!\n");
    }




    public void getIndexesFromUserInput(List<Integer> indexes,int size) {
        System.out.println("Please insert amount of loans you want to invest in");
        int loansAmount = checkValidAmount(size,true);
        if(loansAmount==0)
            return ;
        System.out.println("From the list above please choose the indexes of the loans that you would like to invest in");

        for(int i=0;i<loansAmount;i++) {
            indexes.add(inRangeAndNotDup(size,indexes));
        }
    }
    public int checkValidAmount(int size,boolean allowZero){
        int num =getValidNumber();
        if((num>size || num<1) && !allowZero){
            System.out.println("Please insert a number between 1-" + (size));
            return checkValidAmount(size,allowZero);
        }
        if((num>size || num<0) && allowZero){
            System.out.println("Please insert a number between 0-" + (size));
            return checkValidAmount(size,allowZero);
        }
        return num;
    }

    public int getValidNumber() {
        int answer = -1;

       // System.out.println("Please enter a valid positive integer: (for example: 550 )");
        while (answer < 0) {
            Scanner sc = new Scanner(System.in);
            String input = sc.nextLine();
            if (isNumber(input)) {
                Integer i = new Integer(input);
                answer = i;
            } else {
                System.out.println("Wrong input,please enter a valid positive number: (for example '3')\n");
            }
        }
        return answer;
    }
    public boolean isNumber(String s) {
        if(s.length()==0)
            return false;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) < '0' || s.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }
    public void printRelevantQuestion(String s, boolean requiredField) {
        if (!requiredField) {
            System.out.print("Please choose the " + s + " for your investment:\n" +
                    "If you dont want to choose just insert '0' ");
        } else {
            System.out.print("Please choose the " + s + " for your investment:\n" +
                    "You have to choose this option ('0' is not a valid option) ");
        }
    }
    private void pr1intAllInvestCategories(BankAble b) {
        List<String> categories= b.getAllInvestCategories();
        for(int i=0;i<categories.size();i++){
            System.out.println("#"+(i+1)+" "+categories.get(i));
        }
    }
    public int inRangeAndNotDup(int size,List<Integer>indexes){
        int num =getValidNumber();
        if(num>=size)
        {
            System.out.println("Please insert a number between 0-" + (size-1));
            return  inRangeAndNotDup(size,indexes);
        }
        if(indexes.indexOf(num)!=-1)
        {
            System.out.println("You already picked this investment try again!");
            return inRangeAndNotDup(size,indexes);
        }
        return num;
    }
    public ClientDummy getClientNameFromUser(BankAble b){
        int i = 1;
        for (ClientDummy c : b.getAllUsers()) {
            System.out.println("#" + i + " " + c.toStringWithBalance());
            i++;
        }
        printRelevantQuestion("index of user you would like to invest",true);
        int investerIndex = checkValidAmount(b.getAllUsers().size(),false)-1;
        return b.getAllUsers().get(investerIndex);
    }
}


