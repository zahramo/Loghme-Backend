package ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.order;

import ir.ac.ut.ece.ie.loghme.repository.dataAccess.ConnectionPool;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.Mapper;
import ir.ac.ut.ece.ie.loghme.repository.model.Courier;
import ir.ac.ut.ece.ie.loghme.repository.model.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CourierMapper extends Mapper<Courier, String> {

    private static CourierMapper instance;

    public static CourierMapper getInstance() {
        if(instance == null){
            try {
                instance = new CourierMapper();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
        return instance;
    }

    private CourierMapper() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement createOrderFoodTableStatement = con.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                "Courier (courierId TEXT(500)\n, velocity int, locationX int, locationY int)");
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
        return "INSERT INTO Courier(courierId, velocity, locationX, locationY) VALUES(?,?,?,?)";
    }

    @Override
    protected void fillInsertValues(PreparedStatement st, Courier data) throws SQLException {
        st.setString(1,data.getId());
        st.setInt(2,data.getVelocity());
        Location location = data.getLocation();
        st.setInt(3,location.getX());
        st.setInt(4,location.getY());
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
        return null;
    }

    @Override
    protected void fillFindAllValues(PreparedStatement st, ArrayList<String> ids) throws SQLException {

    }

    @Override
    protected Courier convertResultSetToDomainModel(ResultSet rs) throws SQLException {
        return null;
    }

    @Override
    protected ArrayList<Courier> convertResultSetToDomainModelList(ResultSet rs) throws SQLException {
        return null;
    }
}
