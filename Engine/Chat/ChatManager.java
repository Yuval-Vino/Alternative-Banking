package Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatManager {
    List<ChatMessage> messages;
    int messageCounter ;
    public ChatManager(){
        messages = new ArrayList<>();
        messageCounter = 0;
    }
    public void addNewMessage(String author, String message, int date) {
        messages.add(new ChatMessage(author,message,date));
    }
    public List<ChatMessage> getMessages(){return messages;}
}
