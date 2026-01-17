
import java.sql.*;
import java.util.*;
import java.util.Date;

import org.example.*;

void main() {
     UserDAO ud = new UserDAO();
     ud.rundb();
}

public void welcomeUser() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("ENTER USERNAME: ");
    String username = scanner.nextLine();
    System.out.println("ENTER PASSWORD: ");
    String password = scanner.nextLine();
}

public void printAllUsers() {
    UserDAO ud = new UserDAO();
    for (User i : ud.fetchAllUsers()) {
        System.out.println(i);
    }
}

public void printAllComms() {
    UserDAO ud = new UserDAO();
    for (Commission i : ud.fetchAllComms()) {
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








