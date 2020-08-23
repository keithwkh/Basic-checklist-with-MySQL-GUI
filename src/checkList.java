import java.sql.*;

public class checkList {
    private static String connectionUrl;
    private static Connection con;

    public static Connection connectSQL(String username, String password) throws SQLException {
        connectionUrl = "jdbc:mysql://localhost:3306/checklist?serverTimezone=GMT%2B8";
        con = DriverManager.getConnection(connectionUrl, username, password);
        return con;
    }

    public static void main(String[] args) throws SQLException {
        new loginFrame();
    }
}
