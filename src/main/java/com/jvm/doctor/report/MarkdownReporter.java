package com.jvm.doctor.report;

import com.jvm.doctor.model.DiagnosisContext;
import com.jvm.doctor.model.Finding;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class MarkdownReporter {

    public void report(DiagnosisContext ctx, List<Finding> findings, File outputFile) throws IOException {
        try (PrintWriter w = new PrintWriter(new FileWriter(outputFile))) {
            w.println("# JVM Doctor Diagnosis Report");
            w.println("");
            w.println("**PID**: " + ctx.getPid());
            w.println("**Date**: " + ctx.getTimestamp());
            w.println("**Profile**: " + ctx.getProfile());
            w.println("");

            w.println("## Summary");
            w.println("| ID | Severity | Score | Title |");
            w.println("|---|---|---|---|");
            for (Finding f : findings) {
                w.printf("| %s | %s | %.1f | %s |%n", f.getId(), f.getSeverity(), f.getScore(), f.getTitle());
            }
            w.println("");

            w.println("## Detailed Findings");
            for (Finding f : findings) {
                w.println("### " + f.getTitle());
                w.println("- **ID**: " + f.getId());
                w.println("- **Severity**: " + f.getSeverity());
                w.println("- **Score**: " + f.getScore());
                w.println("- **Description**: " + f.getDescription());
                w.println("");

                w.println("#### Evidence");
                for (String e : f.getEvidence()) {
                    w.println("- " + e);
                }
                w.println("");

                w.println("#### Recommendations");
                for (String r : f.getRecommendations()) {
                    w.println("- " + r);
                }
                w.println("");
                w.println("---");
            }
        }
        System.out.println("Markdown report written to " + outputFile.getAbsolutePath());
    }
}
