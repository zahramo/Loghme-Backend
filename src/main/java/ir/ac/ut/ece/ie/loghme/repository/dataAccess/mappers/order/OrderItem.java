package ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.order;

import ir.ac.ut.ece.ie.loghme.repository.model.Food;
import ir.ac.ut.ece.ie.loghme.repository.model.Order;

import java.util.Map;

public class OrderItem {
    String userId;
    String orderId;
    String restaurantId;
    String foodName;
    int price;
    int number;

    public OrderItem(Order order, Food food, int number){
        userId = order.getUserId();
        orderId = order.getId();
        restaurantId = order.getRestaurantId();
        this.foodName = food.getName();
        this.price = food.getPrice()*number;
        this.number = number;
    }

    public OrderItem(){
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

    public String getOrderId() {
        return orderId;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
