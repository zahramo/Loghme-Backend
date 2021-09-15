package ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.foodParty;

import ir.ac.ut.ece.ie.loghme.repository.dataAccess.ConnectionPool;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.Mapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.food.FoodMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.food.IFoodMapper;
import ir.ac.ut.ece.ie.loghme.repository.model.Food;
import ir.ac.ut.ece.ie.loghme.repository.model.FoodUnderSale;
import ir.ac.ut.ece.ie.loghme.repository.model.Restaurant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class FoodPartyMapper extends Mapper<FoodUnderSale, String> implements IFoodPartyMapper {
    private static FoodPartyMapper instance;

    public static FoodPartyMapper getInstance() {
        if(instance == null){
            try {
                instance = new FoodPartyMapper();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
        return instance;
    }

    private FoodPartyMapper() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement createFoodPartyTableStatement = con.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                "FoodParty (restaurantId CHAR(50),\n" +
                "    foodName CHAR(100),\n" +
                "    foodCount int,\n" +
                "    price int,\n" +
                "    valid int,\n" +
                "    PRIMARY KEY(restaurantId, foodName),\n" +
                "    FOREIGN KEY(restaurantId, foodName)\n" +
                "    REFERENCES Food (restaurantId, foodName) \n ON DELETE CASCADE);"
        );
        createFoodPartyTableStatement.executeUpdate();
        createFoodPartyTableStatement.close();
        con.close();
    }

    @Override
    protected String getFindStatement() {
        return "SELECT * FROM FoodParty foodParty WHERE foodParty.restaurantId = ? and foodParty.foodName = ?;";
    }

    @Override
    protected void fillFindValues(PreparedStatement st, ArrayList<String> ids) throws SQLException {
        st.setString(1, ids.get(0));
        st.setString(2, ids.get(1));
    }

    @Override
    protected String getInsertStatement() {
        return "INSERT INTO FoodParty(restaurantId, foodName, foodCount, price, valid) VALUES(?,?,?,?,?)";
    }

    @Override
    protected void fillInsertValues(PreparedStatement st, FoodUnderSale data) throws SQLException {
        st.setString(1, data.getRestaurantId());
        st.setString(2, data.getName());
        st.setInt(3, data.getCount());
        st.setInt(4, data.getPrice());
        st.setInt(5, 1);
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
        return "SELECT * FROM FoodParty foodParty WHERE foodParty.valid = ?;";
    }

    @Override
    protected void fillFindAllValues(PreparedStatement st, ArrayList<String> ids) throws SQLException {
        st.setInt(1, 1);
    }

    @Override
    protected FoodUnderSale convertResultSetToDomainModel(ResultSet rs) throws SQLException {
        FoodUnderSale food = new FoodUnderSale();
        food.setRestaurantId(rs.getString(1));
        food.setName(rs.getString(2));
        food.setCount(rs.getInt(3));
        food.setPrice(rs.getInt(4));
        food.setValid(rs.getInt(5));
        return food;
    }

    @Override
    protected ArrayList<FoodUnderSale> convertResultSetToDomainModelList(ResultSet rs) throws SQLException {
        ArrayList<FoodUnderSale> foods  = new ArrayList<>();
        while (rs.next()){
            foods.add(this.convertResultSetToDomainModel(rs));
        }
        return foods;
    }

    protected String getUpdateAllValidationsStatement(){
        return "UPDATE FoodParty foodParty SET foodParty.valid = ? WHERE foodParty.valid = ?;\n";
    }

    protected void fillUpdateAllValidationsStatement(PreparedStatement st, int updateValue) throws SQLException {
        st.setInt(1, updateValue);
        st.setInt(2, 1);
    }

    public void updateAllValidations(int updateValue) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getUpdateAllValidationsStatement());
        fillUpdateAllValidationsStatement(st, updateValue);
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

    protected String getUpdateStatement(){
        return "UPDATE FoodParty foodParty SET foodParty.foodCount = ? , foodParty.valid = ? , foodParty.price = ?\n " +
                "WHERE foodParty.restaurantId = ? and foodParty.foodName = ? ; \n";
    }

    protected void fillUpdateStatement(PreparedStatement st, FoodUnderSale foodUnderSale) throws SQLException {
        st.setInt(1, foodUnderSale.getCount());
        st.setInt(2, 1);
        st.setInt(3, foodUnderSale.getPrice());
        st.setString(4, foodUnderSale.getRestaurantId());
        st.setString(5, foodUnderSale.getName());
    }

    public void update(FoodUnderSale foodUnderSale) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getUpdateStatement());
        fillUpdateStatement(st, foodUnderSale);
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
    public void delete(String id) throws SQLException {

    }
}
