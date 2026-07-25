package similarity;

import storage.Credential;

/**
 * Result model representing a similarity match between two vault credentials.
 */
public class SimilarityResult {

    private final Credential credential1;
    private final Credential credential2;
    private final int distance;
    private final double similarityPercentage;
    private final String riskLevel;
    private final String recommendation;

    public SimilarityResult(Credential credential1, Credential credential2, int distance, 
                            double similarityPercentage, String riskLevel, String recommendation) {
        this.credential1 = credential1;
        this.credential2 = credential2;
        this.distance = distance;
        this.similarityPercentage = similarityPercentage;
        this.riskLevel = riskLevel;
        this.recommendation = recommendation;
    }

    public Credential getCredential1() {
        return credential1;
    }

    public Credential getCredential2() {
        return credential2;
    }

    public int getDistance() {
        return distance;
    }

    public double getSimilarityPercentage() {
        return similarityPercentage;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getRecommendation() {
        return recommendation;
    }
}
