package ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.cart;

import ir.ac.ut.ece.ie.loghme.repository.dataAccess.ConnectionPool;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.Mapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CartItemMapper extends Mapper<CartItem, String> {
    private static CartItemMapper instance;

    public static CartItemMapper getInstance() {
        if(instance == null){
            try {
                instance = new CartItemMapper();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return instance;
    }

    private CartItemMapper() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement createCartItemTableStatement = con.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                "   CartItem ( userId CHAR(50),\n" +
                "   restaurantId CHAR(50),\n" +
                "   foodName CHAR(100),\n" +
                "   num int,\n" +
                "   price int,\n"+
                "   PRIMARY KEY(userId, restaurantId, foodName),\n" +
                "   FOREIGN KEY(userId)\n" +
                "   REFERENCES User (email)\n ON DELETE CASCADE,\n" +
                "   FOREIGN KEY(restaurantId, foodName)\n" +
                "   REFERENCES Food (restaurantId, foodName)\n ON DELETE CASCADE);");
        createCartItemTableStatement.executeUpdate();
        createCartItemTableStatement.close();
        con.close();
    }

    @Override
    protected String getInsertStatement() {
        return "INSERT INTO CartItem(userId, restaurantId, foodName, num, price) VALUES(?,?,?,?,?)";
    }

    @Override
    protected void fillInsertValues(PreparedStatement st, CartItem data) throws SQLException {
        st.setString(1,data.getUserId());
        st.setString(2,data.getRestaurantId());
        st.setString(3,data.getFoodName());
        st.setInt(4,data.getNumber());
        st.setInt(5,data.getPrice());
    }

    @Override
    protected String getFindStatement() {
        return null;
    }

    @Override
    protected void fillFindValues(PreparedStatement st, ArrayList<String> ids) throws SQLException {

    }

    @Override
    protected String getDeleteStatement() {
        return "DELETE FROM CartItem WHERE userId = ? and foodName = ?;\n";
    }

    @Override
    protected void fillDeleteValues(PreparedStatement st, ArrayList<String> ids) throws SQLException {
        st.setString(1,ids.get(0));
        st.setString(2,ids.get(1));
    }

    @Override
    protected String getFindAllStatement() {
        return "SELECT * FROM CartItem C WHERE C.userId = ? ;";
    }

    @Override
    protected void fillFindAllValues(PreparedStatement st, ArrayList<String> ids) throws SQLException {
        st.setString(1,ids.get(0));
    }

    protected String getUpdateStatement(){
        return "UPDATE CartItem SET num = ? , price = ? WHERE userId = ? AND restaurantId = ? And foodName = ?\n";
    }

    protected void fillUpdateValues(PreparedStatement st, CartItem data) throws SQLException {
        st.setInt(1, data.getNumber());
        st.setInt(2,data.getPrice());
        st.setString(3,data.getUserId());
        st.setString(4,data.getRestaurantId());
        st.setString(5,data.getFoodName());
    }

    public void update(CartItem data) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getUpdateStatement());
        fillUpdateValues(st, data);
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

    @Override
    protected CartItem convertResultSetToDomainModel(ResultSet rs) throws SQLException {
        CartItem cartItem = new CartItem();
        cartItem.setUserId(rs.getString(1));
        cartItem.setRestaurantId(rs.getString(2));
        cartItem.setFoodName(rs.getString(3));
        cartItem.setNumber(rs.getInt(4));
        cartItem.setPrice(rs.getInt(5));
        return cartItem;
    }

    @Override
    protected ArrayList<CartItem> convertResultSetToDomainModelList(ResultSet rs) throws SQLException {
        ArrayList<CartItem> cartItems  = new ArrayList<>();
        while (rs.next()){
            cartItems.add(this.convertResultSetToDomainModel(rs));
        }
        return cartItems;
    }

    protected String getDeleteAllStatement() {
        return "DELETE FROM CartItem WHERE userId = ?;\n";
    }

    protected void fillDeleteAllValues(PreparedStatement st, ArrayList<String> ids) throws SQLException {
        st.setString(1,ids.get(0));
    }

    public void deleteAll(ArrayList<String> ids) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getDeleteAllStatement());
        fillDeleteAllValues(st, ids);
        try {
            st.executeUpdate();
            st.close();
            con.close();
        } catch (SQLException ex) {
            st.close();
            con.close();
            System.out.println("error in cartItemMapper.delete all query.");
        }
    }
}
