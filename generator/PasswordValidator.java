package generator;

import analyzer.PasswordAnalyzer;
import analyzer.StrengthReport;
import breach.BreachChecker;
import breach.BreachResult;

/**
 * Validation checker for verifying generated password security levels.
 */
public class PasswordValidator {

    /**
     * Checks if a generated password meets minimal safety standards: score >= 60 and not pwned.
     */
    public boolean isSecure(String password, PasswordAnalyzer analyzer, BreachChecker breachChecker) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        // Check Strength Score
        StrengthReport report = analyzer.analyze(password);
        if (report.getScore() < 60) {
            return false;
        }

        // Check Breach Database (HIBP)
        BreachResult breachResult = breachChecker.checkPassword(password);
        if (breachResult.isBreached()) {
            return false;
        }

        return true;
    }
}
