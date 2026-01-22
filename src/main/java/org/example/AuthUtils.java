package org.example;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class AuthUtils {

    private static byte[] createSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private static byte[] createHash(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password, salt, 50000, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return factory.generateSecret(spec).getEncoded();
    }

    public static boolean validateUserAuth(char[] attempt, byte[] salt, byte[] hash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return Arrays.equals(createHash(attempt, salt), hash);
    }

    public static byte[][] generateSaltAndHash(char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = createSalt();
        byte[] hash = createHash(password, salt);
        return new byte[][]{salt, hash};
    }

}




