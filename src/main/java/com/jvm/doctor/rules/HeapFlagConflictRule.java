package com.jvm.doctor.rules;

import com.jvm.doctor.model.DiagnosisContext;
import com.jvm.doctor.model.Finding;
import com.jvm.doctor.model.VmFlags;

import java.util.Collections;
import java.util.List;

public class HeapFlagConflictRule implements Rule {

    @Override
    public String getId() {
        return "FLAGS_HEAP_CONFLICT";
    }

    @Override
    public List<Finding> evaluate(DiagnosisContext context) {
        VmFlags flags = context.getVmFlags();
        if (flags == null)
            return Collections.emptyList();

        // Check if both Xmx and MaxRAMPercentage are set
        // Note: Xmx is 'MaxHeapSize=' in flags

        // However, defaults might be present. R4 says "both present".
        // In VM.flags, we only see what's active.
        // We need to look at raw flags to see if user set them or if they are just
        // defaults.
        // But VM.flags output shows "command line" or "ergonomic" sometimes.
        // V1 Simplicity: if VmFlagsParser found them both in the output, we flag it.
        // Caveat: MaxHeapSize is always present (default). MaxRAMPercentage might be
        // default too.
        // This rule is tricky without knowing if it was *user specified*.
        // Detailed `jcmd VM.flags -all` shows "command line" vs "product".
        // Our parser just grabs values.
        // Let's refine: Only if both are in raw flags as arguments?
        // The spec says "Condition: both Xmx and MaxRAMPercentage present".
        // Let's rely on parsed values. But wait, `MaxHeapSize` is always there.
        // Let's check `VM.command_line` if possible?
        // As a heuristic for v1: if MaxRAMPercentage is found in the output string
        // (meaning we parsed it)
        // AND MaxHeapSize is also there.
        // Actually, typical conflict is users setting `-Xmx4g
        // -XX:MaxRAMPercentage=80.0`.
        // If we see both in the token list.

        // Let's check if the specific strings exist in raw flags list to be safer.
        boolean rawXmx = findRaw(flags.getRawFlags(), "MaxHeapSize=");
        boolean rawRam = findRaw(flags.getRawFlags(), "MaxRAMPercentage=");

        if (rawXmx && rawRam) {
            Finding f = new Finding(getId(), "Conflicting Heap Sizing Flags");
            f.setDescription("Both fixed heap size (Xmx) and RAM percentage are specified.");
            f.setImpact(4);
            f.setConfidence(Finding.Confidence.HIGH);
            f.setEvidenceStrength(0.8);

            // Score = 4 * 1.0 * 0.8 = 3.2 -> INFO
            f.setScore(3.2);
            f.setSeverity(Finding.Severity.INFO);

            f.getEvidence().add("Found both MaxHeapSize and MaxRAMPercentage flags.");
            f.getRecommendations().add("Choose one sizing strategy to avoid confusion.");
            return Collections.singletonList(f);
        }

        return Collections.emptyList();
    }

    private boolean findRaw(List<String> raw, String key) {
        if (raw == null)
            return false;
        for (String s : raw) {
            if (s.contains(key))
                return true;
        }
        return false;
    }
}
