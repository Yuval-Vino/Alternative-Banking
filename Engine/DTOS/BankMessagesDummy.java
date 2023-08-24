package collections.Banking.DTOS;

import collections.Banking.BankMessages;

public class BankMessagesDummy {
    private String loanName;
    private int YAZ;
    private String message;

    BankMessagesDummy(BankMessages msg)
    {
        YAZ = msg.getYAZ();
        message = msg.getMessage();
        loanName = msg.getLoan();
    }

    public int getYAZ() {
        return YAZ;
    }

    public String getMessage() {
        return message;
    }
    public String getLoanName(){
        return loanName;
    }
}
