package collections.Banking.DTOS;

import collections.Banking.AccountMovement;

public class AccountMovementDummy {
    private int YAZ;
    private double sumBefore;
    private double actionSum;
    private double sumAfter;
    AccountMovementDummy(AccountMovement move){
        YAZ=move.getYAZ();
        sumBefore=move.getSumBefore();
        actionSum=move.getActionSum();
        sumAfter=move.getSumAfter();
    }
    public int getYAZ() {return YAZ;}

    public double getSumBefore() {return sumBefore;}

    public double getActionSum(){return actionSum;}

    public double getSumAfter(){return sumAfter;}
    public String toString()
    {return "Movement: In YAZ: "+YAZ+" sum before movement: "+sumBefore +
            " movement sum: "+actionSum+" sum after movement: "+sumAfter;
    }
}

