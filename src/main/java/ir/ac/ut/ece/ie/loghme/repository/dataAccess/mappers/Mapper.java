package ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers;

import ir.ac.ut.ece.ie.loghme.repository.dataAccess.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Mapper<T, I> implements IMapper<T, I> {

    protected Map<I, T> loadedMap = new HashMap<I, T>();

    abstract protected String getFindStatement();
    abstract protected void fillFindValues(PreparedStatement st, ArrayList<I> ids) throws SQLException;
    abstract protected String getInsertStatement();
    abstract protected void fillInsertValues(PreparedStatement st, T data) throws SQLException;
    abstract protected String getDeleteStatement();
    abstract protected void fillDeleteValues(PreparedStatement st, ArrayList<I> ids) throws SQLException;
    abstract protected String getFindAllStatement();
    abstract protected void fillFindAllValues(PreparedStatement st, ArrayList<I> ids) throws SQLException;
    abstract protected T convertResultSetToDomainModel(ResultSet rs) throws SQLException;
    abstract protected ArrayList<T> convertResultSetToDomainModelList(ResultSet rs) throws SQLException;


    public T find(ArrayList<I> ids) throws SQLException {

        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getFindStatement());
        fillFindValues(st, ids);
        try {
            ResultSet resultSet = st.executeQuery();
            if (!resultSet.next() || resultSet == null) {
                st.close();
                con.close();
                return null;
            }
            T result = convertResultSetToDomainModel(resultSet);
            st.close();
            con.close();
            return result;
        } catch (SQLException ex) {
            System.out.println("error in Mapper.findByID query.");
            ex.printStackTrace();
            st.close();
            con.close();
            throw ex;
        }
    }

    public void insert(T obj) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getInsertStatement());
        fillInsertValues(st, obj);
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

    public void delete(ArrayList<I> ids) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getDeleteStatement());
        fillDeleteValues(st, ids);
        try {
            st.executeUpdate();
            st.close();
            con.close();
        } catch (SQLException ex) {
            st.close();
            con.close();
            System.out.println("error in Mapper.delete query.");
        }
    }

    public List<T> findAll(ArrayList<I> ids) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getFindAllStatement());
        fillFindAllValues(st, ids);
        try {
            ResultSet resultSet = st.executeQuery();
            if (resultSet == null) {
                st.close();
                con.close();
                return new ArrayList<T>();
            }
            List<T> result = convertResultSetToDomainModelList(resultSet);
            st.close();
            con.close();
            return result;
        } catch (SQLException e) {
            System.out.println("error in Mapper.findAll query.");
            e.printStackTrace();
            st.close();
            con.close();
            throw e;
        }
    }
}