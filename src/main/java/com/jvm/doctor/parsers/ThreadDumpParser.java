package com.jvm.doctor.parsers;

import com.jvm.doctor.model.ThreadInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThreadDumpParser {

    // Thread State: "main" #1 prio=5 os_prio=31 cpu=105.47ms elapsed=22.56s
    // tid=0x00007fc8d3008800 nid=0x2603 waiting on condition [0x0000700008f1b000]
    // java.lang.Thread.State: TIMED_WAITING (sleeping)

    public ThreadInfo parse(String output) {
        ThreadInfo info = new ThreadInfo();
        Map<String, Integer> counts = new HashMap<>();

        // Count states
        Pattern pState = Pattern.compile("java\\.lang\\.Thread\\.State: ([A-Z_]+)");
        Matcher mState = pState.matcher(output);
        int total = 0;

        while (mState.find()) {
            String state = mState.group(1);
            counts.put(state, counts.getOrDefault(state, 0) + 1);
            total++;
        }

        info.setTotalThreads(total);
        info.setStateCounts(counts);

        // Deadlock detection
        // "Found one Java-level deadlock:"
        if (output.contains("Found one Java-level deadlock:") || output.contains("Found 1 deadlock.")) { // strict check
            info.setDeadlockDetected(true);
            // Parse deadlock victim threads - usually listed after
            // "Java stack information for the threads listed above:"
            List<String> deadlocked = new ArrayList<>();
            // Very simple heuristic: extract threads listed in the deadlock section
            // For v1, we just flag it.
            info.setDeadlockedThreads(deadlocked);
        } else {
            // Check for plural
            Pattern pDeadlocks = Pattern.compile("Found (\\d+) Java-level deadlocks:");
            Matcher mDeadlocks = pDeadlocks.matcher(output);
            if (mDeadlocks.find()) {
                info.setDeadlockDetected(true);
            }
        }

        return info;
    }
}
