import ApplicationUI.Controllers.BankRefresher;
import ApplicationUI.Helpers.MyPopout;
import ApplicationUI.Helpers.NumberTextField;
import ApplicationUI.Helpers.TablesHelper;
import Chat.ChatMessage;
import Chat.ChatRefresher;
import Servlets.Constants;
import Servlets.HttpClientUtil;
import collections.Banking.BankAble;
import collections.Banking.DTOS.BankMessagesDummy;
import collections.Banking.DTOS.ClientDummy;
import collections.Banking.DTOS.LoanDummy;
import collections.Banking.Exceptions;
import collections.Banking.LoanForSale;
import collections.Banking.LoanStatus;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.controlsfx.control.CheckComboBox;
import org.jetbrains.annotations.NotNull;


import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.lang.Integer.parseInt;


public class ClientController {

    private static boolean primaryCSS = true;
    private static BorderPane mainPane = new BorderPane();
    private Stage currStage;
    private static ClientDummy currentClient = null;
    private static BankRefresher bank = null;
    private static Scene mainScene;
    private static ChatRefresher chatRefresher = null;


    private static List<LoanDummy> selectedLoansToInvest = new ArrayList<>();
    private static List<LoanDummy> clientWishToPayLoans = new ArrayList();
    private static Boolean startedTableRefresher = false;
    private static Boolean startedRewindRefresher = false;
    private static int amountSelected;
    private boolean initedSearchGrid = false;
    private NumberTextField searchAmountField = new NumberTextField();
    private NumberTextField searchInterestField = new NumberTextField();
    private NumberTextField searchMinYAZField = new NumberTextField();
    private NumberTextField searchMinOwnerField = new NumberTextField();
    private NumberTextField searchMaxOpenLoansField = new NumberTextField();
    @FXML private TextField newLoanName = new TextField();
    @FXML private TextField newLoanAmount = new TextField();
    @FXML private TextField newLoanFreq = new TextField();
    @FXML private TextField newLoanDuration = new TextField();
    @FXML private TextField newLoanInterest = new TextField();
    @FXML private TextField newLoanCategory = new TextField();@FXML
    private Label newLoanWarningLabel = new Label();
    @FXML private Label yazLabel = new Label();
    @FXML private Label userLabel = new Label();
    @FXML private Label balanceLabel = new Label();
    @FXML private Label didntPickLoansWarningLabel = new Label();
    @FXML private Label didntPickFilterWarningLabel = new Label();
    @FXML private Label didntPickLoansToPayWarningLabel = new Label();
    @FXML private TableView borrowerTable = new TableView();
    @FXML private TableView lenderTable = new TableView();
    @FXML private TableView transactionTable = new TableView();
    @FXML private TableView<LoanDummy> investTable = new TableView();
    @FXML private TableView activeTable = new TableView();
    @FXML private TableView messageTable = new TableView();
    @FXML private Tab loansForSaleTab = new Tab();
    @FXML private CheckComboBox categoryBox = new CheckComboBox();
    @FXML private AnchorPane investAnchor = new AnchorPane();
    @FXML private GridPane searchGridPane = new GridPane();
    @FXML private CheckBox style1CheckBox = new CheckBox();
    @FXML private CheckBox style2CheckBox = new CheckBox();
    @FXML private TextArea chatArea = new TextArea();
    @FXML private TextArea messageField = new TextArea();
    @FXML private Tab searchTab = new Tab();
    @FXML private Tab paymentsTab = new Tab();
    @FXML private Tab chatTab = new Tab();
    @FXML private Button depositButton = new Button();
    @FXML private Button whitdrawButton = new Button();
    @FXML private Label rewindLabel = new Label();
    @FXML private Button addLoanXMLButton = new Button();
    @FXML private Button createLoanButton = new Button();


    public void initialize(BankAble bank, String clientName) {
        this.bank = new BankRefresher(bank);
        chatRefresher = new ChatRefresher();
        startListRefresher();
        startChatRefresher();
        currentClient = this.bank.getDummyClientByName(clientName);
        try {
            mainPane = FXMLLoader.load(getClass().getResource("/clientController.fxml"));
        } catch (IOException err) {
        }
    }


