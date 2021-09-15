package ir.ac.ut.ece.ie.loghme.service;

import io.jsonwebtoken.Claims;
import ir.ac.ut.ece.ie.loghme.Jwt;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.user.UserMapper;
import ir.ac.ut.ece.ie.loghme.repository.exception.*;
import ir.ac.ut.ece.ie.loghme.repository.model.LoghmeRepository;
import ir.ac.ut.ece.ie.loghme.repository.model.*;
import ir.ac.ut.ece.ie.loghme.service.info.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

@RestController
public class UserService {
    @RequestMapping(value = "/credit/{increaseAmount}", method = RequestMethod.POST)
    public ResponseEntity increaseCredit(@PathVariable(value = "increaseAmount") int increaseAmount,
                                         @RequestAttribute Claims claims) {
        User user = LoghmeRepository.getCurInstance().getUserFromDB(claims.getId());
        LoghmeRepository.getCurInstance().updateUserCreditInDB(user, increaseAmount);
        return ResponseEntity.ok(user.getCredit());
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity getProfile(@RequestAttribute Claims claims) {
        System.out.println("In get Profile: "+ claims.getId());
        User user = LoghmeRepository.getCurInstance().getUserFromDB(claims.getId());
        System.out.println(user.getEmail());
        UserInfo userInfo = new UserInfo(user);
        return ResponseEntity.ok(userInfo);
    }

    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    public ResponseEntity getCart(@RequestAttribute Claims claims) {
        Cart userCart = LoghmeRepository.getCurInstance().getUserFromDB(claims.getId()).getCart();
        for(Map.Entry<Food, Integer> food : userCart.getFoods().entrySet()){
            System.out.println(food.getKey().getName() + "/" + food.getKey().getPrice());
        }
        CartInfo info = new CartInfo(userCart);
        return ResponseEntity.ok(info);
    }

    @RequestMapping(value = "/cart/foodCount", method = RequestMethod.GET)
    public ResponseEntity getCartNum(@RequestAttribute Claims claims){
        int cartNum = LoghmeRepository.getCurInstance().getUserFromDB(claims.getId()).getCart().getSize();
        return ResponseEntity.ok(cartNum);
    }


    @RequestMapping(value = "/cart/{foodName}/{restaurantId}/{number}", method = RequestMethod.POST)
    public ResponseEntity addToCart(
            @PathVariable(value = "foodName") String foodName,
            @PathVariable(value = "restaurantId") String restaurantId,
            @PathVariable(value = "number") String number,
            @RequestAttribute Claims claims) {
        try {
            System.out.println("Restaurant Id: "+restaurantId  );
                LoghmeRepository.getCurInstance().addFoodToUserCart(claims.getId(),foodName,
                        restaurantId,Integer.parseInt(number));
        } catch (RestaurantNotFound restaurantNotFound) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("رستورانی با این نام وجود ندارد");
        } catch (FoodNotFound foodNotFound) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("غذایی با این نام وجود ندارد");
        } catch (OrderFromDifferentRestaurant orderFromDifferentRestaurant) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("شما هنوز سفارش قبلیتان را تکمیل نکرده اید");
        } catch (RestaurantOutOfRegion restaurantOutOfRegion) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("این رستوران خارج از محدوده است");
        } catch (NotEnoughPartyFood notEnoughPartyFood) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("تعداد انتخاب شده از موجودی غذا بیشتر است");
        }
        return ResponseEntity.ok(4);
    }

    @RequestMapping(value = "/cart/{foodName}", method = RequestMethod.DELETE)
    public ResponseEntity deleteFoodFromCart(@PathVariable(value = "foodName") String foodName,
                                             @RequestAttribute Claims claims) {
        try {
            LoghmeRepository.getCurInstance().getUserFromDB(claims.getId())
                    .getCart().deleteFoodFromCart(foodName);
        } catch (FoodNotFound foodNotFound) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("غذایی با این نام وجود ندارد");

        }

        return ResponseEntity.ok("ok");
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public ResponseEntity finalizeOrder(@RequestAttribute Claims claims) {
        try {
            System.out.println("In finalizeOrder Service: " + claims.getId());
            LoghmeRepository.getCurInstance().finalizeUserOrder(claims.getId());
        } catch (NotEnoughCredit notEnoughCredit) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("اعتبار شما کافی نیست");

        } catch (EmptyCart emptyCart) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("سبد خرید شما خالی است");

        }
        return ResponseEntity.ok("سفارش شما با موفقیت ثبت شد، وضعیت سفارش را از صفحه‌ی پروفایل خود دنبال کنید ");
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public ResponseEntity getOrders(@RequestAttribute Claims claims) {
        ArrayList<Order> orders;
        orders = LoghmeRepository.getCurInstance().getUserFromDB(claims.getId()).getOrders();
        ArrayList<OrderInfo> orderInfos = new ArrayList<>();
        for(int i=0; i<orders.size(); i++){
            OrderInfo info = new OrderInfo(orders.get(i));
            orderInfos.add(info);
        }
        return ResponseEntity.ok(orderInfos);
    }
}
