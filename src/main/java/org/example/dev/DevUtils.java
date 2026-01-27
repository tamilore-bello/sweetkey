package org.example.dev;

import org.example.Commission;
import org.example.User;

import java.sql.*;
import java.util.ArrayList;

// a class of utilities that are useful as i develop sweetkey, but should NEVER
// be seen in production code
public class DevUtils {
    private String url = "jdbc:mysql://localhost:3307/testdb?useSSL=false&allowPublicKeyRetrieval=true";
    private String dbuser = "root";
    private String password = "passkey";

    public DevUtils() {
        this.url = url;
        this.dbuser = dbuser;
        this.password = password;
    }

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

}