    public void start(Stage primaryStage) {

        mainScene = new Scene(mainPane);
        currStage = primaryStage;
        currStage.setScene(mainScene);
        currStage.setTitle("Client : " + currentClient.getName() + " view");
        currStage.show();
    }
    /**-----------------------------------------------------------------**/
    /**
     * ---------------Main buttons view options------------------
     **/
    @FXML
    public void loansTabSelected() {
        fixTables();
        startTableRefresher();
        rewindRefresher();
    }

    @FXML
    public void loansForSaleTabSelected() {
        TableView table = new TableView();
        TablesHelper.addSellLoanTableColumns(table);
        table.getColumns().add(buyLoanButtonColumn());
        List<LoanForSale> loans = new ArrayList<>();
        for (LoanForSale loan : bank.getLoanForSales())
            if (!loan.getSellerName().equals(currentClient.getName()))
                loans.add(loan);
        table.getItems().addAll(loans);
        TablesHelper.fitMyTableSize(table);

        loansForSaleTab.setContent(table);
    }

    @FXML
    public void movementTabSelected() {
        fixTables();
    }

    @FXML
    public void investTabSelected() {
        investTable.getColumns().clear();
        selectedLoansToInvest.clear();
        investTable.prefWidthProperty().bind(investAnchor.widthProperty());
        if (categoryBox.getItems().size() < bank.getAllInvestCategories().size()) {
            categoryBox.getItems().clear();
            categoryBox.getItems().addAll(bank.getAllInvestCategories());
        }
        didntPickLoansWarningLabel.setVisible(false);
        didntPickFilterWarningLabel.setVisible(false);
        TablesHelper.addLoansTableColumns(investTable);
        investTable.getColumns().add(TablesHelper.addInvestBoxColumn(selectedLoansToInvest, didntPickLoansWarningLabel));
        TablesHelper.fitMyTableSize(investTable);
        initSearchGrid();
    }

    @FXML
    private void payTabSelected() {
        TablesHelper.fixPayTabTables(activeTable, messageTable, bank, clientWishToPayLoans);
        activeTable.getColumns().add(addPayFullAmountButtonColumn());
        for (LoanDummy loan : bank.getAllClientBorrow(currentClient))
            if (loan.getStatus().equals(LoanStatus.ACTIVE.toString()) || loan.getStatus().equals(LoanStatus.RISK.toString()))
                activeTable.getItems().add(loan);

        for (BankMessagesDummy msg : currentClient.getMessagesDummyList())
            messageTable.getItems().add(msg);
        didntPickLoansToPayWarningLabel.setVisible(false);
    }


    /**-----------------------------------------------------------------**/
    /**
     * ---------------Main buttons view options------------------
     **/
    @FXML
    public void searchFilters() {

        int interest = 0, minYAZ = 0, amount = 0, minOwner = 0, maxOpenLoans = 0;
        if (!searchInterestField.getText().equals(""))
            interest = parseInt(searchInterestField.getText());
        if (!searchMinYAZField.getText().equals(""))
            minYAZ = parseInt(searchMinYAZField.getText());
        if (!searchMinOwnerField.getText().equals(""))
            minOwner = parseInt(searchMinOwnerField.getText());
        if (!searchMaxOpenLoansField.getText().equals(""))
            maxOpenLoans = parseInt(searchMaxOpenLoansField.getText());
        if (!searchAmountField.getText().equals(""))
            amount = parseInt(searchAmountField.getText());

        if (amount == 0) {
            didntPickFilterWarningLabel.setVisible(true);
            return;
        }
        didntPickFilterWarningLabel.setVisible(false);
        amountSelected = amount;
        List<Integer> loansWithFilters = bank.searchLoanWithFilters(currentClient.getName(),
                interest, categoryBox.getCheckModel().getCheckedItems(), minYAZ, amount, maxOpenLoans, minOwner);

        List<LoanDummy> allLoans = bank.getAllLoans();

        investTable.getItems().clear();
        for (int i = 0; i < loansWithFilters.size(); i++)
            investTable.getItems().add(allLoans.get(loansWithFilters.get(i)));


        TablesHelper.fitMyTableSize(investTable);
    }

