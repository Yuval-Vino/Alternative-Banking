package Chat;

public class ChatMessage {
    String author;
    String message;
    int date;
    ChatMessage(String author, String message, int date){
        this.author=author;
        this.message=message;
        this.date=date;
    }
    @Override
    public String toString(){
        return date+" "+author+" : "+message;
    }
}
