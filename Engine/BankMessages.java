package collections.Banking;

public class BankMessages {
    private String loanName;
    private int YAZ;
    private String message;

    BankMessages(String loan,int YAZ,String message){
        this.loanName = loan;
        this.YAZ = YAZ;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getYAZ() {
        return YAZ;
    }

    public String getLoan() {
        return loanName;
    }
}
