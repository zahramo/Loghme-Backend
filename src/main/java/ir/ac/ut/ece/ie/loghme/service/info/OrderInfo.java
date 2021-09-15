package ir.ac.ut.ece.ie.loghme.service.info;

import ir.ac.ut.ece.ie.loghme.repository.model.Food;
import ir.ac.ut.ece.ie.loghme.repository.model.Order;
import ir.ac.ut.ece.ie.loghme.repository.model.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderInfo {
    String restaurantName;
    String state;
    ArrayList<ReservedFoodInfo> foods = new ArrayList<>();
    int totalCost;


    public String getRestaurantName() {
        return restaurantName;
    }

    public String getState() {
        return state;
    }

    public ArrayList<ReservedFoodInfo> getFoods() { return foods; }

    public int getTotalCost() {
        return totalCost;
    }

    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public void setState(String state) { this.state = state; }

    public void setTotalCost(int totalCost) { this.totalCost = totalCost; }

    public void setFoods(ArrayList<ReservedFoodInfo> foods) { this.foods = foods; }

    public OrderInfo(Order order){
        this.state = order.getState().name();
        this.restaurantName = order.getRestaurantName();
        for(Map.Entry<Food, Integer> entry : order.getFoods().entrySet()){
            ReservedFoodInfo food = new ReservedFoodInfo(entry.getKey().getName(), entry.getValue(),
                    entry.getKey().getPrice(),0);
            foods.add(food);
        }
        this.totalCost = order.getTotalCost();
    }

    public OrderInfo(){ }

}
