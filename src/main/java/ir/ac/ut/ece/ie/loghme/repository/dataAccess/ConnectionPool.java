package ir.ac.ut.ece.ie.loghme.repository.dataAccess;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp.BasicDataSource;

public class ConnectionPool {
    private static BasicDataSource ds = new BasicDataSource();
    private final static String dbURL = "jdbc:mysql://loghme-db:3306/LoghmeDB?useUnicode=yes&characterEncoding=UTF-8";

    //for kimia : jdbc:mysql://localhost:3306/loghme
    //for zahra : jdbc:mysql://localhost:3306/LoghmeDb
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        ds.setUsername("root");
        // for kimia : zahrakimia
        // for zahra : kimia zahra
        ds.setPassword("root");
        ds.setUrl(dbURL);
        ds.setMinIdle(5);
        ds.setMaxIdle(10);
        ds.setMaxOpenPreparedStatements(100);
        setEncoding();
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static void setEncoding(){
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            statement.execute("ALTER DATABASE LoghmeDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
            connection.close();
            statement.close();
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

}

