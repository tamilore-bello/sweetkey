package org.example.dev;

import org.example.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

public class CLIController {
    UserDAO ud = new UserDAO();
    DevUtils devUtils = new DevUtils();


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

    @InternalMethod
    @Deprecated
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

    @InternalMethod
    @Deprecated
    public void addUser() throws NoSuchAlgorithmException, InvalidKeySpecException {
        Scanner scanner = new Scanner(System.in);
        String username;
        String password;

        System.out.println("ENTER USERNAME");
        username = scanner.nextLine();
        System.out.println("ENTER PASSWORD ");
        password = scanner.nextLine();

        User user = new User(username, "");
        byte[][] saltedMix = AuthUtils.generateSaltAndHash(password.toCharArray());
        ud.addUser(user, saltedMix[0], saltedMix[1]);
    }
}
