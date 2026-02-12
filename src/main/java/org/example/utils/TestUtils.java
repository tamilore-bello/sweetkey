package org.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;

public class TestUtils
{
    private final String url = "jdbc:mysql://localhost:3307/testdb?useSSL=false&allowPublicKeyRetrieval=true";
    private final String dbuser = "root";
    private final String password = "passkey";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, dbuser, password);
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
            
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
