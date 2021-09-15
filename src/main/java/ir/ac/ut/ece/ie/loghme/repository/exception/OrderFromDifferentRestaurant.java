package ir.ac.ut.ece.ie.loghme.repository.exception;

public class OrderFromDifferentRestaurant extends Exception {
    private String message;
    public OrderFromDifferentRestaurant(){
        message = "Your Previous Order Not Completed Yet..";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
