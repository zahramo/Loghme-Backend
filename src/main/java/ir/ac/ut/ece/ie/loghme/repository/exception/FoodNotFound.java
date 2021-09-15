package ir.ac.ut.ece.ie.loghme.repository.exception;

public class FoodNotFound extends Exception {
    private String message;
    public FoodNotFound(){
        message = "This Food Does Not Exist In This Restaurant";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
