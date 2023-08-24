package collections.Banking;

public class Exceptions extends RuntimeException  {
    public String msg;
    public Exceptions(String errorMessage) {
        msg=errorMessage;
    }
    @Override
    public String toString(){
        return msg;
    }

}
