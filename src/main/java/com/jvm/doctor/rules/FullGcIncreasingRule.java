package com.jvm.doctor.rules;

import com.jvm.doctor.model.DiagnosisContext;
import com.jvm.doctor.model.Finding;
import com.jvm.doctor.model.JstatSample;

import java.util.Collections;
import java.util.List;

public class FullGcIncreasingRule implements Rule {

    @Override
    public String getId() {
        return "GC_FULL_GC_ACTIVITY";
    }

    @Override
    public List<Finding> evaluate(DiagnosisContext context) {
        List<JstatSample> samples = context.getJstatSamples();
        if (samples == null || samples.size() < 2) {
            // Can't determine increase if only 0 or 1 sample
            return Collections.emptyList();
        }

        // Check if FGC count increases
        Double firstFGC = null;
        Double lastFGC = null;

        // Find first sample with FGC
        for (JstatSample s : samples) {
            if (s.getValue("FGC") != null) {
                if (firstFGC == null)
                    firstFGC = s.getValue("FGC");
                lastFGC = s.getValue("FGC");
            }
        }

        if (firstFGC != null && lastFGC != null && lastFGC > firstFGC) {
            double increase = lastFGC - firstFGC;

            Finding f = new Finding(getId(), "Full GC Activity Detected");
            f.setDescription("Full Garbage Collection events occurred during the sampling window.");
            f.setImpact(9);
            f.setConfidence(Finding.Confidence.HIGH); // 1.0
            f.setEvidenceStrength(0.9);

            // Score = 9 * 1.0 * 0.9 = 8.1 -> ERROR
            f.setScore(8.1);
            f.setSeverity(Finding.Severity.ERROR);

            f.getEvidence().add(
                    "Full GC Count increased by " + (int) increase + " (from " + firstFGC + " to " + lastFGC + ")");
            f.getRecommendations().add("Investigate memory pressure or memory leaks.");
            f.getRecommendations().add("Check Old Generation usage.");
            f.getRecommendations().add("Consider taking a heap dump.");
            return Collections.singletonList(f);
        }
        return Collections.emptyList();
    }
}
