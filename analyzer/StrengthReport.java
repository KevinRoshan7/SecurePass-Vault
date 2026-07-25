package analyzer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Detailed report returned by the Password Security Analyzer.
 */
public class StrengthReport implements Serializable {
    private static final long serialVersionUID = 1L;

    private int score; // 0 - 100
    private String riskLevel; // VERY_WEAK, WEAK, MEDIUM, STRONG, VERY_STRONG
    private double poolEntropy;
    private double shannonEntropy;
    private boolean hasUppercase;
    private boolean hasLowercase;
    private boolean hasDigit;
    private boolean hasSpecial;
    private boolean hasMinLength;

    private final List<String> warnings = new ArrayList<>();
    private final List<String> suggestions = new ArrayList<>();

    public StrengthReport() {
        // Default constructor
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public double getPoolEntropy() {
        return poolEntropy;
    }

    public void setPoolEntropy(double poolEntropy) {
        this.poolEntropy = poolEntropy;
    }

    public double getShannonEntropy() {
        return shannonEntropy;
    }

    public void setShannonEntropy(double shannonEntropy) {
        this.shannonEntropy = shannonEntropy;
    }

    public boolean isHasUppercase() {
        return hasUppercase;
    }

    public void setHasUppercase(boolean hasUppercase) {
        this.hasUppercase = hasUppercase;
    }

    public boolean isHasLowercase() {
        return hasLowercase;
    }

    public void setHasLowercase(boolean hasLowercase) {
        this.hasLowercase = hasLowercase;
    }

    public boolean isHasDigit() {
        return hasDigit;
    }

    public void setHasDigit(boolean hasDigit) {
        this.hasDigit = hasDigit;
    }

    public boolean isHasSpecial() {
        return hasSpecial;
    }

    public void setHasSpecial(boolean hasSpecial) {
        this.hasSpecial = hasSpecial;
    }

    public boolean isHasMinLength() {
        return hasMinLength;
    }

    public void setHasMinLength(boolean hasMinLength) {
        this.hasMinLength = hasMinLength;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void addSuggestion(String suggestion) {
        this.suggestions.add(suggestion);
    }

    @Override
    public String toString() {
        return String.format("Score: %d/100 | Risk: %s | Entropy: %.2f bits", score, riskLevel, poolEntropy);
    }
}
