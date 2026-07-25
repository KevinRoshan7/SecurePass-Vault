package storage;

import authentication.User;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;
import models.CredentialRecord;

/**
 * Manages active user credentials: adds, views, updates, deletes, and searches.
 * Handles automatic encryption/decryption of passwords during file sync.
 */
public class VaultManager {

    private final String username;
    private final SecretKey sessionKey;
    private final List<Credential> credentials;

    public VaultManager(String username, SecretKey sessionKey) {
        this.username = username;
        this.sessionKey = sessionKey;
        this.credentials = new ArrayList<>();
        loadVault();
    }

    /**
     * Decrypts all saved records from the database and loads them into memory.
     */
    private void loadVault() {
        Map<String, User> registry = FileManager.loadUserRegistry();
        User user = registry.get(username);
        if (user == null) return;

        List<CredentialRecord> records = user.getCredentials();
        for (CredentialRecord record : records) {
            try {
                String decryptedPassword = EncryptionUtil.decrypt(
                        record.getEncryptedPassword(),
                        sessionKey,
                        record.getIv()
                );
                
                Credential credential = new Credential(
                        record.getWebsite(),
                        record.getUsername(),
                        decryptedPassword,
                        record.getCategory(),
                        record.getCreatedDate(),
                        record.getModifiedDate()
                );
                credentials.add(credential);
            } catch (Exception e) {
                // If a single decryption fails, we print a warning and skip (perhaps corrupted key or data)
                System.err.println("Warning: Decryption failed for record " + record.getWebsite() + " (" + e.getMessage() + ")");
            }
        }
    }

    /**
     * Syncs memory state back to disk by encrypting all records.
     */
    public synchronized void syncToDisk() throws IOException {
        Map<String, User> registry = FileManager.loadUserRegistry();
        User user = registry.get(username);
        if (user == null) {
            throw new IOException("User data not found in registry during save operation.");
        }

        List<CredentialRecord> encryptedRecords = new ArrayList<>();
        for (Credential credential : credentials) {
            try {
                byte[] iv = EncryptionUtil.generateIv();
                byte[] encryptedPass = EncryptionUtil.encrypt(
                        credential.getPassword(),
                        sessionKey,
                        iv
                );

                CredentialRecord record = new CredentialRecord(
                        credential.getWebsite(),
                        credential.getUsername(),
                        encryptedPass,
                        iv,
                        credential.getCategory(),
                        credential.getCreatedDate(),
                        credential.getModifiedDate()
                );
                encryptedRecords.add(record);
            } catch (Exception e) {
                throw new IOException("Cryptographic failure during save: " + e.getMessage(), e);
            }
        }

        user.setCredentials(encryptedRecords);
        registry.put(username, user);
        FileManager.saveUserRegistry(registry);
    }

    /**
     * Adds a new credential and commits to disk.
     */
    public void addCredential(Credential credential) throws IOException {
        credentials.add(credential);
        syncToDisk();
    }

    /**
     * Updates an existing credential and commits to disk.
     */
    public void updateCredential(int index, String password, String category) throws IOException {
        if (index < 0 || index >= credentials.size()) {
            throw new IndexOutOfBoundsException("Invalid credential index");
        }
        Credential cred = credentials.get(index);
        cred.setPassword(password);
        cred.setCategory(category);
        cred.setModifiedDate(LocalDateTime.now());
        syncToDisk();
    }

    /**
     * Deletes a credential and commits to disk.
     */
    public void deleteCredential(int index) throws IOException {
        if (index < 0 || index >= credentials.size()) {
            throw new IndexOutOfBoundsException("Invalid credential index");
        }
        credentials.remove(index);
        syncToDisk();
    }

    /**
     * Returns a copy of the loaded credentials list.
     */
    public List<Credential> getCredentials() {
        return new ArrayList<>(credentials);
    }

    /**
     * Searches credentials case-insensitively by website, username, or category.
     */
    public List<Credential> searchCredentials(String query) {
        if (query == null || query.isBlank()) {
            return getCredentials();
        }
        String lowerQuery = query.toLowerCase().strip();
        List<Credential> matches = new ArrayList<>();
        for (Credential cred : credentials) {
            if (cred.getWebsite().toLowerCase().contains(lowerQuery) ||
                cred.getUsername().toLowerCase().contains(lowerQuery) ||
                cred.getCategory().toLowerCase().contains(lowerQuery)) {
                matches.add(cred);
            }
        }
        return matches;
    }
}
