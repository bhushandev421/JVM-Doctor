package com.jvm.doctor.rules;

import com.jvm.doctor.model.DiagnosisContext;
import com.jvm.doctor.model.Finding;
import com.jvm.doctor.model.ThreadInfo;

import java.util.Collections;
import java.util.List;

public class BlockedRatioRule implements Rule {

    @Override
    public String getId() {
        return "THREAD_BLOCKED_RATIO_HIGH";
    }

    @Override
    public List<Finding> evaluate(DiagnosisContext context) {
        ThreadInfo info = context.getThreadInfo();
        if (info == null || info.getTotalThreads() == 0)
            return Collections.emptyList();

        int blocked = info.getStateCounts().getOrDefault("BLOCKED", 0);
        double ratio = (double) blocked / info.getTotalThreads();

        double threshold = 0.20; // 20%

        if (ratio > threshold) {
            Finding f = new Finding(getId(), "High Blocked Thread Ratio");
            f.setDescription("A significant portion of threads are in BLOCKED state.");
            f.setImpact(8);
            f.setConfidence(Finding.Confidence.HIGH);
            f.setEvidenceStrength(0.9);

            // Score = 8 * 1.0 * 0.9 = 7.2 -> ERROR
            f.setScore(8 * 1.0 * 0.9);
            f.setSeverity(Finding.Severity.ERROR);

            f.getEvidence().add(
                    String.format("Blocked threads: %d/%d (%.1f%%)", blocked, info.getTotalThreads(), ratio * 100));
            f.getRecommendations().add("Identify the lock causing contention (check thread names/stacks).");
            f.getRecommendations().add("Check database connection pools or synchronization bottlenecks.");
            return Collections.singletonList(f);
        }

        return Collections.emptyList();
    }
}
