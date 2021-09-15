package ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.order;

import ir.ac.ut.ece.ie.loghme.repository.dataAccess.ConnectionPool;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.Mapper;
import ir.ac.ut.ece.ie.loghme.repository.model.Order;
import ir.ac.ut.ece.ie.loghme.repository.model.State;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderFoodMapper extends Mapper<OrderItem, String> {
    private static OrderFoodMapper instance;

    public static OrderFoodMapper getInstance() {
        if(instance == null){
            try {
                instance = new OrderFoodMapper();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
        return instance;
    }

    private OrderFoodMapper() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement createOrderFoodTableStatement = con.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                "   OrderFood (orderId CHAR(20),\n" +
                "   userId CHAR(50),\n"+
                "   restaurantId CHAR(50),\n" +
                "   foodName CHAR(100),\n" +
                "   num int,\n" +
                "   price int,\n"+
                "   PRIMARY KEY(userId, orderId, restaurantId, foodName),\n" +
                "   FOREIGN KEY(userId, orderId)\n" +
                "   REFERENCES `Order` (userId, orderId)\n ON DELETE CASCADE,\n" +
                "   FOREIGN KEY(restaurantId, foodName)\n" +
                "   REFERENCES Food (restaurantId, foodName)\n ON DELETE CASCADE);");
        createOrderFoodTableStatement.executeUpdate();
        createOrderFoodTableStatement.close();
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
        return "INSERT INTO OrderFood(userId, orderId, restaurantId, foodName, num, price) VALUES(?,?,?,?,?,?)";
    }

    @Override
    protected void fillInsertValues(PreparedStatement st, OrderItem data) throws SQLException {
        st.setString(1,data.getUserId());
        st.setString(2,data.getOrderId());
        st.setString(3,data.getRestaurantId());
        st.setString(4,data.getFoodName());
        st.setInt(5,data.getNumber());
        st.setInt(6,data.getPrice());
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
        return "SELECT * FROM OrderFood orderFood WHERE orderFood.userId = ? AND orderFood.orderId = ? ;";
    }

    @Override
    protected void fillFindAllValues(PreparedStatement st, ArrayList<String> ids) throws SQLException {
        st.setString(1,ids.get(0));
        st.setString(2,ids.get(1));
    }

    @Override
    protected OrderItem convertResultSetToDomainModel(ResultSet rs) throws SQLException {
        System.out.println("Create Order Item ... ");
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(rs.getString(1));
        orderItem.setUserId(rs.getString(2));
        orderItem.setRestaurantId(rs.getString(3));
        orderItem.setFoodName(rs.getString(4));
        orderItem.setNumber(rs.getInt(5));
        orderItem.setPrice(rs.getInt(6));
        System.out.println("returning Order Item ... ");
        return orderItem;
    }

    @Override
    protected ArrayList<OrderItem> convertResultSetToDomainModelList(ResultSet rs) throws SQLException {
        ArrayList<OrderItem> orderItems  = new ArrayList<>();
        while (rs.next()){
            orderItems.add(this.convertResultSetToDomainModel(rs));
        }
        return orderItems;
    }
}
