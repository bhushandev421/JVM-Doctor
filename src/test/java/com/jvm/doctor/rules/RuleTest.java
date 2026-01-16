package com.jvm.doctor.rules;

import com.jvm.doctor.model.DiagnosisContext;
import com.jvm.doctor.model.Finding;
import com.jvm.doctor.model.ThreadInfo;
import com.jvm.doctor.model.VmFlags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RuleTest {

    @Test
    public void testDeadlockRule() {
        DiagnosisContext ctx = new DiagnosisContext();
        ThreadInfo t = new ThreadInfo();
        t.setDeadlockDetected(true);
        t.setDeadlockedThreads(Collections.singletonList("Thread-1"));
        ctx.setThreadInfo(t);

        DeadlockRule rule = new DeadlockRule();
        List<Finding> findings = rule.evaluate(ctx);

        Assertions.assertEquals(1, findings.size());
        Assertions.assertEquals(Finding.Severity.ERROR, findings.get(0).getSeverity());
        Assertions.assertEquals(10.0, findings.get(0).getScore());
    }

    @Test
    public void testThreadExplosionSpring() {
        DiagnosisContext ctx = new DiagnosisContext();
        ctx.setProfile("spring");
        ThreadInfo t = new ThreadInfo();
        t.setTotalThreads(450);
        ctx.setThreadInfo(t);

        ThreadExplosionRule rule = new ThreadExplosionRule();
        List<Finding> findings = rule.evaluate(ctx);

        Assertions.assertEquals(1, findings.size());
        Assertions.assertEquals("THREAD_COUNT_HIGH", findings.get(0).getId());
    }

    @Test
    public void testFlagsConflict() {
        DiagnosisContext ctx = new DiagnosisContext();
        VmFlags flags = new VmFlags();
        flags.setRawFlags(List.of(
                "-XX:MaxHeapSize=1G",
                "-XX:MaxRAMPercentage=80.0"));
        // We need to set fields too if rule relies on them? Rule relies on raw list
        // search mostly
        flags.setXmx(1024L * 1024 * 1024);
        flags.setMaxRAMPercentage(80.0);
        ctx.setVmFlags(flags);

        HeapFlagConflictRule rule = new HeapFlagConflictRule();
        List<Finding> findings = rule.evaluate(ctx);

        Assertions.assertEquals(1, findings.size());
    }
}
