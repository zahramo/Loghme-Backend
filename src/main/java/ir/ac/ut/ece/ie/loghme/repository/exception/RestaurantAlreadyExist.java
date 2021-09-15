package ir.ac.ut.ece.ie.loghme.repository.exception;

public class RestaurantAlreadyExist extends Exception{
    private String message;
    public RestaurantAlreadyExist(){
        message = "This Restaurant Already Exist";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
