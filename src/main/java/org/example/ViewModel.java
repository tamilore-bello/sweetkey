package org.example;

import org.example.dev.DevUtils;
import org.example.dev.InternalMethod;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Scanner;

public class ViewModel {
    UserDAO ud = new UserDAO();
    DevUtils devUtils = new DevUtils();
    User user;
    public User getCurrentUser() {
      return user;
    }
    public boolean welcomeUser(String username, char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        ud.rundb();
        byte[][] saltyMix = ud.fetchSaltAndHash(username);
        if (saltyMix == null) return false;
        if (AuthUtils.validateUserAuth(password, saltyMix[0], saltyMix[1])) {
            user = ud.fetchUser(username);
            return true;
        } else {
            return false;
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

    public boolean addUser(String username, String email, char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        ud.rundb();
        if (isInvalidSignUpDetails(username, email, password)) return false;

        User user = new User(username, email);
        byte[][] saltedMix = AuthUtils.generateSaltAndHash(password);
        ud.addUser(user, saltedMix[0], saltedMix[1]);
        System.out.print(4);
        return true;
    }

    public ArrayList<Commission> getCurrentUserCommission() {
        return ud.fetchAllUserComms(user.getId());
    }

    private boolean isInvalidSignUpDetails(String username, String email, char[] password) {
        if (username.isEmpty() || email.isEmpty() || password.length == 0)
            return true;
        if (!email.contains("@"))
            return true;
        boolean hasSpecial = false;
        for (char c : password)
            if (("!@#$%^&*").contains(Character.toString(c)))
                hasSpecial = true;
        if (!hasSpecial)
            return true;
        if (password.length < 8)
            return true;
        if (!(ud.fetchUser(username) == null))
            return true;
        return false;
    }



}
