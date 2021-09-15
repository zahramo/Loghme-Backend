package ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.cart;

import ir.ac.ut.ece.ie.loghme.repository.dataAccess.ConnectionPool;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.Mapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CartMapper extends Mapper<ArrayList<String>, String> {
    private static CartMapper instance;

    public static CartMapper getInstance() {
        if(instance == null){
            try {
                instance = new CartMapper();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return instance;
    }

    private CartMapper() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement createCartTableStatement = con.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                "   Cart ( userId CHAR(50),\n" +
                "   restaurantId CHAR(50),\n" +
                "   PRIMARY KEY(userId),\n" +
                "   FOREIGN KEY(userId)\n" +
                "   REFERENCES `User` (email) ON DELETE CASCADE);\n");
        createCartTableStatement.executeUpdate();
        createCartTableStatement.close();
        con.close();
    }

    @Override
    protected String getInsertStatement() {
        return "INSERT INTO Cart(userId, restaurantId) VALUES(?,?)";
    }

    @Override
    protected void fillInsertValues(PreparedStatement st, ArrayList<String> data) throws SQLException {
        st.setString(1, data.get(0));
        st.setString(2, data.get(1));
    }

    @Override
    protected String getFindStatement() {
        return "SELECT * FROM Cart C WHERE C.userId = ? ;";
    }

    @Override
    protected void fillFindValues(PreparedStatement st, ArrayList<String> ids) throws SQLException {
        st.setString(1,ids.get(0));
    }

    @Override
    protected String getDeleteStatement() {
        return "DELETE FROM Cart WHERE userId = ?;\n";
    }

    @Override
    protected void fillDeleteValues(PreparedStatement st, ArrayList<String> ids) throws SQLException {
        st.setString(1,ids.get(0));
    }

    protected String getUpdateStatement(){
        return "UPDATE Cart SET restaurantId = ? WHERE userId = ?;\n";
    }

    protected void fillUpdateValues(PreparedStatement st, ArrayList<String> data) throws SQLException {

        st.setString(2, data.get(0));
        st.setString(1, data.get(1));
    }

    public void update(ArrayList<String> data) throws SQLException {
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
    protected String getFindAllStatement() {
        return null;
    }

    @Override
    protected void fillFindAllValues(PreparedStatement st, ArrayList<String> ids) throws SQLException {
    }

    @Override
    protected ArrayList<String> convertResultSetToDomainModel(ResultSet rs) throws SQLException {
        ArrayList<String> result = new ArrayList<String>();
        result.add(rs.getString(1));
        result.add(rs.getString(2));
        return result;
    }

    @Override
    protected ArrayList<ArrayList<String>> convertResultSetToDomainModelList(ResultSet rs) throws SQLException {
        return null;
    }

}
