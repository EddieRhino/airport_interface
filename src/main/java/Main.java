import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args){
        String dbHost = "localhost";
        String dbPort = "3306";
        String dbName = "FLIGHTS";
        String dbUsername = "root";
        String dbPassword = "9372$Couter";
        String jdbcUrl = "jdbc:mysql://%s:%s/%s?user=%s&password=%s"
                .formatted(dbHost,dbPort,dbName, dbUsername, dbPassword);
        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
            if (connection.isValid(0)) {
                Menus menu = new Menus();
                menu.setConnection(connection);

                menu.MainMenu();

            } else {
                System.out.println("Connection to the database is invalid.");
            }
        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
        }



    }
}
