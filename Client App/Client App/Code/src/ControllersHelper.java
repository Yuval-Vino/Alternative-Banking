import collections.Banking.BankAble;
import javafx.stage.Stage;

public class ControllersHelper {

    public static void switchToClientController(String clientName, BankAble bank,Stage stage) {
        ClientController clientController = new ClientController();
        stage.close();
        clientController.initialize(bank,clientName);

        try {
            clientController.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }}
}
