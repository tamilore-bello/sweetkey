package org.example.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class AuthUtils {

    // create the salt for password hashing
    private static byte[] createSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    // creates a unique hash that can be used in combination with salt to verify whether
    // the password used to create the hash and a new password are the same
    private static byte[] createHash(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password, salt, 50000, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return factory.generateSecret(spec).getEncoded();
    }

    // validates whether the password used to create the hash and a new password are the same, aka if
    // the tentative password " attempt " is valid
    public static boolean validateUserAuth(char[] attempt, byte[] salt, byte[] hash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return Arrays.equals(createHash(attempt, salt), hash);
    }

    // generates the salt and hash to be stored in the database for a new User
    public static byte[][] generateSaltAndHash(char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = createSalt();
        byte[] hash = createHash(password, salt);
        return new byte[][]{salt, hash};
    }

}




