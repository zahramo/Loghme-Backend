package ir.ac.ut.ece.ie.loghme.repository.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.cart.CartItemMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.cart.CartMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.food.FoodMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.order.CourierMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.order.OrderFoodMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.order.OrderItem;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.order.OrderMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.foodParty.FoodPartyMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.restaurant.RestaurantMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.user.UserMapper;
import ir.ac.ut.ece.ie.loghme.repository.exception.*;
import ir.ac.ut.ece.ie.loghme.repository.http.LoghmeHttpClient;
import ir.ac.ut.ece.ie.loghme.repository.scheduler.DeliverOrder;
import ir.ac.ut.ece.ie.loghme.repository.scheduler.FoodPartyDataGetter;
import java.sql.SQLException;
import java.util.*;

public class LoghmeRepository {
    private static LoghmeRepository curInstance;
    private ArrayList<Restaurant> restaurants;
    private LoghmeHttpClient httpClient;
    private ArrayList<FoodUnderSale> foodPartyMenu;
    private long foodPartyEndTime;

    public static LoghmeRepository getCurInstance() {
        if(curInstance == null){
            curInstance = new LoghmeRepository();
        }
        return curInstance;
    }

    public void setFoodPartyEndTime(long foodPartyEndTime) {
        this.foodPartyEndTime = foodPartyEndTime;
    }

    private LoghmeRepository() {

        restaurants = new ArrayList<>();
        try {
            fillRestaurantsData();
            addRestaurantsToDB();
        } catch (Exception e) {
            System.out.println("Error in getting restaurants data from api");
        }
    }

