package menu;

import collections.Banking.Bank;
import collections.Banking.Client;
import collections.Banking.Loan;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Ui2Engine {
    public void callingToEngineFunc(char option, Bank b, MenuPrinter m) throws JAXBException, FileNotFoundException {
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
        }

    }

    public void option1(Bank b) throws JAXBException, FileNotFoundException {
        System.out.println("Please enter a valid XML path:\n");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        checkValidPathFile(input);
        b.buildBankFromXML(input);
        //need to delete current bank and create a new one
        b.newCounting();
    }

    public void checkValidPathFile(String input) {
        //need to check its xml file.
        // need to check that empty spaces doesn't fail us.
        // need to check validity of the file.
    }

    public void option2(Bank b) {
        //need to create a new list of all loans
        //add a new func in the bank that printing *ALL* the loans that there are
    }

    public void option3(Bank b) {
        for (Client c : b.users.values()) {
            System.out.println("Client name: " + c.getName() + "\n" +
                    "His account movements are:\n");
            c.getAccount();
            //need to build a new function in a bank that get client and shows all his loans as a loaner and a borrower
        }
    }

    public void option4(Bank b, boolean deposit) {
        Client c = getValidClient(b);
        double i = getValidNumber();
        //need to check if double can get int value
        if (deposit)
            c.balanceAction(i, b.getTime());
        else
            c.balanceAction(-i, b.getTime());
    }

    public Client getValidClient(Bank b) {
        System.out.println("Please enter a valid name of user in the system: (for example:'Moshe')" +
                "you done need the ' " +
                "Be noticed extra white blanks will be ERROR");
        Client c = null;
        while (c == null) {
            Scanner sc = new Scanner(System.in);
            String input = sc.nextLine();
            c = b.getClientByName(input);
            if (c == null) {
                System.out.println("Couldn't fine the client please enter a valid name.\n");
            }
        }
        return c;
    }

    public int getValidNumber() {
        int answer = -1;
        //Integer i = new Integer();
        System.out.println("Please enter a valid positive integer: (for example('550')\n" +
                "you done need the ' \n");
        while (answer < 0) {
            Scanner sc = new Scanner(System.in);
            String input = sc.nextLine();
            if (isNumber(input)) {
                Integer i = new Integer(input);
                answer = i;
            } else {
                System.out.println("Wrong input,please enter a valid positive name: (for example('550')\n");
            }
        }
        return answer;
    }

    public boolean isNumber(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) < '0' || s.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    public void option6(Bank b) {
        int i = 0;
        for (Client c : b.users.values()) {
            System.out.println("#" + i + " " + c.toStringWithBalance());
        }
        System.out.println("");
        Client c = getValidClient(b);
        printRelevantQuestion("sum to invest", true);
        int totalAmount = getValidNumber();
        printRelevantQuestion("min interest", false);
        double minInterest = getValidNumber();
        minInterest = minInterest;
        printRelevantQuestion("max Yaz", false);
        int maxYaz = getValidNumber();
        List<Loan> loansWithFilter = b.searchLoanWithFilters(c.getName(), minInterest, maxYaz, totalAmount);
        //need to finish this func and create a invest.
    }

    public void printRelevantQuestion(String s, boolean totalAmount) {
        if (totalAmount) {
            System.out.println("Please choose the " + s + " for your investment:\n" +
                    "If you dont wont to choose just put '0'");
        } else {
            System.out.println("Please choose the " + s + " for your investment:\n" +
                    "You have to choose this option ('0' is not a valid option");
        }
    }

    public List<Integer> getLoansIndexes() {
        List<Integer> indexes = new ArrayList<>();
        boolean b = false;
        while(!b){
            b = getIndexesFromUserInput(indexes);
            if(!b) {
                System.out.println("Invalid input,please put a new valid one\n");
                indexes.clear();
            }
        }
        return indexes;
    }

    public boolean getIndexesFromUserInput(List<Integer> indexes) {
        int currInd = 0;
        System.out.println("From the list above please choose the indexes of the loans that you would like to invest in\n" +
                "For example: '1 3 5 8' , the ' is not necessary and will cause a ERROR\n" +
                "Be noticed adding non existing indexes or letter or signs will cause a ERROR\n");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        for (int i = 0; i < input.length(); i++) {
            while (input.charAt(i) != ' ') {
                if (input.charAt(i) >= '0' && input.charAt(i) <= '9') {
                    currInd *= 10;
                    currInd += input.charAt(i);
                    i++;
                } else {
                    return false;
                }
            }
            indexes.add(currInd);
            //need to check about the order of the indexes.
            currInd = 0;
        }
        return true;
    }
}

