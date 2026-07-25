package dashboard;

import analyzer.PasswordAnalyzer;
import analyzer.StrengthReport;
import breach.BreachChecker;
import breach.BreachResult;
import java.util.ArrayList;
import java.util.List;
import models.DashboardData;
import similarity.PasswordSimilarityDetector;
import similarity.SimilarityResult;
import storage.Credential;

/**
 * Aggregates security metrics and suggestions into a dashboard view.
 */
public class Dashboard {

    private final PasswordAnalyzer analyzer;
    private final BreachChecker breachChecker;
    private final PasswordSimilarityDetector similarityDetector;

    public Dashboard() {
        this.analyzer = new PasswordAnalyzer();
        this.breachChecker = new BreachChecker();
        this.similarityDetector = new PasswordSimilarityDetector();
    }

    /**
     * Compiles detailed security metrics across all credentials in active vault memory.
     */
    public DashboardData compileData(List<Credential> credentials) {
        if (credentials == null || credentials.isEmpty()) {
            return new DashboardData(0, 0, 0, 0, 0, 0.0, 100, new ArrayList<>(), List.of("No credentials stored. Start by adding one!"));
        }

        int total = credentials.size();
        int weakCount = 0;
        int mediumCount = 0;
        int strongCount = 0;
        int breachedCount = 0;
        double totalEntropy = 0;

        List<StrengthReport> reports = new ArrayList<>();
        List<BreachResult> breaches = new ArrayList<>();

        for (Credential cred : credentials) {
            // Analyze strength
            StrengthReport report = analyzer.analyze(cred.getPassword());
            reports.add(report);

            if (report.getScore() < 60) {
                weakCount++;
            } else if (report.getScore() < 80) {
                mediumCount++;
            } else {
                strongCount++;
            }

            totalEntropy += report.getPoolEntropy();

            // Analyze breach status
            BreachResult breach = breachChecker.checkPassword(cred.getPassword());
            breaches.add(breach);
            if (breach.isBreached()) {
                breachedCount++;
            }
        }

        // Run similarity detection
        List<SimilarityResult> similarityResults = similarityDetector.detectSimilarities(credentials);

        // Compute overall score
        int score = SecurityScore.calculateScore(reports, breaches, similarityResults);

        // Generate custom security suggestions
        List<String> recommendations = new ArrayList<>();
        if (breachedCount > 0) {
            recommendations.add(String.format("CRITICAL: %d of your passwords have been leaked in public breaches. Replace them immediately!", breachedCount));
        }
        if (weakCount > 0) {
            recommendations.add(String.format("You have %d weak passwords (score < 60). Replace them with secure random passwords.", weakCount));
        }
        
        // Count exact duplicates
        int identicalCount = 0;
        for (SimilarityResult s : similarityResults) {
            if (s.getSimilarityPercentage() == 100.0) {
                identicalCount++;
            }
        }
        if (identicalCount > 0) {
            recommendations.add(String.format("Identical password reuse detected in %d credentials. Ensure each account has a unique password.", identicalCount));
        } else if (similarityResults.size() > 0) {
            recommendations.add("Pattern variations detected (similar passwords). Use completely distinct phrases for each website.");
        }

        if (score >= 90) {
            recommendations.add("Excellent! Your overall vault security hygiene is strong.");
        } else if (score >= 70) {
            recommendations.add("Good. Secure the highlighted weak passwords to reach a perfect score.");
        } else {
            recommendations.add("WARNING: Your vault is in a high-risk security state. Address breaches and duplicates immediately.");
        }

        double avgEntropy = totalEntropy / total;

        return new DashboardData(
                total,
                weakCount,
                mediumCount,
                strongCount,
                breachedCount,
                avgEntropy,
                score,
                similarityResults,
                recommendations
        );
    }
}
