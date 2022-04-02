package schema.generated;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class SchemaBasedXML {
    public static void main(String[] args){
        try{
            InputStream inputStream = new FileInputStream(new File("src/resources/ex1-big (2).xml"));
            AbsDescriptor list = func(inputStream);
            List<AbsCustomer> customersList =  list.getAbsCustomers().getAbsCustomer();

            System.out.println(customersList.get(0).getName());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }
    private static AbsDescriptor func(InputStream in)throws JAXBException{
        JAXBContext jc = JAXBContext.newInstance("schema.generated");
        Unmarshaller u = jc.createUnmarshaller();
        return (AbsDescriptor) u.unmarshal(in);
    }
}
