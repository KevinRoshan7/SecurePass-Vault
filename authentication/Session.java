package authentication;

import java.time.LocalDateTime;
import javax.crypto.SecretKey;

/**
 * Encapsulates the active login session.
 */
public class Session {
    private final String username;
    private final LocalDateTime loginTime;
    private SecretKey sessionKey; // Retained in volatile RAM, cleared on logout

    public Session(String username, SecretKey sessionKey) {
        this.username = username;
        this.sessionKey = sessionKey;
        this.loginTime = LocalDateTime.now();
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public SecretKey getSessionKey() {
        return sessionKey;
    }

    /**
     * Terminate the session key to prevent memory scraping.
     */
    public void invalidate() {
        this.sessionKey = null;
    }
}
