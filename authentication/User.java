package authentication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import models.CredentialRecord;

/**
 * Serializable User entity representing account registration details and vault data.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String username;
    private final String passwordHash; // SHA-256 hash of (master password + salt)
    private final byte[] salt; // Unique salt for hashing and key derivation
    private List<CredentialRecord> credentials; // Encrypted credentials list

    public User(String username, String passwordHash, byte[] salt) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.credentials = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public byte[] getSalt() {
        return salt;
    }

    public List<CredentialRecord> getCredentials() {
        if (credentials == null) {
            credentials = new ArrayList<>();
        }
        return credentials;
    }

    public void setCredentials(List<CredentialRecord> credentials) {
        this.credentials = credentials;
    }
}
