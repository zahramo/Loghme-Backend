package ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface IMapper<T, I> {
    T find(ArrayList<I> ids) throws SQLException;
    void insert(T t) throws SQLException;
    void delete(ArrayList<I> ids) throws SQLException;
    List<T> findAll(ArrayList<I> ids) throws SQLException;
}