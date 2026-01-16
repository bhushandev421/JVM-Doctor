package com.jvm.doctor.collectors;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProcessLister {

    public static class JvmProcess {
        public long pid;
        public String name;

        public JvmProcess(long pid, String name) {
            this.pid = pid;
            this.name = name;
        }

        @Override
        public String toString() {
            return pid + " " + name;
        }
    }

    public List<JvmProcess> listProcesses() {
        List<JvmProcess> processes = new ArrayList<>();
        try {
            // Prefer 'jcmd -l' as it lists all JVMs
            ProcessBuilder pb = new ProcessBuilder("jcmd", "-l");
            Process p = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    JvmProcess proc = parseLine(line);
                    if (proc != null) {
                        processes.add(proc);
                    }
                }
            }
            p.waitFor();
        } catch (Exception e) {
            // Fallback could be jps or ps, but for tool v1 jcmd is required
            System.err.println("Warning: failed to list processes via jcmd: " + e.getMessage());
        }
        return processes;
    }

    private JvmProcess parseLine(String line) {
        // Format: <PID> <MainClass/Jar> [Args]
        // or just <PID>
        String trimmed = line.trim();
        if (trimmed.isEmpty())
            return null;

        int spaceDesc = trimmed.indexOf(' ');
        if (spaceDesc == -1) {
            // Maybe just PID?
            try {
                long pid = Long.parseLong(trimmed);
                return new JvmProcess(pid, "<Unknown>");
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            String pidStr = trimmed.substring(0, spaceDesc);
            String name = trimmed.substring(spaceDesc + 1).trim();
            try {
                long pid = Long.parseLong(pidStr);
                return new JvmProcess(pid, name);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
}
