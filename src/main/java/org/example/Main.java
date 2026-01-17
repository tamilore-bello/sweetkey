
import java.sql.*;
import java.util.*;
import java.util.Date;

import org.example.*;

void main() {
    // get the database up and running ! we create a UserDAO object, through which we can access various
    // database methods. The first thing we do is run the database.
     UserDAO ud = new UserDAO();
     ud.rundb();

     // for prototyping and development purposes, we use the internal methods to print all users and all
     // commissions
     printAllUsers();
     printAllComms();

     welcomeUser();
}

// all the current user to log in
public void welcomeUser() {
    Scanner scanner = new Scanner(System.in);
    UserDAO ud = new UserDAO();

    System.out.println("------ LOGIN ------ ");
    System.out.println("ENTER USERNAME: ");
    String username = scanner.nextLine();
    System.out.println("ENTER PASSWORD: ");
    String password = scanner.nextLine();

    User user = ud.fetchUser(username, password);
    if (user != null) {
        System.out.println("\nWELCOME "+user.getUsername());
        System.out.println(user);
    } else {
        System.out.println("INCORRECT CREDENTIALS");
    }
}

@InternalMethod
@Deprecated
public void printAllUsers() {
    DevUtils devUtils = new DevUtils();
    System.out.println("------ ALL USERS ------ ");
    for (User i : devUtils.fetchAllUsers_INTERNAL()) {
        System.out.println(i);
    }
}

@InternalMethod
@Deprecated
public void printAllComms() {
    DevUtils devUtils = new DevUtils();
    System.out.println("------ ALL COMMISSIONS ------ ");
    for (Commission i : devUtils.fetchAllComms_INTERNAL()) {
        System.out.println(i);
    }
}

public void addCommission() {
    Scanner scanner = new Scanner(System.in);
    UserDAO ud = new UserDAO();

    System.out.println("ENTER THE USER ID OF THE ARTIST: ");
    String ai = scanner.nextLine();

    System.out.println("ENTER THE COMMISSIONER'S HANDLE: ");
    String ch = scanner.nextLine();
    System.out.println("ENTER THE SIZE OF THE COMMISSION: ");
    String s = scanner.nextLine();
    System.out.println("ENTER THE LINK TO THE REFERENCE: ");
    String r = scanner.nextLine();
    System.out.println("HAS PAYMENT BEEN RECEIVED?: ");
    Boolean b = scanner.nextBoolean();

    Commission commission = new Commission( ai,
            ch,
            s,
            r,
            b);
    ud.addComm(commission);
}

public void addUser() {
    Scanner scanner = new Scanner(System.in);
    String username;
    String password;

    System.out.println("ENTER USERNAME");
    username = scanner.nextLine();
    System.out.println("ENTER PASSWORD ");
    password = scanner.nextLine();

    User user = new User(username, password);

    UserDAO ud = new UserDAO();
    ud.rundb();
    ud.addUser(user);

}








