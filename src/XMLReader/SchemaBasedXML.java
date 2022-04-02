package XMLReader;

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
import java.util.List;

public class SchemaBasedXML {

    InputStream inputStream;
    JAXBContext jc;
    Unmarshaller u;
    AbsDescriptor descriptor;

    public SchemaBasedXML(String Path) throws FileNotFoundException, JAXBException {
         inputStream = new FileInputStream(new File(Path));
         jc = JAXBContext.newInstance("schema.generated");
         u = jc.createUnmarshaller();
         descriptor=   (AbsDescriptor) u.unmarshal(inputStream);
    }
    public List<AbsCustomer> getAllCustomers(){
            return  descriptor.getAbsCustomers().getAbsCustomer();
    }
    public List<AbsLoan> getAllLoans(){
        return descriptor.getAbsLoans().getAbsLoan();
    }

}
