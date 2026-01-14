package org.example;

import java.sql.*;

public class UserDAO {
    private String url = "jdbc:mysql://localhost:3307/testdb?useSSL=false&allowPublicKeyRetrieval=true";
    private String dbuser = "root";
    private String password = "passkey";

    public UserDAO() {
        this.url = url;
        this.dbuser = dbuser;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, dbuser, password);
    }

    public void fetchAllUsers() {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                System.out.println(result.getString("uuid")+" "+result.getString("username")+" "+result.getString("password")+" "+result.getString("email")+" "+result.getString("datejoined"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addUser(User user) {
        try (Connection conn = getConnection()) {
            String add_to_users = "INSERT INTO users (uuid, username, password, email, datejoined) VALUES (?, ?, ?, ?, ?)";

            PreparedStatement prepped_statement = conn.prepareStatement(add_to_users);

            prepped_statement.setString(1, user.getUuid());
            prepped_statement.setString(2, user.getUsername());
            prepped_statement.setString(3, user.getPassword());
            prepped_statement.setString(4, user.getEmail());
            prepped_statement.setString(5, user.getDate_joined().toString());

            prepped_statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void rundb() {
        try (Connection conn = getConnection()) {
            String create_table = "CREATE TABLE IF NOT EXISTS users (uuid VARCHAR(90) PRIMARY KEY, username VARCHAR(30), password VARCHAR(30), email VARCHAR(30), datejoined VARCHAR(30))";
            String add_to_users = "INSERT INTO users (uuid, username, password, email, datejoined) VALUES (?, ?, ?, ?, ?)";

            PreparedStatement create_if_not = conn.prepareStatement(create_table);
            create_if_not.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
