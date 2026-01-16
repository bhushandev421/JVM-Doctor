package com.jvm.doctor.rules;

import com.jvm.doctor.model.DiagnosisContext;
import com.jvm.doctor.model.Finding;
import com.jvm.doctor.model.VmFlags;

import java.util.Collections;
import java.util.List;

public class ObservabilityGapRule implements Rule {

    @Override
    public String getId() {
        return "OBS_GC_LOGS_MISSING";
    }

    @Override
    public List<Finding> evaluate(DiagnosisContext context) {
        VmFlags flags = context.getVmFlags();
        if (flags == null || flags.getRawFlags() == null)
            return Collections.emptyList();

        // Heuristic: check for Xlog:gc or Xloggc or PrintGCDetails
        boolean loggingEnabled = false;
        for (String flag : flags.getRawFlags()) {
            if (flag.contains("Xlog:gc") || flag.contains("Xloggc") || flag.contains("PrintGC")) {
                loggingEnabled = true;
                break;
            }
        }

        if (!loggingEnabled) {
            Finding f = new Finding(getId(), "GC Logging Not Enabled");
            f.setDescription("Garbage Collection logging appears to be disabled.");
            f.setImpact(3);
            f.setConfidence(Finding.Confidence.MED); // 0.75
            f.setEvidenceStrength(0.7);

            // Score = 3 * 0.75 * 0.7 = 1.575 -> INFO
            f.setScore(1.575);
            f.setSeverity(Finding.Severity.INFO);

            f.getEvidence().add("No GC logging flags found (Xlog, Xloggc, PrintGC).");
            f.getRecommendations().add("Enable GC logging for production monitoring.");
            f.getRecommendations().add("Use -Xlog:gc*:file=... (JDK9+) or -Xloggc:... (JDK8)");
            return Collections.singletonList(f);
        }

        return Collections.emptyList();
    }
}
