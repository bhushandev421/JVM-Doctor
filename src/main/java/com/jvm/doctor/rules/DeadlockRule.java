package com.jvm.doctor.rules;

import com.jvm.doctor.model.DiagnosisContext;
import com.jvm.doctor.model.Finding;
import com.jvm.doctor.model.ThreadInfo;

import java.util.Collections;
import java.util.List;

public class DeadlockRule implements Rule {

    @Override
    public String getId() {
        return "THREAD_DEADLOCK";
    }

    @Override
    public List<Finding> evaluate(DiagnosisContext context) {
        ThreadInfo info = context.getThreadInfo();
        if (info == null || !info.isDeadlockDetected()) {
            return Collections.emptyList();
        }

        Finding f = new Finding(getId(), "Deadlock Detected");
        f.setDescription("One or more Java-level deadlocks were detected in the thread dump.");
        f.setImpact(10);
        f.setConfidence(Finding.Confidence.HIGH);
        f.setEvidenceStrength(1.0);
        f.setScore(10.0); // 10 * 1.0 * 1.0
        f.setSeverity(Finding.Severity.ERROR);

        if (info.getDeadlockedThreads() != null && !info.getDeadlockedThreads().isEmpty()) {
            f.getEvidence().add("Deadlocked threads detected: " + info.getDeadlockedThreads().size());
        } else {
            f.getEvidence().add("Deadlock flag found in thread dump.");
        }

        f.getRecommendations().add("Capture multiple thread dumps to confirm persistent hangs.");
        f.getRecommendations().add("Analyze thread stacks for lock cycles.");
        f.getRecommendations().add("Review synchronized blocks and lock ordering.");

        return Collections.singletonList(f);
    }
}