    @FXML
    public void invest() {
        if (selectedLoansToInvest.size() == 0) {
            didntPickLoansWarningLabel.setVisible(true);
            return;
        }
        Gson gson = new Gson();
        String json = gson.toJson(selectedLoansToInvest);
        String finalUrl = HttpUrl
                .parse(Constants.INVEST_PAGE)
                .newBuilder()
                .addQueryParameter("amount", String.valueOf(amountSelected))
                .addQueryParameter("loans", json)
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
        // int amountInvested = bank.investFX(currentClient.getName(),amountSelected, selectedLoansToInvest);
        currentClient = bank.getDummyClientByName(currentClient.getName());

        removeDups();

        StringBuilder loansNames = new StringBuilder();

        for (LoanDummy loan : selectedLoansToInvest)
            loansNames.append("Invested in : ").append(loan.getName()).append("\n");

        investTable.getItems().clear();
        selectedLoansToInvest.clear();
        // MyPopout.infoPop("Invest success!",loansNames+"Total sum of : "+ amountInvested);
        loansTabSelected();
    }

    @FXML
    private void swapCSS() {
        mainPane.getStylesheets().clear();
        if (primaryCSS) {
            mainPane.getStylesheets().add(
                    getClass().getResource("/CSS/Secondery.css").toExternalForm());
            style1CheckBox.setDisable(false);
            style1CheckBox.setSelected(false);
            style2CheckBox.setDisable(true);
            style2CheckBox.setSelected(true);
        } else {
            mainPane.getStylesheets().add(
                    getClass().getResource("/CSS/mainCSS.css").toExternalForm());
            style1CheckBox.setDisable(true);
            style1CheckBox.setSelected(true);
            style2CheckBox.setDisable(false);
            style2CheckBox.setSelected(false);
        }

        primaryCSS = !primaryCSS;
    }

