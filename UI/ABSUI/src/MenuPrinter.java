
import collections.Banking.*;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MenuPrinter {
    private char answer;

    public static void main(String[] args) throws JAXBException, FileNotFoundException {
        BankAble bank=new Bank();
        UI.Ui2Engine ui2Engine=new UI.Ui2Engine();





        System.out.println("Welcome to Alternative Banking System!\n" +
                "Please enter the operation that you would like to choose.\n" +
                "Notice, your answer should be a number between 1-8, other choose will be invalid.\n\n");
        System.out.println("To start the program first of all");
        ui2Engine.option1(bank);
        while(true) {
            boolean valid = false;
            System.out.print(
                    "#1  Reading new XML file.\n" +
                    "#2 Show the info about all the loans that are in the system.\n" +
                    "#3 Show the info about all the customers(such as account movement) in the system.\n" +
                    "#4 Loading money to bank account.\n" +
                    "#5 Withdrawal money from bank account.\n" +
                    "#6 Make a investment.\n" +
                    "#7 Moving to the next day.\n" +
                    "#8 Exit\n"+
                    "insert your choice:");

            String input = null;
            while (!valid) {
                Scanner sc = new Scanner(System.in);
                input = sc.nextLine();
                valid = checkValidInput(input);
                if (!valid) {
                    System.out.print("Invalid choose, please enter a number between 1-8");
                }
            }
            //answer = input.charAt(0);
            ui2Engine.callingToEngineFunc(input.charAt(0), bank);
        }
    }
    public static boolean checkValidInput(String s){
        if (s.length()!=1) {
            return false;
        }
        else if(s.charAt(0) < '1' || s.charAt(0) > '8'){
            return false;
        }
        return true;
    }

}
