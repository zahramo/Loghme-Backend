package ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.restaurant;

import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.IMapper;
import ir.ac.ut.ece.ie.loghme.repository.model.Food;
import ir.ac.ut.ece.ie.loghme.repository.model.Restaurant;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface IRestaurantMapper extends IMapper<Restaurant, String> {
    void insert(Restaurant restaurant) throws SQLException;
    Restaurant find(ArrayList<String> ids) throws SQLException;
    void delete(String id) throws SQLException;
    List<Restaurant> findAll(ArrayList<String> ids) throws SQLException;
}
