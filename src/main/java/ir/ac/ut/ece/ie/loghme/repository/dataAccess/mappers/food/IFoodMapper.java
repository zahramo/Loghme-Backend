package ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.food;

import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.IMapper;
import ir.ac.ut.ece.ie.loghme.repository.model.Food;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface IFoodMapper extends IMapper<Food, String> {
    void insert(Food food) throws SQLException;
    Food find(ArrayList<String> ids) throws SQLException;
    void delete(String id) throws SQLException;
    List<Food> findAll(ArrayList<String> ids) throws SQLException;
}
