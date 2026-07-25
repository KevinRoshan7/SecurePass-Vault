package analyzer;

/**
 * Main Password Security Analyzer class that builds a StrengthReport.
 */
public class PasswordAnalyzer {

    private final DictionaryChecker dictionaryChecker;
    private final PatternChecker patternChecker;

    public PasswordAnalyzer() {
        this.dictionaryChecker = new DictionaryChecker();
        this.patternChecker = new PatternChecker();
    }

    /**
     * Analyzes a password and returns a comprehensive StrengthReport.
     */
    public StrengthReport analyze(String password) {
        StrengthReport report = new StrengthReport();

        if (password == null || password.isEmpty()) {
            report.setScore(0);
            report.setRiskLevel("VERY_WEAK");
            report.addWarning("Password is empty");
            report.addSuggestion("Please enter a valid password.");
            return report;
        }

        int length = password.length();

        // 1. Basic Type Auditing
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (int i = 0; i < length; i++) {
            char c = password.charAt(i);
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                hasSpecial = true;
            }
        }

        report.setHasUppercase(hasUpper);
        report.setHasLowercase(hasLower);
        report.setHasDigit(hasDigit);
        report.setHasSpecial(hasSpecial);
        report.setHasMinLength(length >= 8);

        // 2. Entropy Calculation
        double poolEntropy = EntropyCalculator.calculatePoolEntropy(password);
        double shannonEntropy = EntropyCalculator.calculateShannonEntropy(password);
        report.setPoolEntropy(poolEntropy);
        report.setShannonEntropy(shannonEntropy);

        // 3. Scoring Math
        int score = 0;

        // Length sub-score (up to 50 pts)
        if (length < 8) {
            score += 10;
            report.addWarning("Length is under 8 characters (highly insecure).");
            report.addSuggestion("Make password at least 12 characters long.");
        } else if (length < 12) {
            score += 25;
            report.addWarning("Length is short (8-11 characters).");
            report.addSuggestion("Extend password length to 12+ characters.");
        } else if (length < 16) {
            score += 40;
        } else {
            score += 50;
        }

        // Type variety sub-score (up to 30 pts)
        if (hasUpper) score += 7;
        else {
            report.addWarning("Does not contain uppercase letters.");
            report.addSuggestion("Add at least one uppercase letter (A-Z).");
        }

        if (hasLower) score += 7;
        else {
            report.addWarning("Does not contain lowercase letters.");
            report.addSuggestion("Add at least one lowercase letter (a-z).");
        }

        if (hasDigit) score += 8;
        else {
            report.addWarning("Does not contain digits.");
            report.addSuggestion("Add at least one digit (0-9).");
        }

        if (hasSpecial) score += 8;
        else {
            report.addWarning("Does not contain special characters.");
            report.addSuggestion("Add at least one special character (e.g. !, @, #, $, %).");
        }

        // Entropy sub-score bonus (up to 20 pts)
        if (poolEntropy >= 80) {
            score += 20;
        } else if (poolEntropy >= 60) {
            score += 15;
        } else if (poolEntropy >= 35) {
            score += 10;
        }

        // 4. Pattern and Dictionary Penalties
        boolean isCommon = dictionaryChecker.isCommonPassword(password);
        boolean containsWord = dictionaryChecker.containsDictionaryWord(password);
        boolean repeatedChars = patternChecker.hasRepeatedCharacters(password);
        boolean sequentialChars = patternChecker.hasSequentialCharacters(password);
        boolean keyboardPattern = patternChecker.hasKeyboardPattern(password);

        if (isCommon) {
            score = 0; // Absolute fail
            report.addWarning("CRITICAL: This is a widely known common password.");
            report.addSuggestion("Change this password immediately to a unique one.");
        } else {
            if (containsWord) {
                String word = dictionaryChecker.getMatchingDictionaryWord(password);
                score -= 15;
                report.addWarning("Contains English dictionary word: \"" + word + "\".");
                report.addSuggestion("Avoid plain dictionary words or names.");
            }
            if (repeatedChars) {
                score -= 10;
                report.addWarning("Contains 3+ repeated characters consecutively.");
                report.addSuggestion("Avoid repeated characters (e.g., 'aaa').");
            }
            if (sequentialChars) {
                score -= 10;
                report.addWarning("Contains sequential character sequences (e.g., '123', 'abc').");
                report.addSuggestion("Avoid sequential letters/numbers.");
            }
            if (keyboardPattern) {
                score -= 10;
                report.addWarning("Contains linear keyboard row pattern (e.g., 'qwe').");
                report.addSuggestion("Avoid simple keyboard slide patterns.");
            }
        }

        // Clamp final score
        score = Math.max(0, Math.min(100, score));
        report.setScore(score);

        // Define Risk Level
        if (score < 35) {
            report.setRiskLevel("VERY_WEAK");
        } else if (score < 60) {
            report.setRiskLevel("WEAK");
        } else if (score < 80) {
            report.setRiskLevel("MEDIUM");
        } else if (score < 90) {
            report.setRiskLevel("STRONG");
        } else {
            report.setRiskLevel("VERY_STRONG");
        }

        return report;
    }
}
