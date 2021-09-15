package ir.ac.ut.ece.ie.loghme.service.info;

import ir.ac.ut.ece.ie.loghme.repository.model.Restaurant;

import java.util.ArrayList;

public class RestaurantInfo {
    private String name;
    private String logo;
    private ArrayList<RestaurantFoodInfo> menu;


    public ArrayList<RestaurantFoodInfo> getMenu() {
        return menu;
    }

    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }


    public RestaurantInfo(Restaurant restaurant){
        this.name = restaurant.getName();
        this.logo = restaurant.getLogo();
        ArrayList<RestaurantFoodInfo> foodsInfo = new ArrayList<>();
        for(int i=0; i<restaurant.getMenu().size(); i++){
            RestaurantFoodInfo foodInfo = new RestaurantFoodInfo(restaurant.getMenu().get(i));
            foodsInfo.add(foodInfo);
        }
        this.menu = foodsInfo;
    }
}
