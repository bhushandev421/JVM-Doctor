package com.jvm.doctor.rules;

import com.jvm.doctor.model.DiagnosisContext;
import com.jvm.doctor.model.Finding;
import com.jvm.doctor.model.JstatSample;

import java.util.Collections;
import java.util.List;

public class OldGenHighRule implements Rule {

    @Override
    public String getId() {
        return "GC_OLD_GEN_HIGH";
    }

    @Override
    public List<Finding> evaluate(DiagnosisContext context) {
        List<JstatSample> samples = context.getJstatSamples();
        // If no samples, check HeapInfo? Spec says "requires jstat".
        if (samples == null || samples.isEmpty()) {
            return Collections.emptyList();
        }

        // Check last sample "O"
        JstatSample last = samples.get(samples.size() - 1);
        Double oldGenUsage = last.getValue("O"); // GC util "O" is percentage usually

        if (oldGenUsage == null)
            return Collections.emptyList();

        if (oldGenUsage > 85.0) {
            Finding f = new Finding(getId(), "Old Gen Utilization High");
            f.setDescription("Old Generation memory usage is critically high.");
            f.setImpact(7);
            f.setConfidence(Finding.Confidence.MED); // 0.75
            f.setEvidenceStrength(0.8);

            // Score = 7 * 0.75 * 0.8 = 4.2 -> WARN
            f.setScore(4.2);
            f.setSeverity(Finding.Severity.WARN);

            f.getEvidence().add(String.format("Old Gen usage: %.2f%% > 85%%", oldGenUsage));
            f.getRecommendations().add("Check for memory leaks or high live-set size.");
            f.getRecommendations().add("Tune heap size or GC ratios.");
            return Collections.singletonList(f);
        }

        return Collections.emptyList();
    }
}
