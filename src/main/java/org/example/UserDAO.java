package org.example;
import org.example.model.Commission;
import org.example.model.User;

import java.math.BigDecimal;
import java.sql.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
    public void addUser(User user, byte[] salt, byte[] hash) throws SQLException {
        Connection conn = getConnection();
            String add_to_users = "INSERT INTO Users (id, username, email, date_joined, salt, hash) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement prepped_statement = conn.prepareStatement(add_to_users);

            prepped_statement.setString(1, user.getId());
            prepped_statement.setString(2, user.getUsername());
            prepped_statement.setString(3, user.getEmail());
            prepped_statement.setString(4, convertDateFormat(user.getDate_joined()));
            prepped_statement.setBytes(5, salt);
            prepped_statement.setBytes(6, hash);
            prepped_statement.executeUpdate();
            conn.close();
    }

    // fetch the salt and hash for a User password authentication
    public byte[][] fetchSaltAndHash(String username) throws SQLException{
        Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT salt, hash FROM Users WHERE username = ?");
            stmt.setString(1, username);

            ResultSet result = stmt.executeQuery();
            if (result.next())

                return new byte[][] {result.getBytes("salt"), result.getBytes("hash")};
            else
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
            return new User(
                result.getString("id"),
                result.getString("username"),
                result.getString("email"),
                result.getDate("date_joined")
            );
        } catch (SQLException e) {
            System.out.println("RESULT SET IS EMPTY or USER DOESN'T EXIST");
            e.printStackTrace();
        }
        return null;
    }


    // USER FUNCTIONS  ------------------------------------------------------------------------------------
    // change the username of a User
    public boolean updateUsername(String userId, String newUsername) {
            try (Connection conn = getConnection()) {
                String add_to_users = "UPDATE Users SET username = ? WHERE id = ?";

                PreparedStatement prepped_statement = conn.prepareStatement(add_to_users);

                prepped_statement.setString(1, newUsername);
                prepped_statement.setString(2, userId);

                prepped_statement.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
    }

    // change the email of a User
    public boolean updateEmail(String userId, String newEmail) {
            try (Connection conn = getConnection()) {
                String add_to_users = "UPDATE Users SET email = ? WHERE id = ?";

                PreparedStatement prepped_statement = conn.prepareStatement(add_to_users);

                prepped_statement.setString(1, newEmail);
                prepped_statement.setString(2, userId);

                prepped_statement.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
    }

    // delete a user.
    // Ideally, we can reinforce with password authentication as well.
    public void deleteUser(String userId) {
        try (Connection conn = getConnection()) {

            String delete_commissions = "DELETE FROM Comms WHERE artist_id = ?";
            PreparedStatement delete_comms = conn.prepareStatement(delete_commissions);
            delete_comms.setString(1, userId);

            String delete_this_user = "DELETE FROM Users WHERE id = ?";
            PreparedStatement delete_user = conn.prepareStatement(delete_this_user);
            delete_user.setString(1, userId);

            delete_comms.executeUpdate();
            delete_user.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // COMMISSION FUNCTIONS  ------------------------------------------------------------------------------------
    // fetch all of a User's commissions based on User id.
    public List<Commission> fetchAllUserComms(String artist_id, OrderCode code) {
        ArrayList<Commission> allComms = new ArrayList<>();
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(orderByCode(code));
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
            conn.close();
            return Collections.unmodifiableList(allComms);
        } catch (SQLException e) {

            e.printStackTrace();
        }

        return Collections.unmodifiableList(allComms);
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
            prepped_statement.setBigDecimal(8,  BigDecimal.valueOf(commission.getCost()));
            prepped_statement.setString(9, commission.getDescription());
            prepped_statement.setString(10, commission.getReference_link());
            prepped_statement.setBoolean(11, commission.getPaymentReceived());
            prepped_statement.setString(12, commission.getStatus());

            prepped_statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // STATS ------------------------------------------------------------------------------------
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
                return (result.getDouble(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    // HELPING METHODS ------------------------------------------------------------------------------------
    // Format a Date object into a String, yyyy-MM-dd
    public String convertDateFormat (Date date) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    // provide the correct SQL query string where code relates to a sort type
    private static String orderByCode(OrderCode code) {
        // as seen in method MAIN, where
        // soonest = 0, oldest = 1, and size = 2
        switch (code) {
            case DUE_SOONEST:
                return "SELECT * FROM Comms WHERE artist_id = ? ORDER BY date_expected"; // order by due soonest
            case OLD_TO_NEW:
                return "SELECT * FROM Comms WHERE artist_id = ? ORDER BY date_ordered"; // order by oldest - newest
            case SIZE_ASCENDING:
                return "SELECT * FROM Comms WHERE artist_id = ? ORDER BY CASE size " + // order by size (ascending)
                        "WHEN 'Icon' THEN 1 " +
                        "WHEN 'Bust' THEN 2 " +
                        "WHEN 'Half-Body' THEN 3 " +
                        "WHEN '3/4' THEN 4 " +
                        "WHEN 'Full-body' THEN 5 " +
                        "WHEN 'Chibi' THEN 6 " +
                        "WHEN 'Reference' THEN 7 " +
                        "ELSE 8 END";
            case LATE_ONLY:
                return "SELECT * FROM Comms WHERE date_expected < CURDATE() AND status != 'Completed' " +
                        "AND artist_id = ? ORDER BY date_expected"; // return only late
            default:
                return "SELECT * FROM Comms WHERE artist_id = ?"; // default
        }
    }

    // provide the correct SQL query string where code relates to a date range
    private static String selectEarningsByDateRange(int code) {
        // as seen in method MAIN, where
        // soonest = 0, oldest = 1, and size = 2
        switch (code) {
            case 0:
                return "SELECT SUM(cost) FROM Comms WHERE artist_id = ? " +
                        "AND date_ordered >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
                        "AND date_ordered <= CURDATE() ";  // gross of last 7 days orders
            case 1:
                return "SELECT SUM(cost) FROM Comms WHERE artist_id = ? " +
                        "AND YEAR(date_ordered) = YEAR(CURDATE()) " +
                        "AND MONTH(date_ordered) = MONTH(CURDATE())";  // gross of current month's orders
            case 2:
                return "SELECT SUM(cost) FROM Comms WHERE artist_id = ? " +
                        "AND YEAR(date_ordered) = YEAR(CURDATE())"; // gross of current year's orders
            default:
                return "SELECT SUM(cost) FROM Comms WHERE artist_id = ?"; // gross of all time's orders
        }
    }

    // enums for selectEarningsByDateRange
    public enum OrderCode {
        DUE_SOONEST,
        OLD_TO_NEW,
        SIZE_ASCENDING,
        LATE_ONLY,
        ARCHIVED_ONLY,
        ALL,
        DEFAULT
    }



}


