package models;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Serializable credential record containing encrypted data for persistent storage.
 */
public class CredentialRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String website;
    private String username;
    private byte[] encryptedPassword;
    private byte[] iv; // AES-GCM IV
    private String category;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public CredentialRecord(String website, String username, byte[] encryptedPassword, byte[] iv, String category,
                            LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.website = website;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.iv = iv;
        this.category = category;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(byte[] encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
