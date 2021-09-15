package ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.foodParty;

import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.IMapper;
import ir.ac.ut.ece.ie.loghme.repository.model.FoodUnderSale;
import ir.ac.ut.ece.ie.loghme.repository.model.Restaurant;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface IFoodPartyMapper extends IMapper<FoodUnderSale, String> {
    void insert(FoodUnderSale foodUnderSale) throws SQLException;
    FoodUnderSale find(ArrayList<String> ids) throws SQLException;
    void delete(String id) throws SQLException;
    List<FoodUnderSale> findAll(ArrayList<String> ids) throws SQLException;
}