    public void finalizeUserOrder(String userId) throws NotEnoughCredit, EmptyCart {
        try {
            User user = getUserFromDB(userId);
            int remainingCredit = user.getCredit() - user.getCart().calculatePrice();
            if(remainingCredit < 0){
                throw new NotEnoughCredit();
            }
            if(user.getCart().getSize() == 0){
                throw new EmptyCart();
            }

            Order order = new Order(user.getCart());
            order.setId(Integer.toString(user.getOrders().size() + 1));
            order.setState(State.SearchingForCourier);
            try {
                user.getOrders().add(order);
                addOrderToDB(order);
                user.setCredit(remainingCredit);
                UserMapper.getInstance().updateCredit(user.getEmail(), remainingCredit);
            }catch (SQLException e){
                System.out.println("Error in adding Order");
            }
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new DeliverOrder(order),0,3000);

            decreaseFoodPartyCounts(user.getCart());
            CartItemMapper.getInstance().deleteAll(new ArrayList<>(Collections.singletonList(user.getEmail())));
            ArrayList<String> result = new ArrayList<String>();
            result.add(user.getEmail());
            result.add("");
            CartMapper.getInstance().update(result);

        } catch (SQLException ignored) {
        }
//        user.getCart().setRestaurantId("");
    }

    public void decreaseFoodPartyCounts(Cart cart) throws SQLException {
        String restaurantId= cart.getRestaurantId();
        for(Map.Entry<Food, Integer> food : cart.getFoods().entrySet()){
            if(food.getKey() instanceof FoodUnderSale){
                int newCount = ((FoodUnderSale) food.getKey()).getCount() - food.getValue();
                ((FoodUnderSale) food.getKey()).setCount(newCount);
                if(newCount == 0){
                    ((FoodUnderSale) food.getKey()).setValid(0);
                    food.getKey().setPrice(((FoodUnderSale) food.getKey()).getOldPrice());
                }
                FoodPartyMapper.getInstance().update((FoodUnderSale) food.getKey());
            }
        }
    }


    public void fillRestaurantsData() throws Exception {
        httpClient = new LoghmeHttpClient();
        try {
            System.out.println("Send Http GET request for Restaurants Data");
            httpClient.sendGet("http://138.197.181.131:8080/restaurants");
        }finally {
            httpClient.close();
        }
        ObjectMapper mapper = new ObjectMapper();
        restaurants = (ArrayList<Restaurant>) mapper.readValue(httpClient.getResponseData(), new TypeReference<List<Restaurant>>(){});
        for (Restaurant restaurant : restaurants) {
            setFoodRestaurantsId(restaurant.getMenu(), restaurant.getId());
        }
    }


    //Todo change this function using database
    public long getFoodPartyEndTime() {
        return foodPartyEndTime;
    }

    public void addRestaurantsToDB(){
        for (Restaurant restaurant : restaurants) {
            addRestaurantToDB(restaurant);
        }
    }

    public void addRestaurantToDB(Restaurant restaurant){
        try {
            System.out.println("inserting " + restaurant.getName() + " to DB...");
            RestaurantMapper.getInstance().insert(restaurant);
            ArrayList<Food> restaurantMenu = restaurant.getMenu();
            ArrayList<FoodUnderSale> restaurantFoodPartyMenu = restaurant.getFoodPartyMenu();
            for (Food menu : restaurantMenu) {
                addFoodToDB(menu);
            }
            for (FoodUnderSale partyMenu : restaurantFoodPartyMenu) {
                addFoodPartyToDB(partyMenu);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public void setFoodRestaurantsId(ArrayList<Food> foods, String restaurantId){
        for (Food food : foods) {
            food.setRestaurantId(restaurantId);
        }
    }

    public ArrayList<Restaurant> getRestaurants(String page, String count) {
        ArrayList<Restaurant> allRestaurants = new ArrayList<>();
        try {
            ArrayList<String> args = new ArrayList<>();
            //toDo insert User Location here
            args.add("0");
            args.add("0");
            args.add(page);
            args.add(count);
            allRestaurants = (ArrayList<Restaurant>) RestaurantMapper.getInstance().findAll(args);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return allRestaurants;
    }

    public boolean doesRestaurantExist(String restaurantId){
        Restaurant findRestaurant = null;
        ArrayList<String> args = new ArrayList<>();
        args.add(restaurantId);
        try {
            findRestaurant = RestaurantMapper.getInstance().find(args);
            if(findRestaurant == null) return false;
            return true;
        } catch (SQLException e) {
            System.out.println("error in find restaurant in doesRestaurantExist func");
            return false;
        }
    }

    public void addFoodToUserCart(String userId, String foodName, String restaurantId, int number)
            throws RestaurantNotFound, FoodNotFound, OrderFromDifferentRestaurant, RestaurantOutOfRegion, NotEnoughPartyFood {
        Cart userCart = getUserFromDB(userId).getCart();
        try {
            userCart.addFood(FoodMapper.getInstance().
                    find(new ArrayList<>(Arrays.asList(restaurantId, foodName))), number);
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public Restaurant getRestaurant(String restaurantId) throws RestaurantNotFound, RestaurantOutOfRegion {
        ArrayList<String> args = new ArrayList<>();
        args.add(restaurantId);
        try {
            Restaurant restaurant = RestaurantMapper.getInstance().find(args);
            if(restaurant == null){ throw new RestaurantNotFound(); }
            restaurant.setMenu((ArrayList<Food>) FoodMapper.getInstance().findAll(args));
            return restaurant;
        } catch (SQLException e) {
            System.out.println("error in get restaurant");
            throw new RestaurantNotFound();
        }
    }

    public void finalize()
    {
        for(int i=0; i<restaurants.size(); i++)
            restaurants.get(i).finalize();
        restaurants.clear();
    }



    public void getFoodPartyData() throws Exception {
        try {
            System.out.println("Send Http GET request for FoodParry Data");
            httpClient.sendGet("http://138.197.181.131:8080/foodparty");
        }finally {
            httpClient.close();
        }
        ObjectMapper mapper = new ObjectMapper();
        String foodPartyData = httpClient.getResponseData();
        foodPartyData = foodPartyData.replaceAll("menu", "foodPartyMenu");
        ArrayList<Restaurant> foodPartyRestaurants;
        foodPartyRestaurants = (ArrayList<Restaurant>) mapper.readValue(foodPartyData, new TypeReference<List<Restaurant>>(){});
        foodPartyRestaurants = setRestaurantId(foodPartyRestaurants);
        fillFoodPartyData(foodPartyRestaurants);
    }

    private ArrayList<Restaurant> setRestaurantId(ArrayList<Restaurant> curRestaurants){
        for(int i=0; i<curRestaurants.size(); i++){
            for(int j=0; j<curRestaurants.get(i).getFoodPartyMenu().size(); j++){
                curRestaurants.get(i).getFoodPartyMenu().get(j).setRestaurantId(curRestaurants.get(i).getId());
            }
        }
        return curRestaurants;
    }

    public void fillFoodPartyData(ArrayList<Restaurant> foodPartyRestaurants){

        try {
            FoodPartyMapper.getInstance().updateAllValidations(0);
            FoodMapper.getInstance().updateAllUnderSale();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        for(int i=0; i<foodPartyRestaurants.size(); i++){
            if(!doesRestaurantExist(foodPartyRestaurants.get(i).getId())){
                addRestaurantToDB(foodPartyRestaurants.get(i));
            }
            else{
                for(int j=0; j<foodPartyRestaurants.get(i).getFoodPartyMenu().size(); j++){
                    addFoodPartyToDB(foodPartyRestaurants.get(i).getFoodPartyMenu().get(j));
                }
            }

        }
    }

    public void addFoodToDB(Food food){
        try {
            FoodMapper.getInstance().insert(food);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addFoodPartyToDB(FoodUnderSale foodUnderSale){
        try {
//            System.out.println(foodUnderSale.getName());
            int oldPrice = foodUnderSale.getOldPrice();


            if(!doesFoodExist(foodUnderSale.getRestaurantId(), foodUnderSale.getName())){
                foodUnderSale.setPrice(oldPrice);
                addFoodToDB(foodUnderSale);
            }else{
                FoodMapper.getInstance().updateUnderSale(foodUnderSale);
                if(!doesFoodPartyExist(foodUnderSale.getRestaurantId(), foodUnderSale.getName())){
                    FoodPartyMapper.getInstance().insert(foodUnderSale);
                }else{
                    FoodPartyMapper.getInstance().update(foodUnderSale);
                }
            }

        } catch (SQLException e) {
            System.out.println("error in addFoodToDB func");
        }
    }


    public boolean doesFoodExist(String restaurantId, String foodName){
        ArrayList<String> args = new ArrayList<>();
        Food findFood = null;
        args.add(restaurantId);
        args.add(foodName);
        try {
            findFood = FoodMapper.getInstance().find(args);
            if(findFood == null) return false;
            return true;
        } catch (SQLException e) {
            System.out.println("error in doesFoodExist func");
            return false;
        }
    }

    public boolean doesFoodPartyExist(String restaurantId, String foodName){
        ArrayList<String> args = new ArrayList<>();
        FoodUnderSale findFood = null;
        args.add(restaurantId);
        args.add(foodName);
        try {
            findFood = FoodPartyMapper.getInstance().find(args);
            if(findFood == null) return false;
            return true;
        } catch (SQLException e) {
            System.out.println("error in doesFoodPartyExist func");
            return false;
        }
    }

    public void deletePartyFoodsFromUsersCart(){
        try {
            List<User> loghmeUsers = UserMapper.getInstance().findAll(new ArrayList<>());
            System.out.println(loghmeUsers);
            for(User user : loghmeUsers){
                user.getCart().deletePartyFoods();
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<FoodUnderSale> getValidFoodParty(){
        ArrayList<FoodUnderSale> foodUnderSales = new ArrayList<>();
        ArrayList<Food> foods = new ArrayList<>();
        try {
            foodUnderSales = (ArrayList<FoodUnderSale>) FoodPartyMapper.getInstance().findAll(null);
            foods = (ArrayList<Food>) FoodMapper.getInstance().findAllFoodParty();
            for(int i=0; i<foodUnderSales.size(); i++){
                for(int j=0; j<foods.size(); j++){
                    if(foodUnderSales.get(i).getRestaurantId().equals(foods.get(j).getRestaurantId()) && foodUnderSales.get(i).getName().equals(foods.get(j).getName())){
                        foodUnderSales.get(i).setPopularity(foods.get(j).getPopularity());
                        foodUnderSales.get(i).setImage(foods.get(j).getImage());
                        foodUnderSales.get(i).setDescription(foods.get(j).getDescription());
                        foodUnderSales.get(i).setOldPrice(foods.get(j).getPrice());
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return foodUnderSales;
    }


    public void addUserToDb(String firstName, String lastName, String phoneNumber, String email, String password) throws SQLException {
        try {
            int newId = UserMapper.getInstance().findMaxId() + 1;
            User newUser = new User(firstName, lastName, phoneNumber, email, password, newId);
            UserMapper.getInstance().insert(newUser);
            ArrayList<String> result = new ArrayList<String>();
            result.add(newUser.getEmail());
            result.add("");
            CartMapper.getInstance().insert(result);
        }catch (SQLException e){
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public void updateUserCreditInDB(User user, int increaseAmount){
        try {
            UserMapper.getInstance().updateCredit(user.getEmail(), user.getCredit() + increaseAmount);
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public User getUserFromDB(String id){
        try {
            User user = UserMapper.getInstance().findById(id);
            System.out.println("In GetUser From DB: " + user.getEmail());
            System.out.println("In GetUser From DB: " + user.getCart().getRestaurantId());
            System.out.println("In GetUser From DB: " + user.getOrders().size());
            return user;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void addOrderToDB(Order order) throws SQLException {
        OrderMapper.getInstance().insert(order);
        for (Map.Entry<Food, Integer> food : order.getFoods().entrySet()) {
            OrderItem orderItem = new OrderItem(order, food.getKey(), food.getValue());
            OrderFoodMapper.getInstance().insert(orderItem);
        }
    }

    public ArrayList<Restaurant> searchWithRestaurantName(String restaurantName, String page, String count){
        ArrayList<Restaurant> findRestaurants = new ArrayList<>();
        try {
            findRestaurants = (ArrayList<Restaurant>) RestaurantMapper.getInstance().searchInRestaurantName(restaurantName, Integer.valueOf(page)*Integer.valueOf(count));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return findRestaurants;
    }

    public ArrayList<Restaurant> searchWithFoodName(String foodName, String page, String count){
        ArrayList<Restaurant> findRestaurants = new ArrayList<>();
        try {
            findRestaurants = (ArrayList<Restaurant>) RestaurantMapper.getInstance().searchInFoodName(foodName, Integer.valueOf(page)*Integer.valueOf(count));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return findRestaurants;
    }

    public ArrayList<Restaurant> searchWithRestaurantAndFoodName(String restaurantName,String foodName, String page, String count){
        ArrayList<Restaurant> findRestaurants = new ArrayList<>();
        try {
            findRestaurants = (ArrayList<Restaurant>) RestaurantMapper.getInstance().searchInFoodAndRestaurantName(foodName, restaurantName, Integer.valueOf(page)*Integer.valueOf(count));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return findRestaurants;
    }

    public FoodUnderSale getFoodUnderSale(Food food) {
        try {
            List<Food> foodparty = FoodMapper.getInstance().findAllFoodParty();
            for(int i=0; i<foodparty.size(); i++) {
                if (foodparty.get(i).getName().equals(food.getName()) &&
                    foodparty.get(i).getRestaurantId().equals(food.getRestaurantId())) {
                    FoodUnderSale foodUnderSale = FoodPartyMapper.getInstance().
                            find(new ArrayList<>(Arrays.asList(food.getRestaurantId(), food.getName())));
                    foodUnderSale.setDescription(food.getDescription());
                    foodUnderSale.setImage(food.getImage());
                    foodUnderSale.setPopularity(food.getPopularity());
                    foodUnderSale.setOldPrice(food.getPrice());
                    return foodUnderSale;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public void addCouriersToDB(ArrayList<Courier> couriers){
        try {
            for (Courier courier : couriers) {
                CourierMapper.getInstance().insert(courier);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean userExists(String email) throws SQLException {
        User user = UserMapper.getInstance().find(new ArrayList<>(Arrays.asList(email)));
        if(user == null){
            return false;
        }
        else {
            return true;

        }
    }

    public int findUser(String email, String password){
        int id = -1;
        User curUser = null;
        try {
            curUser = UserMapper.getInstance().find(new ArrayList<>(Collections.singletonList(email)));
            if(curUser != null){
                if(curUser.getPasswordHash().equals(password)){
                    id = curUser.getId();
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return id;
    }
}
