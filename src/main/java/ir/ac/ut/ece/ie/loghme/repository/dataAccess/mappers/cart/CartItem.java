package ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.cart;

import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.food.FoodMapper;
import ir.ac.ut.ece.ie.loghme.repository.exception.OrderFromDifferentRestaurant;
import ir.ac.ut.ece.ie.loghme.repository.model.Food;
import ir.ac.ut.ece.ie.loghme.repository.model.Order;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class CartItem {
    String userId;
    String restaurantId;
    String foodName;
    int price;
    int number;

    public CartItem(String userId, Food food, int number){
        this.userId = userId;
        this.restaurantId = food.getRestaurantId();
        this.foodName = food.getName();
        this.price = food.getPrice()*number;
        this.number = number;
    }

    public CartItem(){
    }

    public String getUserId() {
        return userId;
    }

    public int getNumber() {
        return number;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
