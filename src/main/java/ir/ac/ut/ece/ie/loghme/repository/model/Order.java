package ir.ac.ut.ece.ie.loghme.repository.model;

import ir.ac.ut.ece.ie.loghme.repository.exception.FoodNotFound;
import ir.ac.ut.ece.ie.loghme.repository.exception.RestaurantNotFound;
import ir.ac.ut.ece.ie.loghme.repository.exception.RestaurantOutOfRegion;

import java.util.HashMap;
import java.util.Map;

public class Order {
    private String id;
    private String userId;
    private String restaurantName;
    private String restaurantId;
    private String courierId;
    private Map<Food,Integer> foods = new HashMap<>();
    private int deliveryTime;
    private State state;
    int totalCost;

    public Order(Cart cart){
        this.userId = cart.getUserId();
        this.restaurantName = cart.getRestaurantName();
        this.restaurantId = cart.getRestaurantId();
        for(Map.Entry<Food, Integer> entry : cart.getFoods().entrySet()){
            foods.put(entry.getKey(), entry.getValue());
        }
        this.totalCost = cart.calculatePrice();
    }

    public Order(){
        this.deliveryTime = -1;
    }


    public void setFoods(Map<Food, Integer> foods) {
        this.foods = foods;
    }

    public void setUserId(String userId) { this.userId = userId; }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCourierId(String courierId) {
        this.courierId = courierId;
    }

    public void setDeliveryTime(int deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public String getUserId() { return userId; }

    public String getCourierId() {
        return courierId;
    }

    public String getId() {
        return id;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public Map<Food, Integer> getFoods() {
        return foods;
    }

    public State getState() {
        return state;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
}
