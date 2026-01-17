package org.example;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

// an object for interacting with the database
public class UserDAO {

    // variables storing the path of the database, usernae, and password
    private String url = "jdbc:mysql://localhost:3307/testdb?useSSL=false&allowPublicKeyRetrieval=true";
    private String dbuser = "root";
    private String password = "passkey";

    public UserDAO() {
        this.url = url;
        this.dbuser = dbuser;
        this.password = password;
    }

    // establish and return a connection to the database
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, dbuser, password);
    }

    public User fetchUser(String username, String password) {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users WHERE username = ? AND password = SHA2(?, 256)");
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                return new User(result.getString("id"),
                        result.getString("username"),
                        result.getString("password"),
                        result.getString("email"),
                        result.getDate("date_joined")
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addUser(User user) {
        try (Connection conn = getConnection()) {
            String add_to_users = "INSERT INTO Users (id, username, password, email, date_joined) VALUES (?, ?, SHA2(?, 256), ?, ?)";

            PreparedStatement prepped_statement = conn.prepareStatement(add_to_users);

            prepped_statement.setString(1, user.getId());
            prepped_statement.setString(2, user.getUsername());
            prepped_statement.setString(3, user.getPassword());
            prepped_statement.setString(4, user.getEmail());
            prepped_statement.setString(5, convertDateFormat(user.getDate_joined()));
            prepped_statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

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
                    "payment_received" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
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

        prepped_statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // start up the database. If the
    public void rundb() {
        try (Connection conn = getConnection()) {
            String create_users_table = "CREATE TABLE IF NOT EXISTS Users (" +
                    "id VARCHAR(90) NOT NULL UNIQUE PRIMARY KEY, " +
                    "username VARCHAR(30) UNIQUE, " +
                    "password VARCHAR(64), " +
                    "email VARCHAR(30), " +
                    "date_joined DATE" +
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
                    "FOREIGN KEY (artist_id) REFERENCES Users(id)" +
                    ")";
            PreparedStatement create_users_if_not_exists = conn.prepareStatement(create_users_table);
            PreparedStatement create_comms_if_not_exists = conn.prepareStatement(create_comms_table);

            create_users_if_not_exists.executeUpdate();
            create_comms_if_not_exists.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String convertDateFormat (Date date) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

}
