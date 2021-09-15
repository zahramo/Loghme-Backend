package ir.ac.ut.ece.ie.loghme.service;

import io.jsonwebtoken.Claims;
import ir.ac.ut.ece.ie.loghme.repository.exception.*;
import ir.ac.ut.ece.ie.loghme.repository.model.LoghmeRepository;
import ir.ac.ut.ece.ie.loghme.repository.model.FoodUnderSale;
import ir.ac.ut.ece.ie.loghme.repository.model.Restaurant;
import ir.ac.ut.ece.ie.loghme.service.info.FoodPartiesInfo;
import ir.ac.ut.ece.ie.loghme.service.info.FoodPartyInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class FoodPartyService {
    @RequestMapping(value = "/partyCart/{foodName}/{restaurantId}/{num}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addPartyFoodToCart(@PathVariable(value = "foodName") String foodName,
                                             @PathVariable(value = "restaurantId") String restaurantId,
                                             @PathVariable(value = "num") String num ,@RequestAttribute Claims claims){
        try {
            System.out.println("In food party service : ");
            System.out.println(num);
            LoghmeRepository.getCurInstance().addFoodToUserCart(claims.getId(),foodName,
                    restaurantId,Integer.parseInt(num));
            return ResponseEntity.ok("ok");
        } catch (RestaurantNotFound restaurantNotFound) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("رستورانی با این نام وجود ندارد");

        } catch (RestaurantOutOfRegion restaurantOutOfRegion) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("این رستوران خارج از محدوده است");
        } catch (FoodNotFound foodNotFound) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("غذایی با این نام وجود ندارد");
        } catch (OrderFromDifferentRestaurant orderFromDifferentRestaurant) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("شما هنوز سفارش قبلیتان را تکمیل نکرده اید");
        } catch (NotEnoughPartyFood notEnoughPartyFood) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("تعداد انتخاب شده از موجودی غذا بیشتر است");
        }
    }

    @RequestMapping(value = "/foodParty", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getFoodParty(){
        ArrayList<FoodUnderSale> foodUnderSales = LoghmeRepository.getCurInstance().getValidFoodParty();
        ArrayList<FoodPartyInfo> foodPartyInfos = new ArrayList<>();
        for (int i=0; i<foodUnderSales.size(); i++){
            try {
                FoodPartyInfo foodPartyInfo = new FoodPartyInfo(foodUnderSales.get(i));
                foodPartyInfos.add(foodPartyInfo);
            } catch (RestaurantOutOfRegion restaurantOutOfRegion) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("این رستوران خارج از محدوده است");
            } catch (RestaurantNotFound restaurantNotFound) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("رستورانی با این نام وجود ندارد");
            }
        }
        FoodPartiesInfo foodPartiesInfo = new FoodPartiesInfo(foodPartyInfos);
        return ResponseEntity.ok(foodPartiesInfo);
    }
}
