package com.jvm.doctor.rules;

import com.jvm.doctor.model.DiagnosisContext;
import com.jvm.doctor.model.Finding;
import com.jvm.doctor.model.VmFlags;

import java.util.Collections;
import java.util.List;

public class MetaspaceRule implements Rule {

    @Override
    public String getId() {
        return "META_MAX_TOO_LOW";
    }

    @Override
    public List<Finding> evaluate(DiagnosisContext context) {
        VmFlags flags = context.getVmFlags();
        if (flags == null || flags.getMaxMetaspaceSize() == null)
            return Collections.emptyList();

        long maxMeta = flags.getMaxMetaspaceSize();
        // If unconstrained, it's usually -1 or very large.
        if (maxMeta < 0 || maxMeta > 100L * 1024 * 1024 * 1024)
            return Collections.emptyList();

        long threshold = 128 * 1024 * 1024; // 128MB
        if ("spring".equalsIgnoreCase(context.getProfile())) {
            threshold = 256 * 1024 * 1024; // 256MB
        }

        if (maxMeta < threshold) {
            Finding f = new Finding(getId(), "MaxMetaspaceSize Too Low");
            f.setDescription("MaxMetaspaceSize is set to a low value.");
            f.setImpact(6);
            f.setConfidence(Finding.Confidence.MED); // 0.75
            f.setEvidenceStrength(0.8);

            // Score = 6 * 0.75 * 0.8 = 3.6 -> INFO
            f.setScore(3.6);
            f.setSeverity(Finding.Severity.INFO);

            f.getEvidence().add("MaxMetaspaceSize: " + (maxMeta / 1024 / 1024) + "MB < threshold ("
                    + (threshold / 1024 / 1024) + "MB)");
            f.getRecommendations().add("Increase MaxMetaspaceSize to avoid OOM.");
            f.getRecommendations().add("Monitor class loading/unloading behavior.");
            return Collections.singletonList(f);
        }

        return Collections.emptyList();
    }
}
