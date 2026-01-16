package com.jvm.doctor.collectors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class JcmdRunner {

    public String run(long pid, String command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("jcmd", String.valueOf(pid), command);
        pb.redirectErrorStream(true); // Merge stderr

        Process p = pb.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        if (!p.waitFor(30, TimeUnit.SECONDS)) {
            p.destroyForcibly();
            throw new IOException("jcmd timed out for pid " + pid + " cmd " + command);
        }

        if (p.exitValue() != 0) {
            // We might still want the output even if exit code is non-zero
            // but for simple cases, let's treat as success if we got output
            // or we can throw. The requirement says "graceful error".
            // We will return output but the caller should check for "Could not find..."
            // messages.
        }

        return output.toString();
    }
}
