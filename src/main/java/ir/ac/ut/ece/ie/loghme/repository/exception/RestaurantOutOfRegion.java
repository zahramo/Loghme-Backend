package ir.ac.ut.ece.ie.loghme.repository.exception;

public class RestaurantOutOfRegion extends Exception{
    private String message;
    public RestaurantOutOfRegion(){
        message = "This Restaurant Is Out Of Region";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
