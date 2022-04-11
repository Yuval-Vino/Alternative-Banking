package XMLReader;

import collections.Banking.Exceptions;
import schema.generated.AbsCustomer;
import schema.generated.AbsDescriptor;
import schema.generated.AbsLoan;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SchemaBasedXML {

    InputStream inputStream;
    JAXBContext jc;
    Unmarshaller u;
    AbsDescriptor descriptor;

    public SchemaBasedXML(String Path) throws FileNotFoundException, JAXBException,Exceptions {
         inputStream = new FileInputStream(new File(Path.trim()));
         jc = JAXBContext.newInstance("schema.generated");
         u = jc.createUnmarshaller();
         descriptor=   (AbsDescriptor) u.unmarshal(inputStream);
       try{ checkValidXml();}
       catch(Exceptions err){
           throw err;
       }
    }

    private void checkValidXml () throws Exceptions{
       try {
           checkValidCategories();
           checkValidBorrowers();
           checkValidFreq();
       }
       catch (Exceptions err) {
           throw err;
       }

    }

    public List<AbsCustomer> getAllCustomers(){
            return  descriptor.getAbsCustomers().getAbsCustomer();
    }
    public List<AbsLoan> getAllLoans(){
        return descriptor.getAbsLoans().getAbsLoan();
    }
    public List<String> getAllCategories(){return descriptor.getAbsCategories().getAbsCategory();}

    private void checkValidCategories() throws Exceptions{
        for(AbsLoan loan : getAllLoans())
            if(getAllCategories().indexOf(loan.getAbsCategory())==-1)
                throw new Exceptions("in loan number "+getAllLoans().indexOf(loan)+" asked by: " +loan.getAbsOwner() +
                        " the category is not Valid!");
    }
    private void checkValidBorrowers() throws Exceptions{
        List <String> names = new ArrayList<>();
        for(AbsCustomer c:getAllCustomers()){
            if(names.indexOf(c.getName()) != -1){
                throw new Exceptions(c.getName() + "Appears twice");
            }
            names.add(c.getName());
        }
        for(AbsLoan loan: getAllLoans()){
            if(names.indexOf(loan.getAbsOwner()) == -1){
                throw new Exceptions(loan.getAbsOwner() + " Requesting a loan but he is not a customer in the system");
            }
        }
    }
    private void checkValidFreq() throws Exceptions{
        for(AbsLoan loan:getAllLoans()){
            if(loan.getAbsTotalYazTime() % loan.getAbsPaysEveryYaz() != 0 ){
                throw new Exceptions("in loan number "+getAllLoans().indexOf(loan)+" asked by: " +loan.getAbsOwner() +
                        " the frequency is not Valid!");
            }
        }
    }
}
