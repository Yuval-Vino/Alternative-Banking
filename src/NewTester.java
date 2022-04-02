import collections.Banking.Bank;
import collections.Banking.Loan;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

public class NewTester {
    public static void main(String[] args) throws JAXBException, FileNotFoundException {
        Bank b = new Bank();
        b.buildBankFromXML("src/resources/ex1-error-2.2.xml");
        for (Loan curr:b.pending) {
            System.out.println(curr);
        }
    }
}
