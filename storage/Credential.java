package storage;

import java.time.LocalDateTime;

/**
 * Domain model representing a cleartext credential record during active runtime.
 */
public class Credential {
    private String website;
    private String username;
    private String password; // Cleartext password (decrypted in session)
    private String category;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public Credential(String website, String username, String password, String category,
                      LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.website = website;
        this.username = username;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @Override
    public String toString() {
        return String.format("[%s] Website: %s | User: %s", category, website, username);
    }
}
