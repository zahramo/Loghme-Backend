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
public class SearchService {
    @RequestMapping(value = "/search/{restaurantName}/{foodName}/{page}/{count}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity search(@PathVariable(value = "restaurantName") String restaurantName, @PathVariable(value = "foodName") String foodName, @PathVariable(value = "page") String page, @PathVariable(value = "count") String count){
        foodName = foodName.trim();
        restaurantName = restaurantName.trim();
        ArrayList<Restaurant> findRestaurants = LoghmeRepository.getCurInstance().searchWithRestaurantAndFoodName(restaurantName, foodName, page, count);
        ArrayList<RestaurantsInfo> findRestaurantInfos = new ArrayList<>();
        for(int i=0; i< findRestaurants.size(); i++){
            RestaurantsInfo restaurantsInfo = new RestaurantsInfo(findRestaurants.get(i));
            findRestaurantInfos.add(restaurantsInfo);
        }
        return ResponseEntity.ok(findRestaurantInfos);
    }
    @RequestMapping(value = "/searchFood/{foodName}/{page}/{count}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchFood( @PathVariable(value = "foodName") String foodName, @PathVariable(value = "page") String page, @PathVariable(value = "count") String count) {
        foodName = foodName.trim();
        ArrayList<Restaurant> findRestaurants = LoghmeRepository.getCurInstance().searchWithFoodName(foodName, page, count);
        ArrayList<RestaurantsInfo> findRestaurantInfos = new ArrayList<>();
        for (int i = 0; i < findRestaurants.size(); i++) {
            RestaurantsInfo restaurantsInfo = new RestaurantsInfo(findRestaurants.get(i));
            findRestaurantInfos.add(restaurantsInfo);
        }
        return ResponseEntity.ok(findRestaurantInfos);
    }
    @RequestMapping(value = "/searchRestaurant/{restaurantName}/{page}/{count}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchRestaurant(@PathVariable(value = "restaurantName") String restaurantName, @PathVariable(value = "page") String page, @PathVariable(value = "count") String count) {
        restaurantName = restaurantName.trim();
        ArrayList<Restaurant> findRestaurants = LoghmeRepository.getCurInstance().searchWithRestaurantName(restaurantName, page, count);
        ArrayList<RestaurantsInfo> findRestaurantInfos = new ArrayList<>();
        for (int i = 0; i < findRestaurants.size(); i++) {
            RestaurantsInfo restaurantsInfo = new RestaurantsInfo(findRestaurants.get(i));
            findRestaurantInfos.add(restaurantsInfo);
        }
        return ResponseEntity.ok(findRestaurantInfos);
    }

}