    @FXML
    private void payAllChosenLoans() {
        if (clientWishToPayLoans.size() == 0) {
            didntPickLoansToPayWarningLabel.setVisible(true);
            return;
        }

        int sumPayed = 0;
        for (LoanDummy loan : clientWishToPayLoans) {
            sumPayed += bank.payment(loan);
            Gson gson = new Gson();
            String json = gson.toJson(loan);
            String finalUrl = HttpUrl
                    .parse(Constants.PAY_LOAN_PAGE)
                    .newBuilder()
                    .addQueryParameter("loan", json)
                    .build()
                    .toString();
            HttpClientUtil.runAsync(finalUrl, new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }
            });

        }

        clientWishToPayLoans.clear();
        currentClient = bank.getDummyClientByName(currentClient.getName());
        payTabSelected();
        MyPopout.infoPop("Payment Done!", "payed total of: " + sumPayed);
    }
    /**-----------------------------------------------------------------**/
    /** Balance action helpers **/
    /**
     * -----------------------------------------------------------------
     **/
    @FXML
    public void deposit() {
        balanceActionDialog(true);
    }

    @FXML
    public void withdraw() {
        balanceActionDialog(false);
    }

    public void balanceActionDialog(boolean deposit) {
        Dialog<Integer> dialog = new Dialog<>();

        dialog.setTitle("Withdraw action");
        if (deposit)
            dialog.setTitle("Deposit action");

        dialog.setHeaderText("Make action for " + currentClient.getName());
        NumberTextField text = new NumberTextField();

        GridPane grid = new GridPane();
        grid.add(new Label("Amount: "), 1, 1);
        grid.add(text, 2, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        text.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!deposit)
                if (parseInt(newValue) > currentClient.getBalance())
                    text.setText(Integer.toString((int) currentClient.getBalance()));
        });
        dialog.setResultConverter(button -> {
            int res = 0;
            if (!text.getText().equals("")) {
                res = parseInt(text.getText());
                if (!deposit)
                    res = -res;
            }
            return res;
        });
        Optional<Integer> action = dialog.showAndWait();

        if (action.get().equals(0))
            return;
        try {
            String finalUrl = HttpUrl
                    .parse(Constants.BALANCE_PAGE)
                    .newBuilder()
                    .addQueryParameter("balance", String.valueOf(action.get()))
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
        } catch (Exceptions err) {
            MyPopout.errorPop(err.msg);
        }
        if (deposit)
            MyPopout.infoPop("Deposit done!",
                    "Added " + action.get() + " to " + currentClient.getName() + " successfully!");
        else
            MyPopout.infoPop("Withdraw done!",
                    "Removed " + -action.get() + " from " + currentClient.getName() + " successfully!");


        movementTabSelected();
    }
    /** End of balance action helpers**/
    /**-----------------------------------------------------------------**/
    /**-----------------------End of view options-----------------------**/
    /**
     * -----------------------------------------------------------------
     **/
    private void setLabels() {
        currentClient = bank.getDummyClientByName(currentClient.getName());
        userLabel.setText("Current user: " + currentClient.getName());

        balanceLabel.setDisable(false);
        balanceLabel.setText("Balance : " + currentClient.getBalance());

        yazLabel.setDisable(false);
        yazLabel.setText("Current YAZ: " + bank.getTime());
    }

    private void initSearchGrid() {
        if (initedSearchGrid)
            return;
        searchGridPane.add(searchAmountField, 0, 2);
        searchAmountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(""))
                didntPickFilterWarningLabel.setVisible(false);

            if (parseInt(newValue) > currentClient.getBalance())
                searchAmountField.setText(Integer.toString((int) currentClient.getBalance()));
        });

        searchGridPane.add(searchInterestField, 1, 2);
        searchGridPane.add(searchMinYAZField, 2, 2);
        searchGridPane.add(searchMinOwnerField, 3, 2);
        searchGridPane.add(searchMaxOpenLoansField, 4, 2);
        initedSearchGrid = true;

        List<NumberTextField> allNumberFields = new ArrayList<>();
        Collections.addAll(allNumberFields, searchAmountField, searchInterestField, searchMinYAZField, searchMinOwnerField, searchMaxOpenLoansField);
        setMaxWidthToList(allNumberFields);
    }

    private TableColumn<LoanDummy, LoanDummy> addPayFullAmountButtonColumn() {
        TableColumn<LoanDummy, LoanDummy> colBtn = new TableColumn<>("PayAll");
        colBtn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));

        colBtn.setCellFactory(param -> new TableCell<LoanDummy, LoanDummy>() {
            private final Button payAllLoanButton = new Button("PayAll");

            @Override
            protected void updateItem(LoanDummy loan, boolean empty) {
                super.updateItem(loan, empty);
                if (loan == null) {
                    setGraphic(null);
                    return;
                }
                payAllLoanButton.setId("rich-blue");
                setGraphic(payAllLoanButton);

                payAllLoanButton.setText("PayAll(" + loan.amountLeftToPay() + ")");
                payAllLoanButton.setTooltip(new Tooltip("Close this loan"));
                payAllLoanButton.setOnAction(event -> {
                    Gson gson = new Gson();
                    String json = gson.toJson(loan);
                    String finalUrl = HttpUrl
                            .parse(Constants.PAY_FULL_LOAN_PAGE)
                            .newBuilder()
                            .addQueryParameter("loan", json)
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
                    bank.payFullLoanAmount(loan);
                    currentClient = bank.getDummyClientByName(currentClient.getName());
                    MyPopout.infoPop("Closed loan", "loan :" + loan.getName() + "is closed");
                    payTabSelected();
                });
            }
        });
        return colBtn;
    }

    private void fixTables() {
        if (borrowerTable.getColumns().size() == 0 || lenderTable.getColumns().size() == 0
                || transactionTable.getColumns().size() == 0) {
            clearTablesColumns();
            TablesHelper.addLoansTableColumns(borrowerTable);
            TablesHelper.fitMyTableSize(borrowerTable);

            TablesHelper.addLoansTableColumns(lenderTable);
            TablesHelper.fitMyTableSize(lenderTable);
            lenderTable.getColumns().add(sellLoanButtonColumn());

            TablesHelper.addMovementTableColumns(transactionTable);
            TablesHelper.fitMyTableSize(transactionTable);
        }
    }

    private void clearTablesColumns() {
        borrowerTable.getColumns().clear();
        lenderTable.getColumns().clear();
        transactionTable.getColumns().clear();
    }

    private void removeDups() {
        Set<LoanDummy> listWithoutDuplicates = new LinkedHashSet<>(selectedLoansToInvest);
        selectedLoansToInvest.clear();
        selectedLoansToInvest.addAll(listWithoutDuplicates);
    }

    private void setMaxWidthToList(List<NumberTextField> fields) {
        for (NumberTextField field : fields)
            field.setMaxWidth(153);
    }

    @FXML
    private void createNewLoan() {
        String loanName = newLoanName.getText();
        String loanAmount = (newLoanAmount.getText());
        String loanDuration = (newLoanDuration.getText());
        String loanFreq = (newLoanFreq.getText());
        String loanInterest = (newLoanInterest.getText());
        String loanCategory = newLoanCategory.getText();
        if (loanName.length() == 0 || loanAmount.length() == 0 || loanDuration.length() == 0 ||
                loanFreq.length() == 0 || loanInterest.length() == 0 || loanCategory.length() == 0) {
            newLoanWarningLabel.setVisible(true);
            newLoanWarningLabel.setText("All field are required!");
            return;
        }
        int duration = 1;
        int freq = 1;
        try {
            duration = parseInt(loanDuration);
            freq = parseInt(loanFreq);
            parseInt(loanAmount);
            parseInt(loanInterest);

        } catch (NumberFormatException err) {
            newLoanWarningLabel.setText("Use only numbers in relevant fields!");
            newLoanWarningLabel.setVisible(true);
            return;
        }

        if (duration % freq != 0) {
            newLoanWarningLabel.setVisible(true);
            newLoanWarningLabel.setText("error in frequency for this duration!");
            return;
        }
        newLoanWarningLabel.setVisible(false);
        String finalUrl = HttpUrl
                .parse(Constants.NEW_LOAN)
                .newBuilder()
                .addQueryParameter("loanName", loanName)
                .addQueryParameter("loanAmount", loanAmount)
                .addQueryParameter("loadDuration", loanDuration)
                .addQueryParameter("loanFreq", loanFreq)
                .addQueryParameter("loanInterest", loanInterest)
                .addQueryParameter("loanCategory", loanCategory)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                Platform.runLater(() -> {
                    if (response.code() == 400) {
                        try {
                            MyPopout.errorPop(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        newLoanFreq.clear();
                        newLoanDuration.clear();
                        newLoanInterest.clear();
                        newLoanAmount.clear();
                        newLoanName.clear();
                        newLoanCategory.clear();
                        loansTabSelected();
                    }
                });
            }
        });
    }

    public TableColumn<LoanDummy, LoanDummy> sellLoanButtonColumn() {
        TableColumn<LoanDummy, LoanDummy> colBtn = new TableColumn<>("Sell");
        colBtn.setCellValueFactory(
                param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        colBtn.setCellFactory(param -> new TableCell<LoanDummy, LoanDummy>() {
            private final Button movementButton = new Button("Sell");

            @Override
            protected void updateItem(LoanDummy loan, boolean empty) {
                super.updateItem(loan, empty);
                if (loan == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(movementButton);
                movementButton.setId("rich-blue");
                if (!loan.getStatus().equals(LoanStatus.ACTIVE.toString()))
                    movementButton.setDisable(true);
                else
                    movementButton.setDisable(false);

                movementButton.setOnAction(event -> {
                    Gson gson = new Gson();
                    String json = gson.toJson(loan);
                    String finalUrl = HttpUrl
                            .parse(Constants.SELL_LOAN_PAGE)
                            .newBuilder()
                            .addQueryParameter("loan", json)
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
                });
            }
        });
        return colBtn;
    }

    public TableColumn<LoanForSale, LoanForSale> buyLoanButtonColumn() {
        TableColumn<LoanForSale, LoanForSale> colBtn = new TableColumn<>("Buy");
        colBtn.setCellValueFactory(
                param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        colBtn.setCellFactory(param -> new TableCell<LoanForSale, LoanForSale>() {
            private final Button movementButton = new Button("Buy");

            @Override
            protected void updateItem(LoanForSale loan, boolean empty) {
                super.updateItem(loan, empty);
                if (loan == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(movementButton);
                if (loan.getAmountOfSale() > currentClient.getBalance())
                    movementButton.setDisable(true);
                else
                    movementButton.setDisable(false);
                movementButton.setId("rich-blue");


                movementButton.setOnAction(event -> {
                    Gson gson = new Gson();
                    String json = gson.toJson(loan);
                    String finalUrl = HttpUrl
                            .parse(Constants.BUY_LOAN_PAGE)
                            .newBuilder()
                            .addQueryParameter("loan", json)
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

                    currentClient = bank.getDummyClientByName(currentClient.getName());
                    loansForSaleTabSelected();
                });
            }
        });
        return colBtn;
    }

    public void startListRefresher() {
        Timer timer = new Timer();
        timer.schedule(bank, 2000, 2000);
    }

    public void startTableRefresher() {
        if (startedTableRefresher)
            return;
        messageField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
             sendMessage();
        });
        startedTableRefresher = true;
        Timer timer2 = new Timer();
        TimerTask TableRefresher = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    borrowerTable.getItems().clear();
                    borrowerTable.getItems().addAll(bank.getAllClientBorrow(currentClient));
                    lenderTable.getItems().clear();
                    lenderTable.getItems().addAll(bank.getAllClientInvest(currentClient));
                    transactionTable.getItems().clear();
                    transactionTable.getItems().addAll(currentClient.getAccount().getMovements());
                    setLabels();
                    fillChatArea();
                });
            }
        };
        timer2.schedule(TableRefresher, 2000, 2000);
    }

    public void startChatRefresher() {
        Timer timer = new Timer();
        timer.schedule(chatRefresher, 2000, 2000);
    }
    private void fillChatArea(){
        String chat = "";
        for(ChatMessage message : chatRefresher.getChatManager().getMessages()){
            chat += message.toString()+'\n';
        }
        chatArea.setText(chat);
    }
    @FXML
    private void sendMessage(){
        if(messageField.getText().length()<1)
            return;
        String finalUrl = HttpUrl
                .parse(Constants.SEND_MESSAGE)
                .newBuilder()
                .addQueryParameter("message", messageField.getText())
                .addQueryParameter("date", Integer.toString(bank.getTime()))
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
        messageField.clear();
    }

    @FXML
    private void newXML(){
            FileChooser fileChooser = new FileChooser();
            Stage fileStage = new Stage();
            File file = fileChooser.showOpenDialog(fileStage);

            buildFromXml(file.getPath());
    }
    private void buildFromXml(String path){
        String finalUrl = HttpUrl
                .parse(Constants.NEW_LOAN_XML)
                .newBuilder()
                .addQueryParameter("path", path)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if(response.code() == 400){
                    Platform.runLater(() -> {
                        try {
                            MyPopout.errorPop(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }
    public void rewindRefresher(){
        if(startedRewindRefresher)
            return;
        startedTableRefresher=true;
        Timer timer2 = new Timer();
        TimerTask TableRefresher = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if(bank.getRewindMode() && !searchTab.isDisabled()) {
                        searchTab.setDisable(true);
                        paymentsTab.setDisable(true);
                        chatTab.setDisable(true);
                        loansForSaleTab.setDisable(true);
                        depositButton.setDisable(true);
                        whitdrawButton.setDisable(true);
                        rewindLabel.setVisible(true);
                        createLoanButton.setDisable(true);
                        addLoanXMLButton.setDisable(true);
                    }
                    else if (searchTab.isDisabled() && !bank.getRewindMode())
                    {
                        searchTab.setDisable(false);
                        paymentsTab.setDisable(false);
                        chatTab.setDisable(false);
                        loansForSaleTab.setDisable(false);
                        depositButton.setDisable(false);
                        whitdrawButton.setDisable(false);
                        rewindLabel.setVisible(false);
                        createLoanButton.setDisable(false);
                        addLoanXMLButton.setDisable(false);
                    }
                });
            }
        };
        timer2.schedule(TableRefresher, 2000,2000);
    }
}

