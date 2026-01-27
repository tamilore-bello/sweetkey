package org.example;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

public class ViewModel {
    UserDAO ud = new UserDAO();
    User user;

    // return the current User.
    public User getCurrentUser() {
      return user;
    }

    // authenticate a User.
    public boolean welcomeUser(String username, char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        ud.rundb();

        // fetch the salt and hash for an entered username, if there is none, return fail
        byte[][] saltyMix = ud.fetchSaltAndHash(username);
        if (saltyMix == null) return false;

        // if on validation, the entered password is valid with the received hash, return pass, otherwise, fail
        if (AuthUtils.validateUserAuth(password, saltyMix[0], saltyMix[1])) {
            user = ud.fetchUser(username);
            return true;
        } else {
            return false;
        }
    }


   // add a commission to the database.
    public void addCommission(Commission commission) {
        ud.addComm(commission);
    }

    // add a user to the database
    public boolean addUser(String username, String email, char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        ud.rundb();

        // if any of the sign-up details are invalid, fail.
        if (isInvalidSignUpDetails(username, email, password)) return false;

        // Add the passed User to the database.
        User user = new User(username, email);
        byte[][] saltedMix = AuthUtils.generateSaltAndHash(password);
        ud.addUser(user, saltedMix[0], saltedMix[1]);
        return true;
    }

    // get all Commissions for a User
    public ArrayList<Commission> getCurrentUserCommission() {
        return ud.fetchAllUserComms(user.getId());
    }

    // validate sign-up details
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
