package ir.ac.ut.ece.ie.loghme.service.info;

import ir.ac.ut.ece.ie.loghme.repository.exception.FoodNotFound;
import ir.ac.ut.ece.ie.loghme.repository.exception.RestaurantNotFound;
import ir.ac.ut.ece.ie.loghme.repository.exception.RestaurantOutOfRegion;
import ir.ac.ut.ece.ie.loghme.repository.model.*;

import java.util.ArrayList;
import java.util.Map;

public class CartInfo {
    private String restaurantName;
    private String restaurantId;
    private ArrayList<ReservedFoodInfo> foods = new ArrayList<>();
    private int totalCost;

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public ArrayList<ReservedFoodInfo> getFoods() {
        return foods;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public CartInfo(Cart cart)  {
        this.restaurantName = cart.getRestaurantName();
        this.totalCost = cart.calculatePrice();
        this.restaurantId = cart.getRestaurantId();
        for(Map.Entry<Food, Integer> entry : cart.getFoods().entrySet()){
            ReservedFoodInfo food;
//            if(entry.getKey() instanceof FoodUnderSale){
//               food = new ReservedFoodInfo(entry.getKey().getName(), entry.getValue(),
//                       entry.getKey().getPrice()*entry.getValue(),1);
//            }else{
//                food = new ReservedFoodInfo(entry.getKey().getName(), entry.getValue(),
//                        entry.getKey().getPrice()*entry.getValue(),0);
//            }
            food = new ReservedFoodInfo(entry.getKey().getName(), entry.getValue(),
                    entry.getKey().getPrice()*entry.getValue(),0);
            foods.add(food);
        }
    }
}