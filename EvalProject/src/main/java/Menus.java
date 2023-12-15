import javax.xml.transform.Result;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.*;
import java.util.ArrayList;
public class Menus {
    private Scanner scanner;
    private Connection connection;

    public Menus() {
        scanner = new Scanner(System.in);
    }

    public void setConnection(Connection connection){
        this.connection = connection;
    }

    public void MainMenu() {
        System.out.println("Welcome to the Utopia Airlines Management System. Which category of user are you?" +
                "\n\n\t1) Employee/Agent\n\t2) Traveler\n\t3) Administrator\n\t4) Exit");
        String input = scanner.nextLine();
        if (input.equals("1")) EmployeeAgentMenu();
        else if (input.equals("2")) TravelerMenu();
        else if (input.equals("3")) AdminMenu();
        else if(input.equals("4")) System.out.println("Thank you for visiting Utopia Airlines");
        else {
            System.out.println("Invalid Response, Please try again.");
            MainMenu();
        }
    }

    public void EmployeeAgentMenu() {
        System.out.println("\nEMPLOYEE/AGENT MENU");
        System.out.println("\t1) View the Flights you manage\n\t2) Quit to the previous menu");
        String input = scanner.nextLine();
        if (input.equals("1")) {
            try {
                ViewFlights();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if (input.equals("2")) MainMenu();
        else {
            System.out.println("Invalid Response, Please try again.");
            EmployeeAgentMenu();
        }
    }

    public void TravelerMenu() {
        System.out.println("\nTRAVELER MENU");
        System.out.println("\t1) Enter Membership Number\n\t2) Quit to the previous menu");
        String input = scanner.nextLine();
        if (input.equals("1")) {
            while (true) {
                System.out.print("Enter Membership Number: ");
                String numStr = scanner.nextLine();
                try {
                    int num = Integer.parseInt(numStr);
                    String query = "Select * from member where user_id = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, num);
                    ResultSet rs = preparedStatement.executeQuery();
                    if (rs.next()) {
                        String name = rs.getString("name");

                        int mem_number = rs.getInt("user_id");
                        Trav1(mem_number, name);
                        break;
                    } else {
                        System.out.println("Invalid membership number");
                    }
                } catch (NumberFormatException | SQLException n) {
                    System.out.println("Invalid membership number");
                    TravelerMenu();
                }


            }

        } else if (input.equals("2")) MainMenu();
        else {
            System.out.println("Invalid Response, Please try again.");
            TravelerMenu();
        }
    }

    public void AdminMenu() {
        System.out.println("\nADMINISTRATOR MENU");
        System.out.println("\t1) Add/Update/Delete/Read Flights\n\t2) Add/Update/Delete/Read Seats\n\t" +
                "3) Add/Update/Delete/Read Bookings and Passengers\n\t4) Add/Update/Delete/Read Airports\n\t5) Add/Update/Delete/Read Travelers\n\t" +
                "6) Add/Update/Delete/Read Employees\n\t7) Quit to the previous menu");
        String input = scanner.nextLine();
        if (input.equals("1")) adminCRUD("flight", "Flight");
        else if (input.equals("2")) adminCRUD("flight_seats", "Seat");
        else if (input.equals("3")) adminCRUD("flight_bookings", "Bookings and Passengers");
        else if (input.equals("4")) adminCRUD("airports", "Airports");
        else if (input.equals("5")) adminCRUD("member", "Travelers");
        else if (input.equals("6")) adminCRUD("employees", "Employee");
        else if (input.equals("7")) MainMenu();

        else {
            System.out.println("Invalid Response, Please try again.");
            AdminMenu();
        }
    }

    public void ViewFlights() throws SQLException {
        System.out.println("EMP1");
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM flight");
        int counter = 1;
        ArrayList<String[]> locations = new ArrayList<String[]>();
        while (rs.next()) {
            String origin = rs.getString("origin");
            String dest = rs.getString("destination");
            String ID = String.valueOf(rs.getInt("flightID"));
            System.out.println("\t" + counter + ") " + origin + " --> " + dest);
            String[] temp = new String[3];
            temp[0] = ID;
            temp[1] = origin;
            temp[2] = dest;
            locations.add(temp);
            counter++;
        }
        System.out.println("\t" + counter + ") Quit to previous menu");
        String choice = scanner.nextLine();
        try {
            int choiceNum = Integer.parseInt(choice);
            if (choiceNum == counter) {
                EmployeeAgentMenu();
            } else {
                EMP2((locations.get(choiceNum - 1)[0]), (locations.get(choiceNum - 1)[1]), (locations.get(choiceNum - 1)[2]));
            }
        } catch (NumberFormatException n) {
            System.out.println("Please enter an integer");
            ViewFlights();
        }
    }

    public void EMP2(String ID, String origin, String dest) throws SQLException {
        System.out.println("EMP2");
        System.out.println("\t1) View more details about the Flight");
        System.out.println("\t2) Update the details of the Flight");
        System.out.println("\t3) Add Seats to the Flight");
        System.out.println("\t4) Quit to previous menu");
        String choice = scanner.nextLine();
        if (choice.equals("1")) {
            System.out.println("You have chosen to view the Flight with Flight Id: " +
                    ID + ", Departure Airport: " + origin + " and Arrival Airport: " + dest);
            int idNum = Integer.parseInt(ID);
            String query = "SELECT * FROM flight JOIN flight_seats USING (flightID) WHERE flightID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, idNum);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    String departureDate = rs.getString("departure_date");
                    String departureTime = rs.getString("departure_time");
                    String arrivalDate = rs.getString("arrival_date");
                    String arrivalTime = rs.getString("arrival_time");

                    int seatsAvailableFC = rs.getInt("seats_available_fc");
                    int seatsAvailableBC = rs.getInt("seats_available_bc");
                    int seatsAvailableEC = rs.getInt("seats_available_ec");

                    System.out.println("\nDeparture Airport: " + origin + " | Arrival Airport: " + dest +
                            " | \nDeparture Date: " + departureDate + " | Departure Time: " + departureTime +
                            " | \nArrival Date: " + arrivalDate + " | Arrival Time: " + arrivalTime);
                    System.out.println("\nAvailable Seats by Class:");
                    System.out.println("1) First --> " + seatsAvailableFC);
                    System.out.println("2) Business --> " + seatsAvailableBC);
                    System.out.println("3) Economy --> " + seatsAvailableEC);
                    System.out.println("4) Quit to previous menu");

                    String input = scanner.nextLine();

                    EMP2(ID, origin, dest);
                } else {
                    System.out.println("Incorrect ID Number");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (choice.equals("2")) {
            System.out.println("You've chosen to update the Flight with Flight ID: " + ID
                    + ", Flight Origin: " + origin + " and Flight Destination: " + dest);
            updateFlight(ID);
            EMP2(ID, origin, dest);
        } else if (choice.equals("3")) {
            System.out.println("Pick the Seat Class you want to add seats of, to your flight:");
            System.out.println("1) First");
            System.out.println("2) Business");
            System.out.println("3) Economy");
            System.out.println("4) Quit to cancel operation and return to previous menu");

            String input = scanner.nextLine();
            int idNum = Integer.parseInt(ID);

            if (input.equals("1")) {
                String query = "SELECT * FROM flight_seats where flightID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, idNum);
                    ResultSet rs = preparedStatement.executeQuery();
                    if (rs.next()) {
                        String fc_seats = rs.getString("seats_available_fc");
                        System.out.println("Existing number of seats: " + fc_seats);
                    } else {
                        System.out.println("Incorrect ID Number");
                    }
                    System.out.println("Enter new number of seats: ");
                    int seat_input = scanner.nextInt();
                    String query2 = "UPDATE flight_seats SET seats_available_fc = ? where flightID = ?";

                    PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
                    preparedStatement.setInt(1, seat_input);
                    preparedStatement.setInt(2, idNum);

                    preparedStatement2.executeUpdate();

                    EMP2(ID, origin, dest);

                } catch (SQLException | InputMismatchException e) {
                    e.printStackTrace();
                }

            } else if (input.equals("2")) {
                String query = "SELECT * FROM flight_seats where flightID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, idNum);
                    ResultSet rs = preparedStatement.executeQuery();
                    if (rs.next()) {
                        String bc_seats = rs.getString("seats_available_bc");
                        System.out.println("Existing number of seats: " + bc_seats);
                    } else {
                        System.out.println("Incorrect ID Number");
                    }
                    System.out.println("Enter new number of seats: ");
                    int seat_input = scanner.nextInt();
                    String query2 = "UPDATE flight_seats SET seats_available_bc = ? where flightID = ?";

                    PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
                    preparedStatement.setInt(1, seat_input);
                    preparedStatement.setInt(2, idNum);

                    preparedStatement2.executeUpdate();

                    EMP2(ID, origin, dest);

                } catch (SQLException | InputMismatchException e) {
                    e.printStackTrace();
                }
            } else if (input.equals("3")) {
                String query = "SELECT * FROM flight_seats where flightID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, idNum);
                    ResultSet rs = preparedStatement.executeQuery();
                    if (rs.next()) {
                        String ec_seats = rs.getString("seats_available_ec");
                        System.out.println("Existing number of seats: " + ec_seats);
                    } else {
                        System.out.println("Incorrect ID Number");
                    }
                    System.out.println("Enter new number of seats: ");
                    int seat_input = scanner.nextInt();
                    String query2 = "UPDATE flight_seats SET seats_available_ec = ? where flightID = ?";

                    PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
                    preparedStatement.setInt(1, seat_input);
                    preparedStatement.setInt(2, idNum);

                    preparedStatement2.executeUpdate();

                    EMP2(ID, origin, dest);

                } catch (SQLException | InputMismatchException e) {
                    e.printStackTrace();
                }
            } else if (input.equals("4")) {
                EMP2(ID, origin, dest);
            } else {
                System.out.println("Invalid input");
                EMP2(ID, origin, dest);
            }

            EMP2(ID, origin, dest);
        } else if (choice.equals("4")) {
            ViewFlights();
        } else {
            System.out.println("Invalid input");
            EMP2(ID, origin, dest);
        }
    }


