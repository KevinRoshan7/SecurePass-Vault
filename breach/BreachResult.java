package breach;

import java.io.Serializable;

/**
 * Result model representing breach analysis for a password.
 */
public class BreachResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean breached;
    private final int breachCount;
    private final String riskLevel;
    private final String recommendation;

    public BreachResult(boolean breached, int breachCount, String riskLevel, String recommendation) {
        this.breached = breached;
        this.breachCount = breachCount;
        this.riskLevel = riskLevel;
        this.recommendation = recommendation;
    }

    public boolean isBreached() {
        return breached;
    }

    public int getBreachCount() {
        return breachCount;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getRecommendation() {
        return recommendation;
    }

    @Override
    public String toString() {
        return String.format("Breached: %b (Count: %d) | Risk: %s", breached, breachCount, riskLevel);
    }
}
