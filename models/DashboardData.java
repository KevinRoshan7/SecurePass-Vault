package models;

import java.util.ArrayList;
import java.util.List;
import similarity.SimilarityResult;

/**
 * Data structure holding summarized password metrics for rendering.
 */
public class DashboardData {
    private final int totalCredentials;
    private final int weakCount;
    private final int mediumCount;
    private final int strongCount;
    private final int breachedCount;
    private final double averageEntropy;
    private final int overallScore;
    private final List<SimilarityResult> similarityResults;
    private final List<String> recommendations;

    public DashboardData(int totalCredentials, int weakCount, int mediumCount, int strongCount, int breachedCount,
                         double averageEntropy, int overallScore, List<SimilarityResult> similarityResults,
                         List<String> recommendations) {
        this.totalCredentials = totalCredentials;
        this.weakCount = weakCount;
        this.mediumCount = mediumCount;
        this.strongCount = strongCount;
        this.breachedCount = breachedCount;
        this.averageEntropy = averageEntropy;
        this.overallScore = overallScore;
        this.similarityResults = similarityResults != null ? similarityResults : new ArrayList<>();
        this.recommendations = recommendations != null ? recommendations : new ArrayList<>();
    }

    public int getTotalCredentials() {
        return totalCredentials;
    }

    public int getWeakCount() {
        return weakCount;
    }

    public int getMediumCount() {
        return mediumCount;
    }

    public int getStrongCount() {
        return strongCount;
    }

    public int getBreachedCount() {
        return breachedCount;
    }

    public double getAverageEntropy() {
        return averageEntropy;
    }

    public int getOverallScore() {
        return overallScore;
    }

    public List<SimilarityResult> getSimilarityResults() {
        return similarityResults;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }
}
