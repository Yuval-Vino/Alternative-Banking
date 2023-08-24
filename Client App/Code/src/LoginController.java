import Servlets.Constants;
import Servlets.HttpClientUtil;
import collections.Banking.Bank;
import collections.Banking.BankAble;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class LoginController extends Application {
    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    private static Stage currStage = new Stage();
    private static BankAble bank = new Bank();
    @FXML private TextField userNameField = new TextField();
    @FXML
    private GridPane mainPane = new GridPane();
    private  Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        mainPane = FXMLLoader.load(getClass().getResource("/loginController.fxml"));
        currStage = stage;
        Scene scene = new Scene(mainPane);
        mainStage = stage;
        currStage.setScene(scene);
        currStage.show();
    }
    @FXML
    public void buttonEvent(){
        String userName = userNameField.getText();
        if (userName.isEmpty()) {
           // errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return;
        }

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter(Constants.USERNAME, userName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                System.out.println(responseBody);

                if (response.code() != 200)
                    System.out.println(responseBody+" error");
                 else{
                    Platform.runLater(() -> {
                        Gson gson = new Gson();
                        Bank b = gson.fromJson(responseBody, Bank.class);
                        ControllersHelper.switchToClientController(userName, b,currStage);
                    });

                }}

        });
    }
    public static void main(String[] args) {
        launch(args);
    }
    }
