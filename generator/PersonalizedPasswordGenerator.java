package generator;

import analyzer.PasswordAnalyzer;
import analyzer.StrengthReport;
import breach.BreachChecker;
import breach.BreachResult;
import java.util.List;
import models.PasswordResult;

/**
 * Orchestrator class for personalized password generation.
 */
public class PersonalizedPasswordGenerator {

    private final PasswordComposer composer;
    private final PasswordValidator validator;
    private final PasswordAnalyzer analyzer;
    private final BreachChecker breachChecker;

    public PersonalizedPasswordGenerator() {
        this.composer = new PasswordComposer();
        this.validator = new PasswordValidator();
        this.analyzer = new PasswordAnalyzer();
        this.breachChecker = new BreachChecker();
    }

    /**
     * Generates a personalized password that is guaranteed to be strong and unbreached.
     */
    public PasswordResult generatePassword(int length, List<String> preferredWords) {
        String candidate = null;
        boolean isSecure = false;
        int attempts = 0;
        int maxAttempts = 15;

        // Loop until a secure password is found or max attempts reached
        while (!isSecure && attempts < maxAttempts) {
            candidate = composer.compose(length, preferredWords);
            isSecure = validator.isSecure(candidate, analyzer, breachChecker);
            attempts++;
        }

        // Run final analysis on whatever the candidate is
        StrengthReport report = analyzer.analyze(candidate);
        BreachResult breachResult = breachChecker.checkPassword(candidate);

        return new PasswordResult(candidate, report, breachResult, isSecure);
    }
}
