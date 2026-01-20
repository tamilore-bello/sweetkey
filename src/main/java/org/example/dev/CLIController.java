package org.example.dev;

import org.example.*;

import java.util.Scanner;

public class CLIController {
    UserDAO ud = new UserDAO();
    DevUtils devUtils = new DevUtils();


    // all the current user to log in
    @Deprecated
    public void welcomeUser() {
        ud.rundb();
        Scanner scanner = new Scanner(System.in);

        System.out.println("------ LOGIN ------ ");
        System.out.println("ENTER USERNAME: ");
        String username = scanner.nextLine();
        System.out.println("ENTER PASSWORD: ");
        String password = scanner.nextLine();

        User user = ud.fetchUser(username, password);
        if (user != null) {
            System.out.println("\nWELCOME "+user.getUsername());
            System.out.println(user);
            addCommission(user);
        } else {
            System.out.println("INCORRECT CREDENTIALS");
        }
    }


    @InternalMethod
    @Deprecated
    public void printAllUsers() {
        System.out.println("------ ALL USERS ------ ");
        for (User i : devUtils.fetchAllUsers_INTERNAL()) {
            System.out.println(i);
        }
    }

    @InternalMethod
    @Deprecated
    public User[] returnAllUsers() {
        return devUtils.fetchAllUsers_INTERNAL().toArray(new User[0]);
    }


    @InternalMethod
    @Deprecated
    public void printAllComms() {
        System.out.println("------ ALL COMMISSIONS ------ ");
        for (Commission i : devUtils.fetchAllComms_INTERNAL()) {
            System.out.println(i);
        }
    }

    // TODO
    // if a user object is fabricated then this is no longer secure and we can add random commissions to users.
    public void addCommission(User user) {
        Scanner scanner = new Scanner(System.in);
        String ai = user.getId();
        System.out.println("ENTER THE COMMISSIONER'S HANDLE: ");
        String ch = scanner.nextLine();
        System.out.println("ENTER THE SIZE OF THE COMMISSION: ");
        String s = scanner.nextLine();
        System.out.println("ENTER THE LINK TO THE REFERENCE: ");
        String r = scanner.nextLine();
        System.out.println("HAS PAYMENT BEEN RECEIVED?: ");
        Boolean b = scanner.nextBoolean();

        // OF COURSE, we can add more fields and scan in responses.
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
        ud.addUser(user);
    }
}