    public void Trav1(int num, String name) {
        System.out.println("Hello " + name + "!");
        System.out.println("TRAV1");
        System.out.println("\t1) Book a Ticket");
        System.out.println("\t2) Cancel an Upcoming Trip");
        System.out.println("\t3) Quit to the previous menu");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            System.out.println("Pick the Flight you want to book a ticket for:");
            String query = "select * from flight";
            int counter = 1;
            ArrayList<String[]> locations = new ArrayList<String[]>();
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    String origin = rs.getString("origin");
                    String dest = rs.getString("destination");
                    String ID = String.valueOf(rs.getInt("flightID"));
                    System.out.println("\t" + counter + ") " + origin + " --> " + dest);
                    String[] temp = new String[3];
                    temp[0] = ID;
                    temp[1] = origin;
                    temp[2] = dest;
                    locations.add(temp);
                    counter++;
                }
                System.out.println("\t" + counter + ") Quit to previous menu");
                String choice2 = scanner.nextLine();
                int choiceNum = Integer.parseInt(choice2);
                if (choiceNum == counter) {
                    Trav1(num, name);
                } else {
                    pickSeat((locations.get(choiceNum - 1)[0]), (locations.get(choiceNum - 1)[1]), (locations.get(choiceNum - 1)[2]), name, num);
                }
            } catch (SQLException | NumberFormatException n) {
                System.out.println("Invalid input");
                Trav1(num, name);
            }

        } else if (choice.equals("2")) {
            Trav2(num, name);
        } else if (choice.equals("3")) {
            TravelerMenu();
        } else {
            System.out.println("Invalid input");
            Trav1(num, name);
        }
    }


    public void pickSeat(String id, String origin, String dest, String name, int userID) {
        int idNum = Integer.parseInt(id);
        System.out.println("Pick the seat you want to book a ticket for: \n");
        System.out.println("\1) View Flight Details");
        String query = "Select * from flight_seats where flightID = ?";
        int counter = 2;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idNum);
            ResultSet rs = preparedStatement.executeQuery();

            int fc = rs.getInt("seats_available_fc");
            int bc = rs.getInt("seats_available_bc");
            int ec = rs.getInt("seats_available_ec");

            boolean first_class = false;
            boolean business_class = false;
            boolean economy_class = false;

            if (fc > 0) {
                System.out.println("\t2) First Class");
                counter++;
                first_class = true;
            }
            if (bc > 0) {
                System.out.println("\t" + counter + ") Business Class");
                counter++;
                business_class = true;
            }
            if (ec > 0) {
                System.out.println("\t" + counter + ") Economy Class");
                counter++;
                economy_class = true;
            }

            System.out.println("\t" + counter + ") Quit to cancel operation");

            String input = scanner.nextLine();
            int choiceNum = Integer.parseInt(input);
            String seat_choice = "";
            //TODO: If time use streams for input stuff
            if (counter == 2) {
                System.out.println("No seats available on this flight");
            } else if (choiceNum == counter) {
                Trav1(userID, name);
            } else if (input.equals("1")) {
                String query2 = "Select * from flight where flightID = ?";
                PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
                preparedStatement2.setInt(1, idNum);
                ResultSet rs2 = preparedStatement2.executeQuery();

                String departureDate = rs2.getString("departure_date");
                String departureTime = rs2.getString("departure_time");
                String arrivalDate = rs2.getString("arrival_date");
                String arrivalTime = rs2.getString("arrival_time");

                System.out.println("\nDeparture Airport: " + origin + " | Arrival Airport: " + dest +
                        " | \nDeparture Date: " + departureDate + " | Departure Time: " + departureTime +
                        " | \nArrival Date: " + arrivalDate + " | Arrival Time: " + arrivalTime);
                System.out.println("Press enter to continue");
                String temp = scanner.next();
                Trav1(userID, name);
            } else if (input.equals("2") && first_class) seat_choice = "First";
            else if (input.equals("2") && business_class) seat_choice = "Business";
            else if (input.equals("2")) seat_choice = "Economy";
            else if (input.equals("3") && first_class && business_class) seat_choice = "Business";
            else if (input.equals("3")) seat_choice = "Economy";
            else if (input.equals("4")) seat_choice = "Economy";
            else {
                System.out.println("Invalid input");
                pickSeat(id, origin, dest, name, userID);

            }

            String query2 = "insert into flight_bookings(flightID, user_id, seat_booked)" +
                    "values (?,?, ?)";
            PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
            preparedStatement2.setInt(1, idNum);
            preparedStatement2.setInt(2, userID);
            preparedStatement2.setString(3, seat_choice);
            preparedStatement2.executeUpdate();
            Trav1(idNum, name);
        } catch (SQLException | NumberFormatException n) {
            System.out.println("Invalid input");
            Trav1(userID, name);
        }
    }


    public void Trav2(int num, String name) {
        System.out.println("Pick the upcoming trip you'd like to cancel:");
        String query = "select * from flight_bookings left join flight on flight_bookings.flightID = flight.flightID where member_number = ?";
        int counter = 1;
        ArrayList<String[]> locations = new ArrayList<String[]>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, num);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String origin = rs.getString("origin");
                String dest = rs.getString("destination");
                String ID = String.valueOf(rs.getInt("flightID"));
                System.out.println("\t" + counter + ") " + origin + " --> " + dest);
                String[] temp = new String[3];
                temp[0] = ID;
                temp[1] = origin;
                temp[2] = dest;
                locations.add(temp);
                counter++;
            }
            if (counter == 1) {
                System.out.println("No upcoming trips");
                Trav1(num, name);
            }
            System.out.println("\t" + counter + ") Quit to previous menu");
            String choice2 = scanner.nextLine();
            int choiceNum = Integer.parseInt(choice2);
            if (choiceNum == counter) {
                Trav1(num, name);
            } else {
                System.out.println("Are you sure you'd like to cancel the following trip:\n");
                String query2 = "Select * from flight where flightID = ?";
                PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
                preparedStatement2.setInt(1, Integer.parseInt(locations.get(choiceNum - 1)[0]));
                ResultSet rs2 = preparedStatement2.executeQuery();

                String origin = rs2.getString("origin");
                String dest = rs2.getString("destination");
                String departureDate = rs2.getString("departure_date");
                String departureTime = rs2.getString("departure_time");
                String arrivalDate = rs2.getString("arrival_date");
                String arrivalTime = rs2.getString("arrival_time");

                System.out.println("\nDeparture Airport: " + origin + " | Arrival Airport: " + dest +
                        " | \nDeparture Date: " + departureDate + " | Departure Time: " + departureTime +
                        " | \nArrival Date: " + arrivalDate + " | Arrival Time: " + arrivalTime);
                System.out.println("\n\t1) Yes \n\t2) No\n\t3) Quit to cancel operation");
                String input2 = scanner.nextLine();
                if (input2.equals("1")) {
                    String query3 = "DELETE flight_bookings where flightID = ?";
                    PreparedStatement preparedStatement3 = connection.prepareStatement(query3);
                    preparedStatement3.setInt(1, Integer.parseInt(locations.get(choiceNum - 1)[0]));
                    preparedStatement3.executeUpdate();
                    //TODO: If you have time, update the seats table
                } else if (input2.equals("2")) {
                    Trav2(num, name);
                } else if (input2.equals("3")) {
                    Trav1(num, name);
                } else {
                    System.out.println("Invalid input");
                    Trav2(num, name);
                }


            }
        } catch (SQLException | NumberFormatException n) {
            System.out.println("Invalid input");
            Trav1(num, name);
        }
    }


    public void adminCRUD(String table, String menu) {
        System.out.println(menu + " Menu");
        System.out.println("\t1) Add");
        System.out.println("\t2) Update");
        System.out.println("\t3) Delete");
        System.out.println("\t4) Read");
        System.out.println("\t5) Quit to previous menu");

        String input = scanner.nextLine();

        if (input.equals("1")) adminAdd(table, menu);
        else if (input.equals("2")) adminUpdate(table, menu);
        else if (input.equals("3")) adminDelete(table, menu);
        else if (input.equals("4")) adminRead(connection, table, menu);
        else if (input.equals("5")) AdminMenu();
        adminCRUD(table, menu);
    }

    public void adminAdd(String table, String menu) {
        System.out.println("Enter quit at any prompt to cancel operation");

        if (menu.equals("Flight") || menu.equals("Airports")) {
            String query = "Select distinct origin as airport from flight union " +
                    "select distinct destination as airport from flight";
            ArrayList<String> distinctAirports = new ArrayList<>();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    String airport = rs.getString("airport");
                    if (!distinctAirports.contains(airport)) {
                        distinctAirports.add(airport);
                    }
                }


            } catch (SQLException e) {
                // Handle any SQL exceptions
                e.printStackTrace();
            }

            if (menu.equals("Flight")) {
                try {
                    System.out.println("Please select the Origin/Departure Airport:");
                    int count = 1;
                    for (int i = 0; i < distinctAirports.size(); i++) {
                        System.out.println("\t" + count + ") " + distinctAirports.get(i));
                        count++;
                    }
                    int origin_choice_in = Integer.parseInt(scanner.nextLine());
                    String origin_choice = distinctAirports.get(origin_choice_in);
                    origin_choice_in--;
                    if (origin_choice.equals("quit")) adminAdd(table, menu);

                    System.out.println("Please select the Destination/Arrival Airport:");
                    count = 1;
                    for (int i = 0; i < distinctAirports.size(); i++) {
                        System.out.println("\t" + count + ") " + distinctAirports.get(i));
                        count++;
                    }
                    int dest_choice_in = Integer.parseInt(scanner.nextLine());
                    String dest_choice = distinctAirports.get(dest_choice_in);
                    dest_choice_in--;
                    if (dest_choice.equals("quit")) adminAdd(table, menu);

                    System.out.println("Please enter the Departure Date:");
                    String dday_choice = scanner.nextLine();
                    if (dday_choice.equals("quit")) adminAdd(table, menu);

                    System.out.println("Please enter the Departure Time:");
                    String dtime_choice = scanner.nextLine();
                    if (dtime_choice.equals("quit")) adminAdd(table, menu);

                    System.out.println("Please enter the Arrival Date:");
                    String aday_choice = scanner.nextLine();
                    if (aday_choice.equals("quit")) adminAdd(table, menu);

                    System.out.println("Please enter the Arrival Time:");
                    String atime_choice = scanner.nextLine();
                    if (atime_choice.equals("quit")) adminAdd(table, menu);

                    System.out.println("Please enter the Flight ID: ");
                    int ID = scanner.nextInt();
                    if (String.valueOf(ID).equals("quit")) adminAdd(table, menu);


                    String query2 = "Insert into flight values (?, ?, ?," +
                            "?, ?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(query2);
                    preparedStatement.setInt(1, ID);
                    preparedStatement.setString(2, origin_choice);
                    preparedStatement.setString(3, dest_choice);
                    preparedStatement.setString(4, dday_choice);
                    preparedStatement.setString(5, aday_choice);
                    preparedStatement.setString(6, dtime_choice);
                    preparedStatement.setString(7, atime_choice);
                    preparedStatement.executeUpdate();
                    System.out.println("Successfully added");

                } catch (SQLException | NumberFormatException n) {

                    System.out.println("Invalid input");

                    n.printStackTrace();
                    adminAdd(table, menu);
                }
            }
        }
    }

    public void adminUpdate(String table, String menu) {
        System.out.println("Pick the " + menu + " you want to update:");
        if (menu.equals("Flight")) {
            String query = "select * from flight";
            int counter = 1;
            ArrayList<String[]> locations = new ArrayList<String[]>();
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    String origin = rs.getString("origin");
                    String dest = rs.getString("destination");
                    String ID = String.valueOf(rs.getInt("flightID"));
                    System.out.println("\t" + counter + ") " + origin + " --> " + dest);
                    String[] temp = new String[3];
                    temp[0] = ID;
                    temp[1] = origin;
                    temp[2] = dest;
                    locations.add(temp);
                    counter++;
                }
                System.out.println("\t" + counter + ") Quit to previous menu");
                String choice2 = scanner.nextLine();
                int choiceNum = Integer.parseInt(choice2);
                if (choiceNum == counter) {
                    return;
                } else {
                    updateFlight((locations.get(choiceNum - 1)[0]));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
//        else if(menu.equals("Seats")){
//            String query = "select * from flight_seats left join flight on flight_bookings.flightID = flight.flightID";
//            int counter = 1;
//            ArrayList<String[]> locations = new ArrayList<String[]>();
//            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//                ResultSet rs = preparedStatement.executeQuery();
//                while (rs.next()) {
//                    String origin = rs.getString("origin");
//                    String dest = rs.getString("destination");
//                    String ID = String.valueOf(rs.getInt("flightID"));
//                    System.out.println("\t" + counter + ") " + origin + " --> " + dest);
//                    String[] temp = new String[3];
//                    temp[0] = ID;
//                    temp[1] = origin;
//                    temp[2] = dest;
//                    locations.add(temp);
//                    counter++;
//                }
//                System.out.println("\t" + counter + ") Quit to previous menu");
//                String choice2 = scanner.nextLine();
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//
//        }
    }

    public void adminDelete(String table, String menu){
        System.out.println("Select the " + menu + " you'd like to delete");
        String query = "select * from " + table;
        if(menu.equals("Flight")){
            DeleteFlight(query, table, menu);
            return;
        }
        else if(menu.equals("Seats") || menu.equals("Bookings and Passengers") || menu.equals("Airports")) {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);


                int columnCount = resultSet.getMetaData().getColumnCount();
                int[] ids = new int[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(resultSet.getMetaData().getColumnName(i) + "\t");
                }
                System.out.println();


                while (resultSet.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        if (resultSet.getMetaData().getColumnType(i) == java.sql.Types.INTEGER) {
                            System.out.print(resultSet.getInt(i) + "\t");
                        } else {
                            System.out.print(resultSet.getString(i) + "\t");
                        }
                    }
                    System.out.println();
                }
                String input = scanner.nextLine();
                int input_val = Integer.parseInt(input);


                String query2 = "Delete from " + table + " Where flightID = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query2);
                preparedStatement.setInt(1, ids[input_val-1]);

                preparedStatement.executeUpdate();


            } catch (SQLException | NumberFormatException e) {
                // Handle any SQL exceptions
                e.printStackTrace();
            }
        }
        else{
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);


                int columnCount = resultSet.getMetaData().getColumnCount();
                int[] ids = new int[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(resultSet.getMetaData().getColumnName(i) + "\t");
                }
                System.out.println();


                while (resultSet.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        if (resultSet.getMetaData().getColumnType(i) == java.sql.Types.INTEGER) {
                            System.out.print(resultSet.getInt(i) + "\t");
                        } else {
                            System.out.print(resultSet.getString(i) + "\t");
                        }
                    }
                    System.out.println();
                }
                String input = scanner.nextLine();
                int input_val = Integer.parseInt(input);


                String query2 = "Delete from " + table + " Where user_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query2);
                preparedStatement.setInt(1, ids[input_val-1]);

                preparedStatement.executeUpdate();


            } catch (SQLException | NumberFormatException e) {
                // Handle any SQL exceptions
                e.printStackTrace();
            }
        }
    }


    public void adminRead(Connection connect, String table, String menu){
        System.out.println("Reading from " + menu + " table");
        String query = "Select * From " + table;

        try {
             Statement statement = connect.createStatement();
             ResultSet resultSet = statement.executeQuery(query);


            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(resultSet.getMetaData().getColumnName(i) + "\t");
            }
            System.out.println();


            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {

                    if (resultSet.getMetaData().getColumnType(i) == java.sql.Types.INTEGER) {
                        System.out.print(resultSet.getInt(i) + "\t");
                    } else {
                        System.out.print(resultSet.getString(i) + "\t");
                    }
                }
                System.out.println();
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
        System.out.println("\nPress enter to go back to the previous menu");
        scanner.nextLine();
    }


    public void updateFlight (String id){
        System.out.println("Enter quit at any prompt to cancel operation");
        System.out.println("\nPlease enter new Origin Airport and City or enter N/A for no change: ");
        String origin_input = scanner.nextLine();

        if (origin_input.equals("quit")) return;
        System.out.println("\nPlease enter new Destination Airport and City or enter N/A for no change: ");
        String dest_input = scanner.nextLine();

        if (dest_input.equals("quit")) return;
        System.out.println("\nPlease enter new Departure Date or enter N/A for no change: ");
        String dday_input = scanner.nextLine();

        if (dday_input.equals("quit")) return;
        System.out.println("\nPlease enter new Departure Time or enter N/A for no change: ");
        String dtime_input = scanner.nextLine();

        if (dtime_input.equals("quit")) return;
        System.out.println("\nPlease enter new Arrival Date or enter N/A for no change: ");
        String aday_input = scanner.nextLine();

        if (aday_input.equals("quit")) return;
        System.out.println("\nPlease enter new Arrival Time or enter N/A for no change: ");
        String atime_input = scanner.nextLine();

        if (atime_input.equals("quit")) return;

        int idNum = Integer.parseInt(id);
        String origin = "";
        String dest = "";
        String departureDate = "";
        String departureTime = "";
        String arrivalDate = "";
        String arrivalTime = "";

        String query = "SELECT * FROM flight where flightID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idNum);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                origin = rs.getString("origin");
                dest = rs.getString("destination");
                departureDate = rs.getString("departure_date");
                departureTime = rs.getString("departure_time");
                arrivalDate = rs.getString("arrival_date");
                arrivalTime = rs.getString("arrival_time");
            } else {
                System.out.println("Incorrect ID Number");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String query2 = "UPDATE flight SET origin = ?, destination = ?, departure_date = ?, " +
                "arrival_date = ?, departure_time = ?, arrival_time = ? where flightID = ? ";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query2)) {
            if (!origin_input.equals("N/A")) preparedStatement.setString(1, origin_input);
            else preparedStatement.setString(1, origin);

            if (!dest_input.equals("N/A")) preparedStatement.setString(2, dest_input);
            else preparedStatement.setString(2, dest);

            if (!dday_input.equals("N/A")) preparedStatement.setString(3, dday_input);
            else preparedStatement.setString(3, departureDate);

            if (!aday_input.equals("N/A")) preparedStatement.setString(4, aday_input);
            else preparedStatement.setString(4, arrivalDate);

            if (!dtime_input.equals("N/A")) preparedStatement.setString(5, dtime_input);
            else preparedStatement.setString(5, departureTime);

            if (!atime_input.equals("N/A")) preparedStatement.setString(6, atime_input);
            else preparedStatement.setString(6, arrivalTime);

            preparedStatement.setInt(7, idNum);
            preparedStatement.executeUpdate();
            System.out.println("Successfully Updated");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void DeleteFlight(String query, String table, String menu){
        int counter = 1;
        ArrayList<String[]> locations = new ArrayList<String[]>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String origin = rs.getString("origin");
                String dest = rs.getString("destination");
                String ID = String.valueOf(rs.getInt("flightID"));
                System.out.println("\t" + counter + ") " + origin + " --> " + dest);
                String[] temp = new String[3];
                temp[0] = ID;
                temp[1] = origin;
                temp[2] = dest;
                locations.add(temp);
                counter++;
            }
            System.out.println("\t" + counter + ") Quit to previous menu");
            String choice2 = scanner.nextLine();
            int choiceNum = Integer.parseInt(choice2);
            if (choiceNum == counter) {
                return;
            } else {
                System.out.println("Are you sure you'd like to delete the following trip:\n");
                String query2 = "Select * from flight where flightID = ?";
                PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
                preparedStatement2.setInt(1, Integer.parseInt(locations.get(choiceNum - 1)[0]));
                ResultSet rs2 = preparedStatement2.executeQuery();

                String origin = rs2.getString("origin");
                String dest = rs2.getString("destination");
                String departureDate = rs2.getString("departure_date");
                String departureTime = rs2.getString("departure_time");
                String arrivalDate = rs2.getString("arrival_date");
                String arrivalTime = rs2.getString("arrival_time");

                System.out.println("\nDeparture Airport: " + origin + " | Arrival Airport: " + dest +
                        " | \nDeparture Date: " + departureDate + " | Departure Time: " + departureTime +
                        " | \nArrival Date: " + arrivalDate + " | Arrival Time: " + arrivalTime);
                System.out.println("\n\t1) Yes \n\t2) No\n\t3) Quit to cancel operation");
                String input2 = scanner.nextLine();
                if (input2.equals("1")) {
                    String query3 = "DELETE flight_bookings where flightID = ?";
                    PreparedStatement preparedStatement3 = connection.prepareStatement(query3);
                    preparedStatement3.setInt(1, Integer.parseInt(locations.get(choiceNum - 1)[0]));
                    preparedStatement3.executeUpdate();

                    String query4 = "DELETE flight where flightID = ?";
                    PreparedStatement preparedStatement4 = connection.prepareStatement(query4);
                    preparedStatement4.setInt(1, Integer.parseInt(locations.get(choiceNum - 1)[0]));
                    preparedStatement4.executeUpdate();

                    String query5 = "DELETE flight_seats where flightID = ?";
                    PreparedStatement preparedStatement5 = connection.prepareStatement(query5);
                    preparedStatement5.setInt(1, Integer.parseInt(locations.get(choiceNum - 1)[0]));
                    preparedStatement5.executeUpdate();

                } else if (input2.equals("2")) {
                    DeleteFlight(query, table, menu);
                } else if (input2.equals("3")) {
                    return;
                } else {
                    System.out.println("Invalid input");
                    DeleteFlight(query, table, menu);
                }


            }
        } catch (SQLException | NumberFormatException n) {
            System.out.println("Invalid input");
        }
    }

}

