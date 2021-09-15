package ir.ac.ut.ece.ie.loghme.service;

import ir.ac.ut.ece.ie.loghme.repository.exception.RestaurantNotFound;
import ir.ac.ut.ece.ie.loghme.repository.exception.RestaurantOutOfRegion;
import ir.ac.ut.ece.ie.loghme.repository.model.LoghmeRepository;
import ir.ac.ut.ece.ie.loghme.repository.model.Restaurant;
import ir.ac.ut.ece.ie.loghme.service.info.RestaurantInfo;
import ir.ac.ut.ece.ie.loghme.service.info.RestaurantsInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class RestaurantService {

    @RequestMapping(value = "/restaurant/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getRestaurant(@PathVariable(value = "id") String id){
        Restaurant restaurant = new Restaurant();
        RestaurantInfo restaurantInfo;
        try {
            restaurant = LoghmeRepository.getCurInstance().getRestaurant(id);
            restaurantInfo = new RestaurantInfo(restaurant);
            return ResponseEntity.ok(restaurantInfo);
        } catch (RestaurantNotFound restaurantNotFound) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("رستورانی با این نام وجود ندارد");
        } catch (RestaurantOutOfRegion restaurantOutOfRegion) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("این رستوران خارج از محدوده است");

        }
    }

    @RequestMapping(value = "/restaurants/{page}/{count}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getRestaurants(@PathVariable(value = "page") String page, @PathVariable(value = "count") String count){
        ArrayList<Restaurant> restaurants = LoghmeRepository.getCurInstance().getRestaurants(page, count);
        ArrayList<RestaurantsInfo> restaurantsInfos = new ArrayList<>();
        for(int i=0; i<restaurants.size(); i++){
            RestaurantsInfo restaurantsInfo = new RestaurantsInfo(restaurants.get(i));
            restaurantsInfos.add(restaurantsInfo);
        }
        return ResponseEntity.ok(restaurantsInfos);
    }
}
