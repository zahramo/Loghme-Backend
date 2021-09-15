package ir.ac.ut.ece.ie.loghme.service.info;

import ir.ac.ut.ece.ie.loghme.repository.model.LoghmeRepository;
import ir.ac.ut.ece.ie.loghme.repository.model.Food;

import java.util.Map;

public class RestaurantFoodInfo {
    private String name;
    private int popularity;
    private int price;
    private String image;
    private String description;
    private int numberInCart ;
    private String restaurantId;

    public int getPopularity() {
        return popularity;
    }

    public int getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public int getNumberInCart() {
        return numberInCart;
    }

    public String getDescription() {
        return description;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public RestaurantFoodInfo(Food food){
        this.image = food.getImage();
        this.price = food.getPrice();
        this.popularity = food.getPopularity();
        this.name = food.getName();
        this.description = food.getDescription();
        this.restaurantId = food.getRestaurantId();

//        for(Map.Entry<Food, Integer> entry : LoghmeRepository.getCurInstance().getUserCart().getFoods().entrySet()){
//            if(entry.getKey().getName().equals(this.name)){
//                this.numberInCart = entry.getValue();
//            }
//        }
    }
}
