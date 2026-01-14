package org.example;

import java.sql.*;
import java.util.*;


public class Main {
    public void main() {
        // init scanner object
        Scanner scanner = new Scanner(System.in);

        // instance variables
        String username;
        String password;

        System.out.println("ENTER USERNAME");
        username = scanner.nextLine();
        System.out.println("ENTER PASSWORD ");
        password = scanner.nextLine();

        User user = new User(username, password);
        System.out.println(user.getUsername() + "\n" + user.getDate_joined());
        Commission commission = new Commission("sdad", "smoothsailor23", "bust", "n/a", true);

        UserDAO ud = new UserDAO();
        ud.rundb();
        ud.addUser(user);
        ud.fetchAllUsers();

    }


}





