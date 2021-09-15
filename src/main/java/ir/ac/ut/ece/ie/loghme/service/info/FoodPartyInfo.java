package ir.ac.ut.ece.ie.loghme.service.info;

import ir.ac.ut.ece.ie.loghme.repository.model.LoghmeRepository;
import ir.ac.ut.ece.ie.loghme.repository.exception.RestaurantNotFound;
import ir.ac.ut.ece.ie.loghme.repository.exception.RestaurantOutOfRegion;
import ir.ac.ut.ece.ie.loghme.repository.model.FoodUnderSale;

import java.time.LocalDateTime;

public class FoodPartyInfo {
    private String name;
    private String description;
    private float popularity;
    private String restaurantName;
    private String restaurantId;
    private int discountprice;
    private String image;
    private int count;
    private int oldPrice;
    private int numberInCart = 0;


    public String getName() {
        return name;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public float getPopularity() {
        return popularity;
    }

    public int getCount() {
        return count;
    }

    public int getDiscountprice() {
        return discountprice;
    }

    public String getDescription() {
        return description;
    }

    public int getOldPrice() {
        return oldPrice;
    }

    public String getImage() {
        return image;
    }

    public int getNumberInCart() {
        return numberInCart;
    }



    public FoodPartyInfo(FoodUnderSale foodUnderSale) throws RestaurantOutOfRegion, RestaurantNotFound {
        this.count = foodUnderSale.getCount();
        this.description = foodUnderSale.getDescription();
        this.discountprice = foodUnderSale.getPrice();
        this.image = foodUnderSale.getImage();
        this.name = foodUnderSale.getName();
        this.oldPrice = foodUnderSale.getOldPrice();
        this.popularity = foodUnderSale.getPopularity();
        this.restaurantName = LoghmeRepository.getCurInstance().getRestaurant(foodUnderSale.getRestaurantId()).getName();
        this.restaurantId = foodUnderSale.getRestaurantId();


    }
}
