package ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.restaurant;

import ir.ac.ut.ece.ie.loghme.repository.dataAccess.ConnectionPool;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.Mapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.food.FoodMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.food.IFoodMapper;
import ir.ac.ut.ece.ie.loghme.repository.model.Food;
import ir.ac.ut.ece.ie.loghme.repository.model.Location;
import ir.ac.ut.ece.ie.loghme.repository.model.Restaurant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RestaurantMapper extends Mapper<Restaurant, String> implements IRestaurantMapper {
    private static RestaurantMapper instance;

    public static RestaurantMapper getInstance() {
        if(instance == null){
            try {
                instance = new RestaurantMapper();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
        return instance;
    }

    private RestaurantMapper() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement createRestaurantTableStatement = con.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                "Restaurant (restaurantId CHAR(50),\n" +
                "    restaurantName CHAR(50),\n" +
                "    description CHAR(200),\n" +
                "    locationX int,\n" +
                "    locationY int,\n" +
                "    logo CHAR(200),\n" +
                "    PRIMARY KEY(restaurantId)\n);" );
        createRestaurantTableStatement.executeUpdate();
        createRestaurantTableStatement.close();
        con.close();
    }

    @Override
    protected String getFindStatement() {
        return "SELECT * FROM Restaurant restaurant WHERE restaurant.restaurantId = ?;";
    }

    @Override
    protected void fillFindValues(PreparedStatement st, ArrayList<String> ids) throws SQLException {
        System.out.println("in restaurant mapper, restaurantId : " + ids.get(0));
        st.setString(1, ids.get(0));
    }

    @Override
    protected String getInsertStatement() {
        return "INSERT INTO Restaurant(restaurantId, restaurantName, description, locationX, locationY, logo) VALUES(?,?,?,?,?,?)";
    }

    @Override
    protected void fillInsertValues(PreparedStatement st, Restaurant data) throws SQLException {
        st.setString(1, data.getId());
        st.setString(2, data.getName());
        st.setString(3, data.getDescription());
        st.setInt(4, data.getLocation().getX());
        st.setInt(5, data.getLocation().getY());
        st.setString(6, data.getLogo());
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
        return "SELECT* FROM Restaurant restaurant WHERE SQRT(POW(restaurant.locationX - ?, 2) + POW(restaurant.locationY - ?, 2)) <= 170 LIMIT ?;";
    }

    @Override
    protected void fillFindAllValues(PreparedStatement st, ArrayList<String> ids) throws SQLException {
        st.setInt(1, Integer.parseInt(ids.get(0)));
        st.setInt(2, Integer.parseInt(ids.get(1)));
        st.setInt(3, Integer.parseInt(ids.get(2)) * Integer.parseInt(ids.get(3)));
    }

    @Override
    protected Restaurant convertResultSetToDomainModel(ResultSet rs) throws SQLException {
        Restaurant restaurant = new Restaurant();
        Location location = new Location();
        restaurant.setId(rs.getString(1));
        restaurant.setName(rs.getString(2));
        restaurant.setDescription(rs.getString(3));
        location.setX(rs.getInt(4));
        location.setY(rs.getInt(5));
        restaurant.setLocation(location);
        restaurant.setLogo(rs.getString(6));
        return restaurant;
    }

    @Override
    protected ArrayList<Restaurant> convertResultSetToDomainModelList(ResultSet rs) throws SQLException {
        ArrayList<Restaurant> restaurants  = new ArrayList<>();
        while (rs.next()){
            restaurants.add(this.convertResultSetToDomainModel(rs));
        }
        return restaurants;
    }

    @Override
    public void delete(String id) throws SQLException {

    }

    protected String getSearchInRestaurantNameStatement() {
        return "SELECT* FROM Restaurant restaurant WHERE restaurant.restaurantName LIKE ? LIMIT ?;";
    }

    protected void fillSearchInRestaurantNameStatement(PreparedStatement st, String restaurantName, int count) throws SQLException {
        st.setString(1, "%"+restaurantName+"%");
        st.setInt(2, count);
    }

    public List<Restaurant> searchInRestaurantName(String restaurantName, int count) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getSearchInRestaurantNameStatement());
        fillSearchInRestaurantNameStatement(st, restaurantName, count);
        try {
            ResultSet resultSet = st.executeQuery();
            if (resultSet == null) {
                st.close();
                con.close();
                return new ArrayList<Restaurant>();
            }
            List<Restaurant> result = convertResultSetToDomainModelList(resultSet);
            st.close();
            con.close();
            return result;
        } catch (SQLException e) {
            System.out.println("error in restaurant Mapper.");
            st.close();
            con.close();
            throw e;
        }
    }

    protected String getSearchInFoodNameStatement() {
        return "SELECT* FROM Restaurant restaurant WHERE restaurant.restaurantId in ( SELECT food.restaurantId FROM Food food WHERE food.foodName LIKE ?) LIMIT ?;";
    }

    protected void fillSearchInFoodNameStatement(PreparedStatement st, String foodName, int count) throws SQLException {
        st.setString(1, "%" + foodName + "%");
        st.setInt(2, count);
    }

    public List<Restaurant> searchInFoodName(String foodName, int count) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getSearchInFoodNameStatement());
        fillSearchInFoodNameStatement(st, foodName, count);
        try {
            ResultSet resultSet = st.executeQuery();
            if (resultSet == null) {
                st.close();
                con.close();
                return new ArrayList<Restaurant>();
            }
            List<Restaurant> result = convertResultSetToDomainModelList(resultSet);
            st.close();
            con.close();
            return result;
        } catch (SQLException e) {
            System.out.println("error in foodMapper.search.");
            st.close();
            con.close();
            throw e;
        }
    }

    protected String getSearchInFoodAndRestaurantNameStatement() {
        return "SELECT* FROM Restaurant restaurant WHERE restaurant.restaurantName LIKE ? and restaurant.restaurantId in ( SELECT food.restaurantId FROM Food food WHERE food.foodName LIKE ?) LIMIT ?;";
    }

    protected void fillSearchInFoodAndRestaurantNameStatement(PreparedStatement st, String foodName, String restaurantName, int count) throws SQLException {
        st.setString(1, "%" + restaurantName + "%");
        st.setString(2, "%" + foodName + "%");
        st.setInt(3, count);
    }

    public List<Restaurant> searchInFoodAndRestaurantName(String foodName, String restaurantName, int count) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getSearchInFoodAndRestaurantNameStatement());
        fillSearchInFoodAndRestaurantNameStatement(st, foodName, restaurantName, count);
        try {
            ResultSet resultSet = st.executeQuery();
            if (resultSet == null) {
                st.close();
                con.close();
                return new ArrayList<Restaurant>();
            }
            List<Restaurant> result = convertResultSetToDomainModelList(resultSet);
            st.close();
            con.close();
            return result;
        } catch (SQLException e) {
            System.out.println("error in foodMapper.search.");
            st.close();
            con.close();
            throw e;
        }
    }
}
