package models;

import analyzer.StrengthReport;
import breach.BreachResult;

/**
 * Result model representing a generated or analyzed password result.
 */
public class PasswordResult {
    private final String password;
    private final StrengthReport strengthReport;
    private final BreachResult breachResult;
    private final boolean secure;

    public PasswordResult(String password, StrengthReport strengthReport, BreachResult breachResult, boolean secure) {
        this.password = password;
        this.strengthReport = strengthReport;
        this.breachResult = breachResult;
        this.secure = secure;
    }

    public String getPassword() {
        return password;
    }

    public StrengthReport getStrengthReport() {
        return strengthReport;
    }

    public BreachResult getBreachResult() {
        return breachResult;
    }

    public boolean isSecure() {
        return secure;
    }
}
