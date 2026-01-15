
import java.sql.*;
import java.util.*;
import org.example.*;

void main() {
    UserDAO ud = new UserDAO();
    ud.rundb();
    ud.fetchAllUsers();
}

public void addCommission() {
    User user = new User("jess", "jessisawesome");
    Scanner scanner = new Scanner(System.in);
    String username;
    String password;
    Commission commission = new Commission(
            user.getUuid(),
            "smoothsailor23",
            "bust",
            "n/a",
            true);
    UserDAO ud = new UserDAO();

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
    ud.addUser(user);
}








