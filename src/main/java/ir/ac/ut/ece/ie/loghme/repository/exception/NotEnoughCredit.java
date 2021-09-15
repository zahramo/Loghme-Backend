package ir.ac.ut.ece.ie.loghme.repository.exception;

public class NotEnoughCredit extends Exception {
    private String message;
    public NotEnoughCredit(){
        message = "Your Credit Is Not Enough";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
