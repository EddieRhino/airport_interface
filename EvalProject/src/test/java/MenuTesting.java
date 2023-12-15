//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions;
import java.sql.*;

//import static org.junit.Assert.*;


public class MenuTesting {
    private Menus menu;
    private Connection connection;


    @BeforeEach
    void setUp() throws SQLException {
        String dbHost = "localhost";
        String dbPort = "3306";
        String dbName = "FLIGHTS";
        String dbUsername = "root";
        String dbPassword = "9372$Couter";
        String jdbcUrl = "jdbc:mysql://%s:%s/%s?user=%s&password=%s"
                .formatted(dbHost,dbPort,dbName, dbUsername, dbPassword);
        connection = DriverManager.getConnection(jdbcUrl);
        if (connection.isValid(0)) {
            menu = new Menus();

        } else {
            System.out.println("Connection to the database is invalid.");
        }
    }
    @Test
    void TestAdminAddAndDeleteFlights() {
        int[] values = {1,0};
        int[] counts = {0,1};

        try {
            String query = "Insert into flightTest(flightID, origin, destination) values (?,?,?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1,1);
            statement.setString(2,"MSP");
            statement.setString(3,"LAX");
            statement.executeUpdate();

            String query2 = "SELECT COUNT(flightID) FROM flightTest";

            PreparedStatement statement2 = connection.prepareStatement(query2);
            ResultSet resultSet = statement2.executeQuery(query2);

            if (resultSet.next()) {
                counts[0] = resultSet.getInt(1);
            }
            String query3 = "DELETE from flightTest where flightID = ?";
            PreparedStatement statement3 = connection.prepareStatement(query3);
            statement3.setInt(1,1);
            statement3.executeUpdate();

            String query4 = "SELECT COUNT(flightID) FROM flightTest";

            PreparedStatement statement4 = connection.prepareStatement(query4);
            ResultSet resultSet2 = statement4.executeQuery(query4);


            if (resultSet2.next()) {
                counts[1] = resultSet2.getInt(1);
            }


        }
        catch(SQLException e){
            e.printStackTrace();
        }

        Assertions.assertArrayEquals(counts,values);
    }


    @Test
    void TestAdminUpdateFlights() {

        try {
            String query = "Insert into flightTest(flightID, origin, destination) values (?,?,?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1,1);
            statement.setString(2,"MSP");
            statement.setString(3,"LAX");
            statement.executeUpdate();

            String query2 = "Update flightTest set destination = ? where flightID = ?";
            PreparedStatement statement2 = connection.prepareStatement(query2);
            statement2.setInt(2,1);
            statement2.setString(1,"ATL");
            statement2.executeUpdate();

            String query3 = "select * from flightTest";
            PreparedStatement statement3 = connection.prepareStatement(query3);
            ResultSet rs = statement3.executeQuery(query3);
            String dest = "";
            if(rs.next()) {
                dest = rs.getString("destination");
            }
            Assertions.assertEquals(dest,"ATL");

            String query4 = "DELETE from flightTest where flightID = ?";
            PreparedStatement statement4 = connection.prepareStatement(query4);
            statement4.setInt(1,1);
            statement4.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }





    void tearDown(){
        menu = null;
    }

}
