package ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.user;

import ir.ac.ut.ece.ie.loghme.repository.dataAccess.ConnectionPool;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.Mapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.cart.CartItem;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.cart.CartItemMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.cart.CartMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.food.FoodMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.food.FoodToPartyFood;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.order.OrderFoodMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.order.OrderItem;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.order.OrderMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.restaurant.RestaurantMapper;
import ir.ac.ut.ece.ie.loghme.repository.model.*;
//import javafx.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserMapper extends Mapper<User, String> {
    private static UserMapper instance;

    public static UserMapper getInstance() {
        if(instance == null){
            try {
                instance = new UserMapper();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return instance;
    }

    private UserMapper() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement createUserTableStatement = con.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                "`User` (id int,\n" +
                "    firstName CHAR(30),\n" +
                "    lastName CHAR(30),\n" +
                "    phoneNumber CHAR(11),\n" +
                "    email CHAR(50),\n" +
                "    passwordHash CHAR(100),\n" +
                "    credit int,\n" +
                "    locationX int,\n" +
                "    locationY int,\n" +
                "    PRIMARY KEY(id),\n" +
                "    UNIQUE (email));\n");
        createUserTableStatement.executeUpdate();
        createUserTableStatement.close();
        con.close();
    }

    @Override
    protected String getFindStatement() {
        return "Select * FROM User U WHERE U.email = ? \n";
    }

    @Override
    protected void fillFindValues(PreparedStatement st, ArrayList<String> ids) throws SQLException {
        st.setString(1,ids.get(0));
    }

    @Override
    protected String getInsertStatement() {
        return "INSERT INTO User(id, firstName, lastName, phoneNumber, email, passwordHash, credit, locationX, locationY) VALUES(?,?,?,?,?,?,?,?,?)";
    }

    @Override
    protected void fillInsertValues(PreparedStatement st, User data) throws SQLException {
        st.setInt(1, data.getId());
        st.setString(2, data.getFirstName());
        st.setString(3, data.getLastName());
        st.setString(4, data.getPhoneNumber());
        st.setString(5, data.getEmail());
        st.setString(6,data.getPasswordHash());
        st.setInt(7, data.getCredit());
        st.setInt(8, data.getLocation().getX());
        st.setInt(9, data.getLocation().getY());
    }

    @Override
    protected String getDeleteStatement() {
        return null;
    }

    @Override
    protected void fillDeleteValues(PreparedStatement st, ArrayList<String> ids) throws SQLException {

    }

    @Override
    protected String getFindAllStatement() {
        return "SELECT* FROM User;";
    }

    @Override
    protected void fillFindAllValues(PreparedStatement st, ArrayList<String> ids) throws SQLException {

    }

    @Override
    protected User convertResultSetToDomainModel(ResultSet rs) throws SQLException {
        String firstName = rs.getString(2);
        String lastName = rs.getString(3);
        String phoneNumber = rs.getString(4);
        String email = rs.getString(5);
        String passwordHash = rs.getString(6);
        int credit = rs.getInt(7);
        int locationX = rs.getInt(8);
        int locationY = rs.getInt(9);
        int id = rs.getInt(1);
        User user = new User(firstName, lastName, phoneNumber, email, passwordHash, id);
        user.setCredit(credit);
        user.setLocation(locationX, locationY);
//        ArrayList<String> ids = new ArrayList<>();
//        ids.add(email);
//        List<Order> userOrders = OrderMapper.getInstance().findAll(ids);
//        user.setOrders((ArrayList<Order>) userOrders);
//
//        ids.clear();
//        ids.add(email);
//        List<CartItem> cartItems = CartItemMapper.getInstance().findAll(ids);
//
//        Cart cart = new Cart(email);
//        ArrayList<String> result = CartMapper.getInstance().find(new ArrayList<>(Collections.singletonList(email)));
//        if(result != null) {
//            String restaurantId = result.get(1);
//            cart.setRestaurantId(restaurantId);
//            if (!restaurantId.equals("")) {
//                cart.setRestaurantName(RestaurantMapper.getInstance().find(new ArrayList<>(Collections.singletonList(restaurantId))).getName());
//            }
//            Map<Food, Integer> foods = new HashMap<>();
//            for (CartItem item : cartItems) {
//                Food food = FoodMapper.getInstance().find(
//                        new ArrayList<>(Arrays.asList(item.getRestaurantId(), item.getFoodName())));
//                FoodUnderSale foodUnderSale = FoodToPartyFood.getInstance().getFoodUnderSale(food);
//                if(foodUnderSale != null){
//                    food = foodUnderSale;
//                }
//                foods.put(food, item.getNumber());
//            }
//            cart.setFoods(foods);
//        }else{
//            ArrayList<String> res = new ArrayList<String>();
//            res.add(email);
//            res.add("");
//            CartMapper.getInstance().insert(res);
//        }
//        user.setCart(cart);

        return user;
    }

    private List<Order>  getUserOrders(String userEmail) throws SQLException {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(userEmail);
        List<Order> userOrders = OrderMapper.getInstance().findAll(ids);

        for(int i=0; i<userOrders.size(); i++){
            ids.clear();
            ids.add(userOrders.get(i).getUserId());
            ids.add(userOrders.get(i).getId());
            List<OrderItem> orderItems = OrderFoodMapper.getInstance().findAll(ids);
            Map<Food, Integer> foods = new HashMap<>();
            for(OrderItem item : orderItems){
                Food food = new Food();
                food.setRestaurantId(userOrders.get(i).getRestaurantId());
                food.setName(item.getFoodName());
                food.setPrice(item.getPrice());
                foods.put(food, item.getNumber());
            }
            userOrders.get(i).setFoods(foods);
        }
        return userOrders;
    }
    private Cart getUserCart(String userEmail) throws SQLException {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(userEmail);
        List<CartItem> cartItems = CartItemMapper.getInstance().findAll(ids);

        Cart cart = new Cart(userEmail);

        ArrayList<String> result = CartMapper.getInstance().find(new ArrayList<>(Collections.singletonList(userEmail)));
        if(result != null) {
            String restaurantId = result.get(1);
            cart.setRestaurantId(restaurantId);
            if (!restaurantId.equals("")) {
                cart.setRestaurantName(RestaurantMapper.getInstance().find(new ArrayList<>(Collections.singletonList(restaurantId))).getName());
            }
            Map<Food, Integer> foods = new HashMap<>();
            for (CartItem item : cartItems) {
                Food food = FoodMapper.getInstance().find(
                        new ArrayList<>(Arrays.asList(item.getRestaurantId(), item.getFoodName())));
                FoodUnderSale foodUnderSale = FoodToPartyFood.getInstance().getFoodUnderSale(food);
                if(foodUnderSale != null){
                    food = foodUnderSale;
                }
                foods.put(food, item.getNumber());
            }
            cart.setFoods(foods);
        }else{
            ArrayList<String> res = new ArrayList<String>();
            res.add(userEmail);
            res.add("");
            CartMapper.getInstance().insert(res);
        }
        return cart;
    }

    @Override
    protected ArrayList<User> convertResultSetToDomainModelList(ResultSet rs) throws SQLException {
        ArrayList<User> users  = new ArrayList<>();
        while (rs.next()){
            users.add(this.convertResultSetToDomainModel(rs));
        }
        return users;
    }

    // --------- independent functions ---------- //

    protected String getUpdateCreditStatement() {
        return "UPDATE User SET credit = ? WHERE email = ? \n";
    }

    protected void fillUpdateCreditData(PreparedStatement st, String id, int updateValue) throws SQLException {
        st.setInt(1, updateValue);
        st.setString(2,id);
    }

    public void updateCredit(String id, int updateValue) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getUpdateCreditStatement());
        fillUpdateCreditData(st, id, updateValue);
        try {
            st.execute();
            st.close();
            con.close();
        } catch (Exception e) {
            st.close();
            con.close();
            System.out.println(e.getMessage());
        }
    }

    protected String getFindMaxIdStatement() {
        return "SELECT MAX(u.id) FROM User u;\n";
    }

    public int findMaxId() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getFindMaxIdStatement());
        try {
            ResultSet resultSet = st.executeQuery();
            if (!resultSet.next() || resultSet == null) {
                st.close();
                con.close();
                return -1;
            }
            int id = resultSet.getInt(1);
            st.close();
            con.close();
            return id;
        } catch (SQLException e) {
            System.out.println("error in userMapper maxId.");
            e.printStackTrace();
            st.close();
            con.close();
            throw e;
        }
    }

    protected String getFindByIdStatement() {
        return "Select * FROM User U WHERE U.id = ?; \n";
    }

    protected void fillFindByIdValues(PreparedStatement st, String id) throws SQLException {
        st.setString(1,id);
    }

    public User findById(String id) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getFindByIdStatement());
        fillFindByIdValues(st, id);
        try {
            ResultSet resultSet = st.executeQuery();
            if (!resultSet.next()) {
                st.close();
                con.close();
                return null;
            }
            User result = convertResultSetToDomainModel(resultSet);
            st.close();
            con.close();
            result.setOrders((ArrayList<Order>) getUserOrders(result.getEmail()));
            result.setCart(getUserCart(result.getEmail()));
            return result;
        } catch (SQLException ex) {
            System.out.println("error in Mapper.findByID query.");
            st.close();
            con.close();
            throw ex;
        }
    }
}
