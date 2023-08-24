package Chat;

import Servlets.HttpClientUtil;
import Servlets.Constants;
import com.google.gson.Gson;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;


import java.io.IOException;
import java.util.TimerTask;

public class ChatRefresher extends TimerTask {
    ChatManager chatManager;
    public ChatManager getChatManager(){return chatManager;}
    public ChatRefresher(){
        chatManager = new ChatManager();
    }
    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(Constants.GET_CHAT)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String rawBody = response.body().string();
                    Gson gson = new Gson();
                    ChatManager b = gson.fromJson(rawBody, ChatManager.class);
                    synchronized (this) {
                        chatManager = b;
                    }
                }
            }
        });
    }
}
