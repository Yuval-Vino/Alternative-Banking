package menu;

import java.util.Scanner;

public class MenuPrinter {
    private char answer;

    public void print() {
        boolean valid = false;
        System.out.println("Welcome to Alternative Banking System!\n" +
                "Please enter the operation that you would like to choose.\n" +
                "Notice, your answer should be a number between 1-8, other choose will be invalid.\n\n" +
                "#1  Reading the XML file.\n" +
                "for this choose you will be needed to put the right path for the file and a *valid* XML file.\n" +
                "Otherwise we will inform you about the problem and ask you to fix it depends on the problem.\n\n" +
                "#2 Show the info about all the loans that are in the system.\n\n" +
                //need to check if XML file was uploaded
                "#3 Show the info about all the customers(such as account movement) in the system.\n\n" +
                //need to check if XML file was uploaded
                "#4 Loading money to bank account.\n\n" +
                "#5 Withdrawal money from bank account.\n\n" +
                "#6 Make a investment.\n\n" +
                "#7 Moving to the next day.\n\n" +
                "#8 Exit\n\n");

        String input = null;
        while (!valid) {
            Scanner sc = new Scanner(System.in);
            input = sc.nextLine();
            valid = checkValidInput(input);
            if (!valid) {
                System.out.println("Invalid choose.\n" +
                        "Please enter only 1 number between 1-8 and press enter.\n" +
                        "Other input will be invalid!");
            }
        }
        this.answer = input.charAt(0);


    }
    public static boolean checkValidInput(String s){
        if (s.length() > 1) {
            return false;
        }
        else if(s.charAt(0) < '1' || s.charAt(0) > '8'){
            return false;
        }
        return true;
    }

}
