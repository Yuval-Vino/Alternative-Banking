public class AccountMovement {
    int YAZ;
    double sumBefore;
    double actionSum;
    double sumAfter;
    AccountMovement(int YAZ,double sumBefore,double movementSum){
        this.YAZ=YAZ;
        this.sumBefore=sumBefore;
        this.actionSum=movementSum;
        this.sumAfter=movementSum+sumBefore;
    }
    @Override
    public String toString()
    {return "Movement: In YAZ: "+YAZ+" sum before movement: "+sumBefore + " movement sum: "+actionSum+" sum after movement: "+sumAfter;}
}
