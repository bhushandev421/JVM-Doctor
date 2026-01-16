package com.jvm.doctor.rules;

import com.jvm.doctor.model.DiagnosisContext;
import com.jvm.doctor.model.Finding;
import com.jvm.doctor.model.ThreadInfo;

import java.util.Collections;
import java.util.List;

public class ThreadExplosionRule implements Rule {

    @Override
    public String getId() {
        return "THREAD_COUNT_HIGH";
    }

    @Override
    public List<Finding> evaluate(DiagnosisContext context) {
        ThreadInfo info = context.getThreadInfo();
        if (info == null)
            return Collections.emptyList();

        int threshold = 300;
        if ("spring".equalsIgnoreCase(context.getProfile())) {
            threshold = 400;
        }

        if (info.getTotalThreads() > threshold) {
            Finding f = new Finding(getId(), "High Thread Count");
            f.setDescription("The JVM has a high number of live threads.");
            f.setImpact(7);
            f.setConfidence(Finding.Confidence.HIGH);
            f.setEvidenceStrength(0.9);

            // Score = 7 * 1.0 * 0.9 = 6.3 -> WARN
            f.setScore(7 * 1.0 * 0.9);
            f.setSeverity(Finding.Severity.WARN);

            f.getEvidence().add("Threads: " + info.getTotalThreads() + " > threshold (" + threshold + ")");
            f.getRecommendations().add("Check thread pool sizing configurations.");
            f.getRecommendations().add("Investigate blocked threads or I/O bottlenecks.");
            return Collections.singletonList(f);
        }

        return Collections.emptyList();
    }
}
