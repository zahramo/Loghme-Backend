package ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.food;

import ir.ac.ut.ece.ie.loghme.repository.dataAccess.ConnectionPool;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.Mapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.foodParty.FoodPartyMapper;
import ir.ac.ut.ece.ie.loghme.repository.model.Food;
import ir.ac.ut.ece.ie.loghme.repository.model.FoodUnderSale;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FoodMapper extends Mapper<Food, String> implements IFoodMapper {
    private static FoodMapper instance;

    public static FoodMapper getInstance() {
        if(instance == null){
            try {
                instance = new FoodMapper();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
        return instance;
    }

    private FoodMapper() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement createFoodTableStatement = con.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                "Food (restaurantId CHAR(50),\n" +
                "    foodName CHAR(225),\n" +
                "    description TEXT(500),\n" +
                "    popularity int,\n" +
                "    price int,\n" +
                "    image TEXT(500),\n" +
                "    underSale int,\n" +
                "    PRIMARY KEY(restaurantId, foodName),\n" +
                "    FOREIGN KEY(restaurantId)\n" +
                "    REFERENCES Restaurant (restaurantId) \n ON DELETE CASCADE);"
        );
        createFoodTableStatement.executeUpdate();
        createFoodTableStatement.close();
        con.close();
    }


    @Override
    protected String getFindStatement() {
        return "SELECT* FROM Food food WHERE food.restaurantId = ? and food.foodName = ?;";
    }

    @Override
    protected void fillFindValues(PreparedStatement st, ArrayList<String> ids) throws SQLException {
        st.setString(1, ids.get(0));
        st.setString(2, ids.get(1));
    }

    @Override
    protected String getInsertStatement() {
        return "INSERT INTO Food(restaurantId, foodName, description, popularity, price, image, underSale) VALUES(?,?,?,?,?,?,?)";
    }

    @Override
    protected void fillInsertValues(PreparedStatement st, Food data) throws SQLException {
        st.setString(1, data.getRestaurantId());
        st.setString(2, data.getName());
        st.setString(3, data.getDescription());
        st.setInt(4, data.getPopularity());
        st.setInt(5, data.getPrice());
        st.setString(6, data.getImage());
        if(data instanceof FoodUnderSale){
            st.setInt(7, 1);
        }else{
            st.setInt(7, 0);
        }
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
        return "SELECT * FROM Food food WHERE food.restaurantId = ?;";
    }

    @Override
    protected void fillFindAllValues(PreparedStatement st, ArrayList<String> ids) throws SQLException {
        st.setString(1, ids.get(0));
    }

    @Override
    protected Food convertResultSetToDomainModel(ResultSet rs) throws SQLException {
        Food food = new Food();
        food.setRestaurantId(rs.getString(1));
        food.setName(rs.getString(2));
        food.setDescription(rs.getString(3));
        food.setPopularity(rs.getInt(4));
        food.setPrice(rs.getInt(5));
        food.setImage(rs.getString(6));
        return food;
    }

    @Override
    protected ArrayList<Food> convertResultSetToDomainModelList(ResultSet rs) throws SQLException {
        ArrayList<Food> foods  = new ArrayList<>();
        while (rs.next()){
            foods.add(this.convertResultSetToDomainModel(rs));
        }
        return foods;
    }

    protected String getUpdateAllUnderSaleStatement(){
        return "UPDATE Food food SET food.underSale = ? WHERE food.underSale = ?;\n";
    }

    protected void fillUpdateAllUnderSaleStatement(PreparedStatement st) throws SQLException {
        st.setInt(1, 0);
        st.setInt(2, 1);
    }

    public void updateAllUnderSale() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getUpdateAllUnderSaleStatement());
        fillUpdateAllUnderSaleStatement(st);
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

    protected String getFindAllFoodPartyStatement() {
        return "SELECT* FROM Food food WHERE food.underSale = ?;";
    }

    protected void fillFindAllFoodPartyStatement(PreparedStatement st) throws SQLException {
        st.setInt(1, 1);
    }

    public List<Food> findAllFoodParty() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getFindAllFoodPartyStatement());
        fillFindAllFoodPartyStatement(st);
        try {
            ResultSet resultSet = st.executeQuery();
            if (resultSet == null) {
                st.close();
                con.close();
                return new ArrayList<Food>();
            }
            List<Food> result = convertResultSetToDomainModelList(resultSet);
            st.close();
            con.close();
            return result;
        } catch (SQLException e) {
            System.out.println("error in foodMapper.findAll foodparty query.");
            st.close();
            con.close();
            throw e;
        }
    }

    @Override
    public void delete(String id) throws SQLException {

    }


    protected String getUpdateUnderSaleStatement(){
        return "UPDATE Food food SET food.underSale = ? WHERE food.restaurantId = ? and food.foodName = ? ; \n";
    }

    protected void fillUpdateUnderSaleStatement(PreparedStatement st, Food food) throws SQLException {
        st.setInt(1, 1);
        st.setString(2, food.getRestaurantId());
        st.setString(3, food.getName());
    }

    public void updateUnderSale(Food food) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getUpdateUnderSaleStatement());
        fillUpdateUnderSaleStatement(st, food);
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
