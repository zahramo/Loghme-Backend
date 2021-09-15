package ir.ac.ut.ece.ie.loghme.repository.exception;

public class FoodAlreadyExist extends Exception {
    private String message;
    public FoodAlreadyExist(){
        message = "This Food Is Already Exist In This Restaurants";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
