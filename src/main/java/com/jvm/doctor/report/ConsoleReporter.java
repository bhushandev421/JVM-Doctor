package com.jvm.doctor.report;

import com.jvm.doctor.model.DiagnosisContext;
import com.jvm.doctor.model.Finding;
import java.util.List;

public class ConsoleReporter {

    public void report(DiagnosisContext ctx, List<Finding> findings) {
        System.out.println("=================================================================================");
        System.out.println(" JVM DOCTOR REPORT ");
        System.out.println("=================================================================================");
        System.out.printf(" PID: %d%n", ctx.getPid());
        if (ctx.getVmInfo() != null) {
            System.out.printf(" Java: %s (%s)%n", ctx.getVmInfo().getJavaVersion(), ctx.getVmInfo().getJvmName());
        }
        if (ctx.getThreadInfo() != null) {
            System.out.printf(" Threads: %d%n", ctx.getThreadInfo().getTotalThreads());
        }
        if (ctx.getHeapInfo() != null && ctx.getHeapInfo().getHeapMaxBytes() > 0) {
            System.out.printf(" Heap Max: %d MB%n", ctx.getHeapInfo().getHeapMaxBytes() / 1024 / 1024);
        }

        System.out.println("\n[TOP FINDINGS]");
        int limit = Math.min(3, findings.size());
        if (limit == 0) {
            System.out.println(" No significant findings.");
        }
        for (int i = 0; i < limit; i++) {
            printFinding(findings.get(i));
        }

        System.out.println("\n[ALL FINDINGS]");
        if (findings.isEmpty()) {
            System.out.println(" No issues found. System looks healthy.");
        } else {
            for (Finding f : findings) {
                printFinding(f);
            }
        }
        System.out.println("=================================================================================");
    }

    private void printFinding(Finding f) {

        System.out.printf(" [%s %.1f] %s%n", f.getSeverity(), f.getScore(), f.getTitle());
        System.out.printf("   Impact: %d | Confidence: %s%n", f.getImpact(), f.getConfidence());

        if (!f.getEvidence().isEmpty()) {
            System.out.println("   Evidence:");
            for (String e : f.getEvidence()) {
                System.out.println("    - " + e);
            }
        }

        if (!f.getRecommendations().isEmpty()) {
            System.out.println("   Recommendations:");
            for (String r : f.getRecommendations()) {
                System.out.println("    -> " + r);
            }
        }
        System.out.println();
    }
}
