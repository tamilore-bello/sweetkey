package org.example;
import java.math.BigDecimal;
import java.sql.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

// an object for interacting with the database
public class UserDAO {

    // instance data storing the path of the database, usernae, and password
    private final String url = "jdbc:mysql://localhost:3307/testdb?useSSL=false&allowPublicKeyRetrieval=true";
    private final String dbuser = "root";
    private final String password = "passkey";

    // establish and return a connection to the database
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, dbuser, password);
    }

    // USER LOG IN / SIGN-UP ------------------------------------------------------------------------------------
    // add a new User to the database
    public void addUser(User user, byte[] salt, byte[] hash) {
        try (Connection conn = getConnection()) {
            String add_to_users = "INSERT INTO Users (id, username, email, date_joined, salt, hash) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement prepped_statement = conn.prepareStatement(add_to_users);

            prepped_statement.setString(1, user.getId());
            prepped_statement.setString(2, user.getUsername());
            prepped_statement.setString(3, user.getEmail());
            prepped_statement.setString(4, convertDateFormat(user.getDate_joined()));
            prepped_statement.setBytes(5, salt);
            prepped_statement.setBytes(6, hash);
            prepped_statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // add a new Commission to the database
    public void addComm(Commission commission) {
        try (Connection conn = getConnection()) {
            String add_to_comms = "INSERT INTO Comms (" +
                    "id, " +
                    "artist_id, " +
                    "commissioner_handle, " +
                    "platform, " +
                    "date_ordered, " +
                    "date_expected, " +
                    "size, " +
                    "cost, " +
                    "description, " +
                    "reference_link, " +
                    "payment_received," +
                    "status" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    ;
            PreparedStatement prepped_statement = conn.prepareStatement(add_to_comms);
            prepped_statement.setString(1, commission.getId());
            prepped_statement.setString(2, commission.getArtist_id());
            prepped_statement.setString(3, commission.getCommissioner_handle());
            prepped_statement.setString(4, commission.getPlatform());
            prepped_statement.setString(5, convertDateFormat(commission.getDate_ordered()));
            prepped_statement.setString(6, convertDateFormat(commission.getDate_expected()));
            prepped_statement.setString(7, commission.getSize());
            prepped_statement.setBigDecimal(8, new BigDecimal(commission.getCost()));
            prepped_statement.setString(9, commission.getDescription());
            prepped_statement.setString(10, commission.getReference_link());
            prepped_statement.setBoolean(11, commission.getPaymentReceived());
            prepped_statement.setString(12, commission.getStatus());

            prepped_statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // fetch the salt and hash for a User password authentication
    public byte[][] fetchSaltAndHash(String username) {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT salt, hash FROM Users WHERE username = ?");
            stmt.setString(1, username);

            ResultSet result = stmt.executeQuery();
            if (result.next())
                return new byte[][] {result.getBytes("salt"), result.getBytes("hash")};
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // fetch a User based on their Username, used for fetching a user post-auth and for checking that a Username
    // doesn't already exist during User creation.
    public User fetchUser(String username) {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users WHERE username = ? LIMIT 1");
            stmt.setString(1, username);

            ResultSet result = stmt.executeQuery();
            result.next();
            try {
                return new User(
                        result.getString("id"),
                        result.getString("username"),
                        result.getString("email"),
                        result.getDate("date_joined")
                );
            } catch (SQLException e){
                return null;
            }
        } catch (SQLException e) {
            System.out.println("RESULT SET IS EMPTY");
            e.printStackTrace();
        }
        return null;
    }

    // COMMISSION FETCHING ------------------------------------------------------------------------------------
    // fetch all of a User's commissions based on User id.
    public ArrayList<Commission> fetchAllUserComms(String artist_id, int order) {
        ArrayList<Commission> allComms = new ArrayList<>();
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(orderByCode(order));
            stmt.setString(1, artist_id);

            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                allComms.add(new Commission(
                        result.getString("id"),
                        result.getString("artist_id"),
                        result.getString("commissioner_handle"),
                        result.getString("platform"),
                        result.getDate("date_ordered"),
                        result.getDate("date_expected"),
                        result.getString("size"),
                        (result.getBigDecimal("cost")).doubleValue(),
                        result.getString("description"),
                        result.getString("reference_link"),
                        result.getBoolean("payment_received"),
                        result.getString("status")
                ));
            }
            return allComms;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allComms;
    }

    // provide the correct SQL query string where code relates to a sort type
    private static String orderByCode(int code) {
        // as seen in method MAIN, where
        // soonest = 0, oldest = 1, and size = 2
        switch (code) {
            case 0:
                return "SELECT * FROM Comms WHERE artist_id = ? ORDER BY date_expected"; // order by due soonest
            case 1:
                return "SELECT * FROM Comms WHERE artist_id = ? ORDER BY date_ordered"; // order by oldest - newest
            case 2:
                return "SELECT * FROM Comms WHERE artist_id = ? ORDER BY CASE size " + // order by size (ascending)
                        "WHEN 'Icon' THEN 1 " +
                        "WHEN 'Bust' THEN 2 " +
                        "WHEN 'Half-Body' THEN 3 " +
                        "WHEN '3/4' THEN 4 " +
                        "WHEN 'Full-body' THEN 5 " +
                        "WHEN 'Chibi' THEN 6 " +
                        "WHEN 'Reference' THEN 7 " +
                        "ELSE 8 END";
            default:
                return "SELECT * FROM Comms WHERE artist_id = ?"; // default
        }
    }


    // STATS ------------------------------------------------------------------------------------

    // TODO
    // commission type (bar graph)??
    // top (50, 25, 10% of users based on timeliness)
        // and above based on earnings
    // advised amount witholding amount (30% of income for current tax year)
    // percent of late / completed (%late)

    // an API to view earnings / commission qty by month in graph format or smth

    // fetch all of a user's current late commissions
    public ArrayList<Commission> fetchAllUserLateCommissions(String artist_id) {
        ArrayList<Commission> allComms = new ArrayList<>();
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement( "SELECT * FROM Comms WHERE date_expected < CURDATE() AND status != 'Completed' AND artist_id = ?");
            stmt.setString(1, artist_id);
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                allComms.add(new Commission(
                        result.getString("id"),
                        result.getString("artist_id"),
                        result.getString("commissioner_handle"),
                        result.getString("platform"),
                        result.getDate("date_ordered"),
                        result.getDate("date_expected"),
                        result.getString("size"),
                        (result.getBigDecimal("cost")).doubleValue(),
                        result.getString("description"),
                        result.getString("reference_link"),
                        result.getBoolean("payment_received"),
                        result.getString("status")
                ));
            }
            return allComms;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allComms;
    }

    // fetch # of all of a user's current late commissions
    public int countAllUserLateCommissions(String artist_id) {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement( "SELECT COUNT(*) FROM Comms WHERE date_expected < CURDATE() AND status != 'Completed' AND artist_id = ?");
            stmt.setString(1, artist_id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // fetch amount earned where code relates to a date range
    public double amountEarned(String artist_id, int code) {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(selectEarningsByDateRange(code));
            stmt.setString(1, artist_id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                System.out.println("getting double...");
                return (result.getDouble(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // provide the correct SQL query string where code relates to a date range
    private static String selectEarningsByDateRange(int code) {
        // as seen in method MAIN, where
        // soonest = 0, oldest = 1, and size = 2
        switch (code) {
            case 0:
                return "SELECT SUM(cost) FROM Comms WHERE artist_id = ? " +
                        "AND YEAR(date_ordered) = YEAR(CURDATE()) " +
                        "AND MONTH(date_ordered) = MONTH(CURDATE()) " +
                        "AND WEEK(date_ordered) = WEEK(CURDATE())"; // gross of current week's orders
            case 1:
                return "SELECT SUM(cost) FROM Comms WHERE artist_id = ? " +
                        "AND YEAR(date_ordered) = YEAR(CURDATE()) " +
                        "AND MONTH(date_ordered) = MONTH(CURDATE()) ";  // gross of current month's orders
            case 2:
                return "SELECT SUM(cost) FROM Comms WHERE artist_id = ? " +
                        "AND YEAR(date_ordered) = YEAR(CURDATE())"; // gross of current year's orders
            default:
                return "SELECT SUM(cost) FROM Comms WHERE artist_id = ?"; // gross of all time's orders
        }
    }


    // HELPING METHODS ------------------------------------------------------------------------------------
    // Format a Date object into a String, yyyy-MM-dd
    public String convertDateFormat (Date date) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }
}
