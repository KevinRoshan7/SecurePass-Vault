package dashboard;

import analyzer.StrengthReport;
import breach.BreachResult;
import java.util.List;
import similarity.SimilarityResult;

/**
 * Computes the overall safety score (0-100) of the vault credentials.
 */
public class SecurityScore {

    /**
     * Calculates security score by averaging strengths and deducting for breaches/reuse.
     */
    public static int calculateScore(List<StrengthReport> reports, 
                                      List<BreachResult> breaches, 
                                      List<SimilarityResult> similarities) {
        if (reports == null || reports.isEmpty()) {
            return 100; // Perfect score if nothing is stored (clean slate)
        }

        // 1. Base Score: Average strength score of all credentials
        double strengthSum = 0;
        for (StrengthReport r : reports) {
            strengthSum += r.getScore();
        }
        double baseScore = strengthSum / reports.size();

        // 2. Penalties
        int breachPenalty = 0;
        int reusePenalty = 0;

        // Leak/Breach Penalty: -20 pts per breached password, max penalty 50 pts
        for (BreachResult b : breaches) {
            if (b.isBreached()) {
                breachPenalty += 20;
            }
        }
        breachPenalty = Math.min(50, breachPenalty);

        // Similarity/Reuse Penalty
        for (SimilarityResult s : similarities) {
            if (s.getSimilarityPercentage() == 100.0) {
                reusePenalty += 15; // Identical reuse
            } else {
                reusePenalty += 8;  // Highly similar
            }
        }
        reusePenalty = Math.min(40, reusePenalty);

        // Calculate final score
        double finalScore = baseScore - breachPenalty - reusePenalty;
        return Math.max(0, Math.min(100, (int) Math.round(finalScore)));
    }
}
