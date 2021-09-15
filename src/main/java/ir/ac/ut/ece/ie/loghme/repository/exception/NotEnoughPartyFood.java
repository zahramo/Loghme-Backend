package ir.ac.ut.ece.ie.loghme.repository.exception;

public class NotEnoughPartyFood extends Exception {
    private String message;
    public NotEnoughPartyFood(){
        message = "Only Few Number Of This Food Exist In Food Party Menu";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
