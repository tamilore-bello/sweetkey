package org.example;
import java.math.BigDecimal;
import java.sql.*;
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
            System.out.println("Executed");
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
            return new User (
                    result.getString("id"),
                    result.getString("username"),
                    result.getString("email"),
                    result.getDate("date_joined")
            );
        } catch (SQLException e) {
            System.out.println("RESULT SET IS EMPTY");
            e.printStackTrace();
        }
        return null;
    }

    // fetch all of a User's commissions based on User id.
    public ArrayList<Commission> fetchAllUserComms(String id) {
        ArrayList<Commission> allComms = new ArrayList<>();
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Comms WHERE artist_id = ?");
            stmt.setString(1, id);

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
                System.out.println(1);
            }
            return allComms;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allComms;
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
            PreparedStatement create_users_if_not_exists = conn.prepareStatement(create_users_table);
            PreparedStatement create_comms_if_not_exists = conn.prepareStatement(create_comms_table);

            create_users_if_not_exists.executeUpdate();
            create_comms_if_not_exists.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Format a Date object into a String, yyyy-MM-dd
    public String convertDateFormat (Date date) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }
}
