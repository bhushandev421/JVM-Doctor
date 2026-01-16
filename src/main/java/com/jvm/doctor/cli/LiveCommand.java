package com.jvm.doctor.cli;

import com.jvm.doctor.collectors.JcmdRunner;
import com.jvm.doctor.collectors.JstatRunner;
import com.jvm.doctor.collectors.ProcessLister;
import com.jvm.doctor.model.*;
import com.jvm.doctor.parsers.*;
import com.jvm.doctor.report.ConsoleReporter;
import com.jvm.doctor.report.JsonReporter;
import com.jvm.doctor.report.MarkdownReporter;
import com.jvm.doctor.rules.RuleRegistry;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;

@Command(name = "live", description = "Run live diagnosis on a JVM")
public class LiveCommand implements Callable<Integer> {

    @Option(names = "--pid", description = "Target PID")
    private Long pid;

    @Option(names = "--pick", description = "Interactive process selection")
    private boolean pick;

    @Option(names = "--duration", description = "Sampling duration (e.g. 10s)")
    private String durationStr = "10s";

    @Option(names = "--interval", description = "Sampling interval (e.g. 1s)")
    private String intervalStr = "1s";

    @Option(names = "--format", description = "Output formats: console, json, md", split = ",")
    private List<String> formats = new ArrayList<>(Arrays.asList("console"));

    @Option(names = "--out", description = "Output file path (single file)")
    private File outFile;

    @Option(names = "--out-dir", description = "Output directory")
    private File outDir;

    @Option(names = "--profile", description = "Rule profile (generic, spring)")
    private String profile = "generic";

    @Option(names = "--verbose", description = "Include raw outputs in JSON")
    private boolean verbose;

    @Option(names = "--no-jstat", description = "Disable jstat sampling")
    private boolean noJstat;

    @Override
    public Integer call() throws Exception {
        if (pid == null) {
            if (pick) {
                pid = pickProcess();
                if (pid == null)
                    return 2; // User cancelled
            } else {
                System.err.println("Error: --pid <PID> is required (or use --pick)");
                return 2;
            }
        }

        System.out.println("Attaching to process " + pid + "...");

        long durationMs = parseTime(durationStr);
        long intervalMs = parseTime(intervalStr);

        // 1. Collect Data
        JcmdRunner jcmd = new JcmdRunner();

        String vmVersionRaw = null;
        String vmFlagsRaw = null;
        String threadDumpRaw = null;
        String heapInfoRaw = null;
        List<String> jstatRaw = null;

        try {
            vmVersionRaw = jcmd.run(pid, "VM.version");
            vmFlagsRaw = jcmd.run(pid, "VM.flags");
            heapInfoRaw = jcmd.run(pid, "GC.heap_info");
            threadDumpRaw = jcmd.run(pid, "Thread.print -l");

            // Jstat (optional)
            if (!noJstat) {
                int samples = (int) (durationMs / intervalMs);
                if (samples < 1)
                    samples = 1;
                System.out.println("Collecting jstat samples (" + samples + " samples @ " + intervalMs + "ms)...");
                JstatRunner jstat = new JstatRunner();
                jstatRaw = jstat.runGcUtil(pid, intervalMs, samples);
            }

        } catch (Exception e) {
            System.err.println("Error collecting data: " + e.getMessage());
            e.printStackTrace();
            return 3;
        }

        // 2. Parse Data
        DiagnosisContext ctx = new DiagnosisContext();
        ctx.setPid(pid);
        ctx.setTimestamp(LocalDateTime.now());
        ctx.setProfile(profile);

        if (vmVersionRaw != null)
            ctx.setVmInfo(new VmVersionParser().parse(vmVersionRaw));
        if (vmFlagsRaw != null)
            ctx.setVmFlags(new VmFlagsParser().parse(vmFlagsRaw));
        if (heapInfoRaw != null)
            ctx.setHeapInfo(new HeapInfoParser().parse(heapInfoRaw));
        if (threadDumpRaw != null)
            ctx.setThreadInfo(new ThreadDumpParser().parse(threadDumpRaw));
        if (jstatRaw != null)
            ctx.setJstatSamples(new JstatGcutilParser().parse(jstatRaw));

        // 3. Evaluate Rules
        RuleRegistry registry = new RuleRegistry();
        List<Finding> findings = registry.evaluateAll(ctx);

        // 4. Report
        boolean hasError = findings.stream().anyMatch(f -> f.getSeverity() == Finding.Severity.ERROR);

        if (formats.contains("console")) {
            new ConsoleReporter().report(ctx, findings);
        }

        if (formats.contains("json")) {
            File dest = outFile;
            if (outDir != null)
                dest = new File(outDir, "jvm-doctor-" + pid + ".json");
            // If dest is null and format is json, reporter prints to stdout (but console
            // reporter also prints)
            // Ideally if console AND json are set, JSON should go to file or --out
            new JsonReporter().report(ctx, findings, dest, verbose);
        }

        if (formats.contains("md")) {
            File dest = outFile;
            if (outDir != null)
                dest = new File(outDir, "jvm-doctor-" + pid + ".md");
            if (dest == null)
                dest = new File("jvm-doctor-" + pid + ".md");
            new MarkdownReporter().report(ctx, findings, dest);
        }

        return hasError ? 1 : 0;
    }

    private Long pickProcess() {
        ProcessLister lister = new ProcessLister();
        List<ProcessLister.JvmProcess> procs = lister.listProcesses();
        if (procs.isEmpty()) {
            System.out.println("No Java processes found.");
            return null;
        }

        System.out.println("Select a process:");
        for (int i = 0; i < procs.size(); i++) {
            System.out.printf("[%d] %d %s%n", i + 1, procs.get(i).pid, procs.get(i).name);
        }

        System.out.print("Enter number: ");
        try (Scanner sc = new Scanner(System.in)) {
            if (sc.hasNextInt()) {
                int idx = sc.nextInt();
                if (idx > 0 && idx <= procs.size()) {
                    return procs.get(idx - 1).pid;
                }
            }
        }
        return null;
    }

    private long parseTime(String s) {
        s = s.toLowerCase();
        if (s.endsWith("ms"))
            return Long.parseLong(s.replace("ms", ""));
        if (s.endsWith("s"))
            return Long.parseLong(s.replace("s", "")) * 1000;
        if (s.endsWith("m"))
            return Long.parseLong(s.replace("m", "")) * 60000;
        try {
            return Long.parseLong(s); // assume ms
        } catch (NumberFormatException e) {
            return 1000; // default
        }
    }
}
