package com.jvm.doctor.parsers;

import com.jvm.doctor.model.HeapInfo;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeapInfoParser {

    // G1 Heap:
    // garbage-first heap total 514048K, used 123456K [0x...]
    // region size 2048K, 12 young (24576K), 0 survivors (0K)
    // Metaspace used 15302K, capacity 15550K, committed 15616K, reserved 1062912K

    // Serial/Parallel Heap:
    // def new generation total 78080K, used 6087K [0x...]
    // eden space 69440K, 8% used [0x...]
    // from space 8640K, 0% used [0x...]
    // to space 8640K, 0% used [0x...]
    // tenured generation total 173440K, used 0K [0x...]
    // the space 173440K, 0% used [0x...]
    // Metaspace used 5219K, capacity 5310K, committed 5504K, reserved 1056768K

    public HeapInfo parse(String output) {
        HeapInfo info = new HeapInfo();
        info.setRawOutput(output);

        long totalHeapUsed = 0;
        long totalHeapMax = 0;

        String[] lines = output.split("\n");
        for (String line : lines) {
            String clean = line.trim();

            // Generic "total X, used Y" matcher
            if (clean.contains("total") && clean.contains("used")) {
                long total = extractKBytes(clean, "total");
                long used = extractKBytes(clean, "used");

                if (clean.startsWith("garbage-first heap") || clean.startsWith("def new generation")
                        || clean.startsWith("tenured generation") || clean.startsWith("PSYoungGen")
                        || clean.startsWith("ParOldGen")) {
                    // This is additive for some GCs, but for G1 "garbage-first heap" is the summary
                    if (clean.startsWith("garbage-first heap")) {
                        info.setHeapMaxBytes(total);
                        info.setHeapUsedBytes(used);
                        return info; // G1 summary found
                    }
                }
            }

            if (clean.startsWith("Metaspace")) {
                long metaUsed = extractKBytes(clean, "used");
                info.setMetaspaceUsedBytes(metaUsed);
            }
        }

        // If we didn't find G1 specific summary, we might need to sum up young+old?
        // For simplicity in v1, let's rely on parsing "total" from summary lines.
        // Or if we run out of time, we rely on Jstat for precise numbers.

        return info;
    }

    // Helper to extract "key 1234K"
    private long extractKBytes(String line, String key) {
        // pattern: key\s+(\d+)K
        Pattern p = Pattern.compile(key + "\\s+(\\d+)K");
        Matcher m = p.matcher(line);
        if (m.find()) {
            return Long.parseLong(m.group(1)) * 1024;
        }
        return 0;
    }
}
