package authentication;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import javax.crypto.SecretKey;
import storage.EncryptionUtil;
import storage.FileManager;
import utils.Constants;

/**
 * Handles user authentication, registrations, hashing verification, and session state.
 */
public class AuthenticationManager {

    /**
     * Registers a new user. Returns true if successful, false if username already exists.
     */
    public static boolean register(String username, String password) throws Exception {
        Map<String, User> registry = FileManager.loadUserRegistry();

        if (registry.containsKey(username.toLowerCase().strip())) {
            return false;
        }

        // 1. Generate unique random salt
        byte[] salt = EncryptionUtil.generateSalt();

        // 2. Hash master password with salt
        String passwordHash = hashPassword(password, salt);

        // 3. Save User details
        User newUser = new User(username, passwordHash, salt);
        registry.put(username.toLowerCase().strip(), newUser);
        FileManager.saveUserRegistry(registry);

        return true;
    }

    /**
     * Authenticates a user and starts a new session.
     * Returns a valid Session object if successful, null if failed.
     */
    public static Session login(String username, String password) throws Exception {
        Map<String, User> registry = FileManager.loadUserRegistry();
        String lookupKey = username.toLowerCase().strip();

        if (!registry.containsKey(lookupKey)) {
            return null;
        }

        User user = registry.get(lookupKey);
        String targetHash = hashPassword(password, user.getSalt());

        if (targetHash.equals(user.getPasswordHash())) {
            // Derive AES key from password and salt for session cryptographic operations
            SecretKey sessionKey = EncryptionUtil.deriveKey(password, user.getSalt());
            return new Session(user.getUsername(), sessionKey);
        }

        return null;
    }

    /**
     * Safely logs out the user by clearing sensitive session memory.
     */
    public static void logout(Session session) {
        if (session != null) {
            session.invalidate();
        }
    }

    /**
     * Hashes a password concatenated with a salt using SHA-256.
     */
    private static String hashPassword(String password, byte[] salt) throws Exception {
        try {
            MessageDigest digest = MessageDigest.getInstance(Constants.HASH_ALGORITHM);
            digest.update(salt);
            byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new Exception("Error hashing password", e);
        }
    }
}
