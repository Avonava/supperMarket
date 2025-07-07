package online_shopp;

import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/online_shopp"; // change DB name if needed
    private static final String USER = "root";  // your XAMPP MySQL username
    private static final String PASSWORD = "";  // your XAMPP MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
