package com.jvm.doctor.rules;

import com.jvm.doctor.model.DiagnosisContext;
import com.jvm.doctor.model.Finding;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuleRegistry {
    private final List<Rule> rules = new ArrayList<>();

    public RuleRegistry() {
        // Register all standard rules
        rules.add(new DeadlockRule());
        rules.add(new ThreadExplosionRule());
        rules.add(new BlockedRatioRule());
        rules.add(new HeapFlagConflictRule());
        rules.add(new MetaspaceRule());
        rules.add(new ObservabilityGapRule());
        rules.add(new FullGcIncreasingRule());
        rules.add(new OldGenHighRule());
    }

    public List<Finding> evaluateAll(DiagnosisContext context) {
        List<Finding> allFindings = new ArrayList<>();

        // Special case: Jstat Unavailable
        if (context.getJstatSamples().isEmpty()) {
            Finding info = new Finding("JSTAT_UNAVAILABLE", "Jstat Telemetry Missing");
            info.setDescription("Could not collect jstat GC samples. GC health checks (R7, R8) skipped.");
            info.setImpact(1);
            info.setConfidence(Finding.Confidence.HIGH);
            info.setEvidenceStrength(0.6);
            info.setScore(0.6);
            info.setSeverity(Finding.Severity.INFO);
            allFindings.add(info);
        }

        for (Rule rule : rules) {
            allFindings.addAll(rule.evaluate(context));
        }

        Collections.sort(allFindings);
        return allFindings;
    }
}
