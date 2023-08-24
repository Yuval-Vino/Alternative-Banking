import ApplicationUI.Controllers.BankRefresher;
import ApplicationUI.Helpers.MyPopout;
import ApplicationUI.Helpers.TablesHelper;
import Servlets.Constants;
import Servlets.HttpClientUtil;
import collections.Banking.Bank;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class AdminController extends Application {
    private static GridPane mainPane;

    private static Stage currStage;
    private static Scene currScene;
    private static BankRefresher bank ;
    private static Boolean startedTableRefresher = false;
    @FXML private Label currYAZLabel = new Label();
    @FXML private TableView loansTable = new TableView<>();
    @FXML private TableView clientsTable = new TableView<>();
    @FXML private ComboBox rewindComboBox = new ComboBox();
    @FXML private Label rewindLabel = new Label();
    @FXML private Button unRewindButton = new Button();

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.bank = new BankRefresher(new Bank());
        startListRefresher();
        mainPane = FXMLLoader.load(getClass().getResource("/admin2.fxml"));
        currScene= new Scene(mainPane);
        currStage = primaryStage;
        currStage.setScene(currScene);
        currStage.setTitle("Admin View");
        currStage.show();
    }
    /**-----------------------------------------------------------------**/
    /**------------------Main buttons view options----------------------**/
    @FXML
    public void fixTables(){
        startTableRefresher();
        if(loansTable.getColumns().size() == 0 || clientsTable.getColumns().size()==0) {
            clearTablesColumns();
            TablesHelper.addClientTableColumns(clientsTable, bank);
            TablesHelper.fitMyTableSize(clientsTable);
            TablesHelper.addLoansTableColumns(loansTable);
            TablesHelper.fitMyTableSize(loansTable);
        }
    }
    @FXML
    public void moveDay(){
        String finalUrl = HttpUrl
                .parse(Constants.MOVE_DAY)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {}
        });
        MyPopout.infoPop("Day moved","Starting day #"+ (bank.getTime()+1));
    }

    /**-----------------------------------------------------------------**/
    /**-----------------------End of view options-----------------------**/
    /**-----------------------------------------------------------------**/
    /**-----------------------------------------------------------------**/
    /**----------------------Bank action functions----------------------**/

    private void clearTablesColumns(){
        loansTable.getColumns().clear();
        clientsTable.getColumns().clear();
    }
    @FXML private void rewindOn(){
        if(rewindComboBox.getValue() == null)
            return;
        rewindLabel.setVisible(true);
        unRewindButton.setDisable(false);
        String finalUrl = HttpUrl
                .parse(Constants.REWIND)
                .newBuilder()
                .addQueryParameter("YAZ", String.valueOf(rewindComboBox.getValue()))
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
            }
        });
    }   @FXML private void rewindOff(){
        rewindLabel.setVisible(false);
        unRewindButton.setDisable(true);
        String finalUrl = HttpUrl
                .parse(Constants.UNREWIND)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
            }
        });
    }
    /**-----------------------------------------------------------------**/
    /**------------------End of Bank action functions-------------------**/
    /**-----------------------------------------------------------------**/
    /**-------------------------Utils functions-------------------------**/



    public void startListRefresher() {
        Timer timer = new Timer();
        timer.schedule(bank, 2000, 2000);

    }
    public void startTableRefresher() {
        if(startedTableRefresher)
            return;
        startedTableRefresher=true;
        rewindComboBox.getItems().add(0);
        Timer timer2 = new Timer();
        TimerTask TableRefresher = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    clientsTable.getItems().clear();
                    loansTable.getItems().clear();
                    loansTable.getItems().addAll(bank.getAllLoans());
                    clientsTable.getItems().addAll(bank.getAllUsers());
                    currYAZLabel.setText("Current YAZ : "+ bank.getTime());
                    if(rewindComboBox.getItems().size() != bank.getTime()){
                        rewindComboBox.getItems().clear();
                        List<Integer> range = IntStream.rangeClosed(0, bank.getTime()-1)
                            .boxed().collect(Collectors.toList());
                        rewindComboBox.getItems().addAll(range);
                    }
                });
            }
        };
        timer2.schedule(TableRefresher, 2000,2000);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
