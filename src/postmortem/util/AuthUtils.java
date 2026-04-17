package postmortem.util;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Utility class for password hashing and verification using PBKDF2.
 */
public class AuthUtils {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String PBKDF2_ALGO = "PBKDF2WithHmacSHA256";
    private static final int SALT_LEN = 16;               // bytes
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;            // bits

    /**
     * Hash a password with a randomly generated salt using PBKDF2.
     * Returns a String in format: "Base64(salt):Base64(hash)"
     */
    public static String hashPassword(char[] password) throws Exception {
        byte[] salt = new byte[SALT_LEN];
        RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(password, salt, ITERATIONS, KEY_LENGTH);

        String saltB64 = Base64.getEncoder().encodeToString(salt);
        String hashB64 = Base64.getEncoder().encodeToString(hash);

        return saltB64 + ":" + hashB64;
    }

    /**
     * Verify a password against a stored hash.
     * storedHash should be in format: "Base64(salt):Base64(hash)"
     */
    public static boolean verifyPassword(char[] password, String storedHash) throws Exception {
        String[] parts = storedHash.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid stored hash format");
        }

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
        byte[] actualHash = pbkdf2(password, salt, ITERATIONS, KEY_LENGTH);

        return constantTimeArrayEquals(expectedHash, actualHash);
    }

    /**
     * PBKDF2 key derivation function.
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) throws Exception {
        KeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKeyFactory f = SecretKeyFactory.getInstance(PBKDF2_ALGO);
        return f.generateSecret(spec).getEncoded();
    }

    /**
     * Constant-time array comparison to prevent timing attacks.
     */
    private static boolean constantTimeArrayEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }
}
