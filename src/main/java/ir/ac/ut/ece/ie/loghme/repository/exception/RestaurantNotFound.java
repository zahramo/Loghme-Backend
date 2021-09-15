package ir.ac.ut.ece.ie.loghme.repository.exception;

public class RestaurantNotFound extends Exception {
    private String message;
    public RestaurantNotFound(){
        message = "This Restaurant Not Found";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
