package org.example;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewModel {
    UserDAO ud = new UserDAO();
    User user;

    // USER OPERATIONS ------------------------------------------------------------------------------------
    // return the current User.
    public User getCurrentUser() {
      return user;
    }

    // add a user to the database
    public boolean addUser(String username, String email, char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // if any of the sign-up details are invalid, fail.
        if (isInvalidSignUpDetails(username, email, password)) return false;

        // Add the passed User to the database.
        User user = new User(username, email);
        byte[][] saltedMix = AuthUtils.generateSaltAndHash(password);
        ud.addUser(user, saltedMix[0], saltedMix[1]);
        return true;
    }

    // authenticate a User.
    public boolean welcomeUser(String username, char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
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

    // change a username
    public boolean updateUsername(String newUsername) {
        if (isUsernameValid(newUsername) && !(newUsername.equals(user.getUsername()))) {
            if (ud.updateUsername(user.getId(), newUsername)) {
                user = ud.fetchUser(newUsername);
                return true;
            }
        }
        return false;
    }

    // change an email
    public boolean updateEmail(String newEmail) {
        if (isEmailValid(newEmail) && !(newEmail.equals(user.getEmail()))) {
            if (ud.updateEmail(user.getId(), newEmail)) {
                user.setEmail(newEmail);
                return true;
            }
        }
        return false;
    }

    // delete a User
    public void deleteUser() {
        ud.deleteUser(user.getId());
        user = null;
    }

    // COMMISSION OPERATIONS ------------------------------------------------------------------------------------
    // add a commission to the database.
    public boolean addCommission(Commission commission) {
        if (!isInvalidCommission(commission)) {
            ud.addComm(commission);
            return true;
        } else {
            return false;
        }
    }

    // get all Commissions for a User
    public List<Commission> getCurrentUserCommission(int order) {
        return ud.fetchAllUserComms(user.getId(), order);
    }


    // AUTH / VALIDATION  ------------------------------------------------------------------------------------
    // validate sign-up details
    private boolean isInvalidSignUpDetails(String username, String email, char[] password) {
        if (isUsernameValid(username) == false)
            return true;
        if (email.isBlank() || password.length == 0)
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
        return false;
    }

    private boolean isUsernameValid (String username) {
        if ((ud.fetchUser(username) != null))
            return false;
        if (username.isBlank())
            return false;
        return true;
    }

    public boolean isEmailValid(String email) {
        if (email.isBlank())
            return false;
        if (!email.contains("@"))
            return false;
        return true;
    }

    // validate commission details
    private boolean isInvalidCommission(Commission commission) {
        if (commission.getCommissioner_handle().isBlank() || commission.getDescription().isBlank())
            return true;
        return false;
    }

    // Validate that an entered cost is a Double value
    public boolean validCost(String c) {
        try {
            Double.parseDouble(c);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    // STATS  ------------------------------------------------------------------------------------
    public int lateCommissionQty() {
        return ud.countAllUserLateCommissions(user.getId());
    }

    public double earningsEver() {
        return ud.amountEarned(user.getId(), 3);
    }
    public double earningsYear() {
        return ud.amountEarned(user.getId(), 2);
    }
    public double earningsMonth() {
        return ud.amountEarned(user.getId(), 1);
    }
    public double earningsLast7() {
        return ud.amountEarned(user.getId(), 0);
    }

    public Date getDateJoined() {
        return user.getDate_joined();
    }

}
