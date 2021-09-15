package ir.ac.ut.ece.ie.loghme.repository.exception;

public class EmptyCart extends Exception {
    private String message;

    public EmptyCart(){
        message = "Your Cart Is Empty";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
