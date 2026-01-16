package com.jvm.doctor.collectors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JstatRunner {

    // jstat -gcutil <pid> <interval> <samples>
    public List<String> runGcUtil(long pid, long intervalMs, int samples) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                "jstat", "-gcutil", String.valueOf(pid), String.valueOf(intervalMs), String.valueOf(samples));
        pb.redirectErrorStream(true);

        Process p = pb.start();

        List<String> outputLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    outputLines.add(line);
                }
            }
        }

        // Wait allowing some buffer for JVM pause
        long timeoutMs = (intervalMs * samples) + 5000;
        if (!p.waitFor(timeoutMs, TimeUnit.MILLISECONDS)) {
            p.destroyForcibly();
            throw new IOException("jstat timed out");
        }

        return outputLines;
    }
}
