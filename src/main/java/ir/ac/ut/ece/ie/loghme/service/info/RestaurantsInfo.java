package ir.ac.ut.ece.ie.loghme.service.info;

import ir.ac.ut.ece.ie.loghme.repository.model.Restaurant;

public class RestaurantsInfo {
    String logo;
    String name;
    String id;

    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }

    public String getId() {
        return id;
    }

    public RestaurantsInfo(Restaurant restaurant){
        this.logo = restaurant.getLogo();
        this.name = restaurant.getName();
        this.id = restaurant.getId();
    }
}
