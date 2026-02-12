package org.example.dev;

import org.example.Commission;
import org.example.User;

import java.sql.*;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.TimeZone;

// a class of utilities that are useful as i develop sweetkey, but should NEVER
// be seen in production code
public class DevUtils {
    private final String url = "jdbc:mysql://localhost:3307/testdb?useSSL=false&allowPublicKeyRetrieval=true";
    private final String dbuser = "root";
    private final String password = "passkey";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, dbuser, password);
    }

    // returns all users.
    @InternalMethod
    @Deprecated
    public ArrayList<User> fetchAllUsers_INTERNAL() {
        ArrayList<User> allUsers = new ArrayList<User>();
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users");
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                allUsers.add(new User(result.getString("id"),
                        result.getString("username"),
                        result.getString("email"),
                        result.getDate("date_joined")
                            ));
            }
            return allUsers;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allUsers;
    }

    // returns all commissions.
    @InternalMethod
    @Deprecated
    public ArrayList<Commission> fetchAllComms_INTERNAL() {
        ArrayList<Commission> allComms = new ArrayList<Commission>();
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Comms");
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
    } //ADMIN FETCH ALL COMMS

    // get current UTC database time
    @InternalMethod
    @Deprecated
    public String getDBDate() {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT NOW()");
            ResultSet result = stmt.executeQuery();
            while (result.next())
                    return result.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    // wipes all tables without wiping the database.
    @InternalMethod
    @Deprecated
    public void eraseEverything() {
        try (Connection conn = getConnection()) {
            String dropComms = "DROP TABLE Comms";
            String dropUsers = "DROP TABLE Users";

            PreparedStatement dcps = conn.prepareStatement(dropComms);
            PreparedStatement dups = conn.prepareStatement(dropUsers);

            dcps.executeUpdate();
            dups.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // start up the database. Create the tables if they don't already exist.
    public void rundb() {
        try (Connection conn = getConnection()) {
            String create_users_table = "CREATE TABLE IF NOT EXISTS Users (" +
                    "id VARCHAR(90) NOT NULL UNIQUE PRIMARY KEY, " +
                    "username VARCHAR(30) UNIQUE, " +
                    "email VARCHAR(30), " +
                    "date_joined DATE," +
                    "salt VARBINARY(16)," +
                    "hash VARBINARY(16)" +
                    ")";
            String create_comms_table = "CREATE TABLE IF NOT EXISTS Comms (" +
                    "id VARCHAR(90) PRIMARY KEY, " +
                    "artist_id VARCHAR(90), " +
                    "commissioner_handle VARCHAR(30), " +
                    "platform VARCHAR(30), " +
                    "date_ordered DATE, " +
                    "date_expected DATE, " +
                    "size VARCHAR(30), " +
                    "cost DECIMAL(10, 2), " +
                    "description VARCHAR(100), " +
                    "reference_link VARCHAR(120), " +
                    "payment_received BOOLEAN DEFAULT FALSE, " +
                    "status VARCHAR(30), " +
                    "FOREIGN KEY (artist_id) REFERENCES Users(id)" +
                    ")";

            String timeZone = "SET time_zone = ?";

            PreparedStatement create_users_if_not_exists = conn.prepareStatement(create_users_table);
            PreparedStatement create_comms_if_not_exists = conn.prepareStatement(create_comms_table);
            PreparedStatement setTimeZone_st = conn.prepareStatement(timeZone);
            setTimeZone_st.setString(1, String.valueOf(ZoneId.systemDefault().getRules().getOffset(Instant.now())));

            setTimeZone_st.executeUpdate();
            create_users_if_not_exists.executeUpdate();
            create_comms_if_not_exists.executeUpdate();

            System.out.println("done!");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
