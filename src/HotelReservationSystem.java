import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "7266194@MySql";
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            Connection con = DriverManager.getConnection(url, username, password);
            Statement stmt = con.createStatement();

            while (true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.print("Choose An Option: ");
                int choice = sc.nextInt();
                switch (choice){
                    case 1:
                        reserveRoom(con,sc,stmt);
                        break;
                    case 2:
                        viewReservation(con,stmt);
                        break;
                    case 3:
                        getRoomNumber(con,sc,stmt);
                        break;
                    case 4:
                        updateReservation(con,sc,stmt);
                        break;
                    case 5:
                        deleteReservation(con,sc,stmt);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid Choice. Try Again.");
                }
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom(Connection con, Scanner sc, Statement stmt){
        try{
            System.out.print("Enter Guest Name: ");
            String guestName = sc.next();
            sc.nextLine();
            System.out.print("Enter Room Number: ");
            int roomNumber = sc.nextInt();
            System.out.print("Enter Contact Number: ");
            String contactNumber = sc.next();

            String sql = "INSERT INTO reservations (guest_name, room_number,contact_number) "+
                    "VALUES('"+ guestName+ "', " + roomNumber + ", '"+contactNumber+"' )";


            int affectRows = stmt.executeUpdate(sql);

            if (affectRows > 0){
                System.out.println("Reservation Successful");
            } else{
                System.out.println("Reservation Failed!");
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static void viewReservation(Connection con, Statement stmt){
        String sql = "SELECT reservation_id, guest_name,room_number, contact_number, reservation_date FROM reservations";

        try(ResultSet rs = stmt.executeQuery(sql)){
            System.out.println("Current Reservations: ");
            System.out.println("+----------------+-----------------+---------------+----------------------+--------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number       | Reservation Date         |");
            System.out.println("+----------------+-----------------+---------------+----------------------+--------------------------+");
            while(rs.next()){
                int reservationId = rs.getInt("reservation_id");
                String guestName = rs.getString("guest_name");
                int roomNumber = rs.getInt("room_number");
                String contactNumber = rs.getString("contact_number");
                String reservationDate = rs.getTimestamp("reservation_date").toString();

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationId,guestName,roomNumber, contactNumber, reservationDate);

            }
            System.out.println("+----------------+-----------------+---------------+----------------------+--------------------------+");
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void getRoomNumber(Connection con, Scanner sc, Statement stmt){
        try {
            System.out.print("Enter Reservation ID: ");
            int reservationID = sc.nextInt();
            System.out.print("Enter Guest Name: ");
            String guestName = sc.next();

            String sql = "SELECT room_number FROM reservations "+
                    "WHERE reservation_id = "+reservationID+ " AND guest_name = '"+ guestName +"'";

            try(ResultSet rs = stmt.executeQuery(sql)){
                if (rs.next()){
                    int roomNumber = rs.getInt("room_number");
                    System.out.println("Room Number For Reservation Id "+ reservationID +" And Guest "+ guestName+ " Is: "+ roomNumber);
                } else {
                    System.out.println("Reservation Not Found For The Given ID And Guest Name");
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection con, Scanner sc, Statement stmt){
        try{
            System.out.print("Enter Reservation ID To Update: ");
            int reservationId = sc.nextInt();
            sc.nextLine();

            if (!reservationExists(con, reservationId)){
                System.out.println("Reservation Not Found For The Given ID.");
                return;
            }

            System.out.print("Enter New Guest Name: ");
            String newGuestName = sc.nextLine();
            System.out.print("Enter New Room Number: ");
            int newRoomNumber = sc.nextInt();
            System.out.print("Enter New Contact Number: ");
            String newContactNumber = sc.next();

            String sql = "UPDATE reservations SET guest_name = '"+ newGuestName + "', " + "room_number = " + newRoomNumber + ", "+
                    "contact_number = '" + newContactNumber + "' "+  "WHERE reservation_id = "+ reservationId;

            int affectRows = stmt.executeUpdate(sql);

            if (affectRows>0){
                System.out.println("Reservation Update Successfully.");
            } else {
                System.out.println("Reservation Update Failed!");
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection con, Scanner sc, Statement stmt){
        try {
            System.out.print("Enter Reservation ID To Delete: ");
            int reservationId = sc.nextInt();

            if (!reservationExists(con,reservationId)){
                System.out.println("Reservation Not Found For The Given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id ="+reservationId;

            int affectRows = stmt.executeUpdate(sql);

            if (affectRows>0){
                System.out.println("Reservation Delete Successfully.");
            } else {
                System.out.println("Reservation Delete Failed!");
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection con, int reservationId){
        try {
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = "+reservationId;

            try(Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
                return  rs.next();
            }

        } catch (SQLException e){
            e.printStackTrace();
            return  false;
        }
    }

    private static void exit() throws InterruptedException{
        System.out.print("Exiting System");
        int i = 5;
        while (i!=0){
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("Thank You For Using Hotel Reservation System.");
    }
}
