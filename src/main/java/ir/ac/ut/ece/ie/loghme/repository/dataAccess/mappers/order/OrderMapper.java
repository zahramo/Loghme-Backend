package ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.order;

import ir.ac.ut.ece.ie.loghme.repository.dataAccess.ConnectionPool;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.Mapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.food.FoodMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.restaurant.RestaurantMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.user.UserMapper;
import ir.ac.ut.ece.ie.loghme.repository.model.Food;
import ir.ac.ut.ece.ie.loghme.repository.model.Order;
import ir.ac.ut.ece.ie.loghme.repository.model.Restaurant;
import ir.ac.ut.ece.ie.loghme.repository.model.State;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderMapper extends Mapper<Order, String> {
    private static OrderMapper instance;

    public static OrderMapper getInstance() {
        if(instance == null){
            try {
                instance = new OrderMapper();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
        return instance;
    }

    private OrderMapper() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement createOrderTableStatement = con.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                "`Order` (orderId CHAR(20),\n" +
                "    userId CHAR(50),\n" +
                "    restaurantId CHAR(50),\n" +
                "    courierId CHAR(50),\n" +
                "    deliveryTime int,\n" +
                "    state CHAR(20),\n" +
                "    totalCost int,\n" +
                "    PRIMARY KEY(userId, orderId));\n");
        createOrderTableStatement.executeUpdate();
        createOrderTableStatement.close();
        con.close();
    }

    @Override
    protected String getFindStatement() {
        return null;
    }

    @Override
    protected void fillFindValues(PreparedStatement st, ArrayList<String> ids) throws SQLException {

    }

    @Override
    protected String getInsertStatement() {
        return "INSERT INTO `Order`(orderId, userId, restaurantId, courierId, deliveryTime, state, totalCost)\n"+
                "VALUES(?,?,?,?,?,?,?);\n";
    }

    @Override
    protected void fillInsertValues(PreparedStatement st, Order data) throws SQLException {
        st.setString(1, data.getId());
        st.setString(2, data.getUserId());
        st.setString(3, data.getRestaurantId());
        st.setString(4, null);
        st.setInt(5, -1);
        st.setString(6, data.getState().toString());
        st.setInt(7, data.getTotalCost());
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
        return "SELECT O.orderId, O.userId, O.restaurantId, O.courierId, O.deliveryTime, O.state, O.totalCost, R.restaurantName FROM `Order` O, Restaurant R WHERE O.userId = ? and O.restaurantId = R.restaurantId;";
    }

    @Override
    protected void fillFindAllValues(PreparedStatement st, ArrayList<String> ids) throws SQLException {
        st.setString(1,ids.get(0));
    }

    @Override
    protected Order convertResultSetToDomainModel(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getString(1));
        order.setUserId(rs.getString(2));
        order.setRestaurantId(rs.getString(3));
        order.setRestaurantName(rs.getString(8));

        ArrayList<String> ids = new ArrayList<>();
//        ids.add(order.getRestaurantId());
//        Restaurant restaurant = RestaurantMapper.getInstance().find(ids);
//        if(restaurant != null) {
//            order.setRestaurantName(restaurant.getName());
//        }
        order.setCourierId(rs.getString(4));
        order.setDeliveryTime(rs.getInt(5));
        State state = rs.getString(6).equals("SearchingForCourier") ? State.SearchingForCourier :
                rs.getString(6).equals("CourierOnTheWay") ? State.CourierOnTheWay : State.Delievered;
        order.setState(state);
        order.setTotalCost(rs.getInt(7));

//        ids.clear();
////        ids.add(order.getUserId());
////        ids.add(order.getId());
////        List<OrderItem> orderItems = OrderFoodMapper.getInstance().findAll(ids);
////        Map<Food, Integer> foods = new HashMap<>();
////        for(OrderItem item : orderItems){
////            Food food = new Food();
////            food.setRestaurantId(order.getRestaurantId());
////            food.setName(item.getFoodName());
////            food.setPrice(item.getPrice());
////            foods.put(food, item.getNumber());
////        }
////        order.setFoods(foods);
        return order;
    }

    @Override
    protected ArrayList<Order> convertResultSetToDomainModelList(ResultSet rs) throws SQLException {
        ArrayList<Order> userOrders  = new ArrayList<>();
        while (rs.next()){
            userOrders.add(this.convertResultSetToDomainModel(rs));
        }
        System.out.println(userOrders.size());
        return userOrders;
    }

    //------------Independent functions-----------//

    protected String getUpdateCourierIdStatement() {
        return "UPDATE `Order`\n" +
                "SET courierId = ?\n" +
                "WHERE userId = ? AND orderId = ? \n";
    }
    protected String getUpdateDeliveryTimeStatement() {
        return "UPDATE `Order`\n" +
                "SET deliveryTime = ?\n" +
                "WHERE userId = ? AND orderId = ? \n";
    }
    protected String getUpdateStateStatement() {
        return "UPDATE `Order`\n" +
                "SET state = ?\n" +
                "WHERE userId = ? AND orderId = ? \n";
    }

    protected void fillUpdateCourierIdValues(PreparedStatement st, ArrayList<String> ids, String  updateValue) throws SQLException {
        st.setString(1, updateValue);
        st.setString(2,ids.get(0));
        st.setString(3,ids.get(1));
    }

    protected void fillUpdateDeliveryTimeValues(PreparedStatement st, ArrayList<String> ids, int updateValue) throws SQLException {
        st.setInt(1, updateValue);
        st.setString(2,ids.get(0));
        st.setString(3,ids.get(1));
    }

    protected void fillUpdateStateValues(PreparedStatement st, ArrayList<String> ids, String  updateValue) throws SQLException {
        st.setString(1, updateValue);
        st.setString(2,ids.get(0));
        st.setString(3,ids.get(1));
    }

    public void updateCourierId(ArrayList<String> ids, String  updateValue) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getUpdateCourierIdStatement());
        fillUpdateCourierIdValues(st, ids, updateValue);
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

    public void updateDeliveryTime(ArrayList<String> ids, int updateValue) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getUpdateDeliveryTimeStatement());
        fillUpdateDeliveryTimeValues(st, ids, updateValue);
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

    public void updateState(ArrayList<String> ids, String  updateValue) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getUpdateStateStatement());
        fillUpdateStateValues(st, ids, updateValue);
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
}
