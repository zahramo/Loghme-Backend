package ir.ac.ut.ece.ie.loghme.repository.model;

import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.cart.CartItem;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.cart.CartItemMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.cart.CartMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.food.FoodMapper;
import ir.ac.ut.ece.ie.loghme.repository.exception.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Cart {
    private String restaurantName;
    private String restaurantId;
    private Map<Food,Integer> foods;
    private String userId;

    public Cart(String userId){
        restaurantName = "";
        restaurantId = "";
        this.userId = userId;
        foods = new HashMap<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getRestaurantId(){
        return restaurantId;
    }

    public Map<Food, Integer> getFoods(){
        return foods;
    }

    public void addFood(Food food, int number) throws OrderFromDifferentRestaurant, RestaurantNotFound,
            RestaurantOutOfRegion, NotEnoughPartyFood, SQLException {
        FoodUnderSale foodUnderSale = LoghmeRepository.getCurInstance().getFoodUnderSale(food);
        if(foodUnderSale != null){
            food = foodUnderSale;
        }
        if(restaurantId.equals("")) {
            foods.put(food, number);
            restaurantId = food.getRestaurantId();
            restaurantName = LoghmeRepository.getCurInstance().getRestaurant(restaurantId).getName();
            ArrayList<String> result = new ArrayList<String>();
            result.add(userId);
            result.add(food.getRestaurantId());
            CartMapper.getInstance().update(result);
            System.out.println("*******************" + result);
            CartItem cartItem = new CartItem(userId, food, number);
            CartItemMapper.getInstance().insert(cartItem);
        }
        else if(food.getRestaurantId().equals(restaurantId)){
            Food existsFood = null;
            int existsCount = 0;
            for(Map.Entry<Food, Integer> cartfood : foods.entrySet()){
                if(cartfood.getKey().getName().equals(food.getName())){
                    if(food instanceof FoodUnderSale){
                        if(((FoodUnderSale) food).getCount() <= cartfood.getValue()){
                            throw new NotEnoughPartyFood();
                        }
                    }
                    existsFood = cartfood.getKey();
                    existsCount = cartfood.getValue();
                    break;
                }
            }
            if(existsFood != null){
                foods.put(existsFood, existsCount+number);
                CartItem cartItem = new CartItem(userId, existsFood, existsCount+number);
                CartItemMapper.getInstance().update(cartItem);
            }else{
                foods.put(food, number);
                CartItem cartItem = new CartItem(userId, food, number);
                CartItemMapper.getInstance().insert(cartItem);
            }
        }else{
            throw new OrderFromDifferentRestaurant();
        }
//        for(Map.Entry<Food, Integer> cartfood : foods.entrySet()){
//            System.out.println(cartfood.getKey().getName() + "/" + cartfood.getValue() + "/" + food.getPrice());
//        }
    }

    public int getFoodCount(String foodName){
        for(Map.Entry<Food, Integer> food : foods.entrySet()){
            if(food.getKey().getName().equals(foodName)){
                return food.getValue();
            }
        }
        return 0;
    }

    public void empty(){
        foods.clear();
        restaurantName = "";
        restaurantId = "";
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void deleteFoodFromCart(String foodName) throws FoodNotFound {
        boolean foodFound = false;
        Food targetfood = null;
        for (Map.Entry<Food, Integer> food : foods.entrySet()) {
            System.out.println("In Delete from cart module : ");
            System.out.println(food.getKey().getName() + "/" + food.getValue());
            if (food.getKey().getName().equals(foodName)) {
                targetfood = food.getKey();
                foodFound = true;
            }
        }
        if (foodFound == false) {
            throw new FoodNotFound();
        }
        try {
            if (foods.get(targetfood) == 1) {
                foods.remove(targetfood);
                CartItemMapper.getInstance().delete(new ArrayList<>(Arrays.asList(userId, targetfood.getName())));
                if (foods.size() == 0) {
                    restaurantId = "";
                    restaurantName = "";
                    ArrayList<String> result = new ArrayList<String>();
                    result.add(userId);
                    result.add("");
                    CartMapper.getInstance().update(result);
                }
            } else {
                foods.replace(targetfood, foods.get(targetfood) - 1);
                CartItem cartItem = new CartItem(userId, targetfood, foods.get(targetfood));
                CartItemMapper.getInstance().update(cartItem);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public int calculatePrice(){
        int price = 0;
        if(restaurantName.equals("")){
            return price;
        }
        for(Map.Entry<Food, Integer> food : foods.entrySet()){
            price += food.getKey().getPrice() * food.getValue();
        }
        return price;
    }

    public void deletePartyFoods(){
        try {
            for (Map.Entry<Food, Integer> food : foods.entrySet()) {
                FoodUnderSale foodUnderSale = LoghmeRepository.getCurInstance().getFoodUnderSale(food.getKey());
//                System.out.println(foodUnderSale);
                if (foodUnderSale != null) {
                    foods.remove(food.getKey());
                    CartItemMapper.getInstance().delete(new ArrayList<>(Arrays.asList(userId, food.getKey().getName())));
                }
            }
            if (foods.size() == 0) {
                restaurantId = "";
                restaurantName = "";
                ArrayList<String> result = new ArrayList<String>();
                result.add(userId);
                result.add("");
                CartMapper.getInstance().update(result);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
    public int getSize(){
        int size = 0;
        for(Map.Entry<Food, Integer> food : foods.entrySet()){
            size += food.getValue();
        }
        return  size;
    }

    public void setFoods(Map<Food, Integer> foods) {
        this.foods = foods;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
