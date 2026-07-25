package storage;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import utils.Constants;

/**
 * Cryptographic utility class providing AES-GCM encryption/decryption and PBKDF2 key derivation.
 */
public final class EncryptionUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private EncryptionUtil() {
        // Prevent instantiation
    }

    /**
     * Generates a cryptographically secure random salt of standard length.
     */
    public static byte[] generateSalt() {
        byte[] salt = new byte[Constants.SALT_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }

    /**
     * Generates a cryptographically secure random Initialization Vector (IV) for AES-GCM.
     */
    public static byte[] generateIv() {
        byte[] iv = new byte[Constants.GCM_IV_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(iv);
        return iv;
    }

    /**
     * Derives a 256-bit AES SecretKey from a password and salt using PBKDF2.
     */
    public static SecretKey deriveKey(String password, byte[] salt) throws Exception {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(Constants.KDF_ALGORITHM);
            KeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    Constants.PBKDF2_ITERATIONS,
                    Constants.PBKDF2_KEY_LENGTH_BITS
            );
            SecretKey tmp = factory.generateSecret(spec);
            return new SecretKeySpec(tmp.getEncoded(), Constants.KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new Exception("Error deriving key using PBKDF2", e);
        }
    }

    /**
     * Encrypts plaintext string using AES-GCM with the derived key and provided IV.
     */
    public static byte[] encrypt(String plaintext, SecretKey key, byte[] iv) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(Constants.CIPHER_ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(Constants.GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            return cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new Exception("Error during AES encryption", e);
        }
    }

    /**
     * Decrypts ciphertext byte array using AES-GCM with the derived key and IV.
     */
    public static String decrypt(byte[] ciphertext, SecretKey key, byte[] iv) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(Constants.CIPHER_ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(Constants.GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            byte[] decryptedBytes = cipher.doFinal(ciphertext);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new Exception("Error during AES decryption: Make sure your master key is correct.", e);
        }
    }
}
