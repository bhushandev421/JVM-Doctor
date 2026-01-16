package com.jvm.doctor.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreadInfo {
    private int totalThreads;
    private Map<String, Integer> stateCounts = new HashMap<>();

    private boolean deadlockDetected;
    private List<String> deadlockedThreads;

    public int getTotalThreads() {
        return totalThreads;
    }

    public void setTotalThreads(int totalThreads) {
        this.totalThreads = totalThreads;
    }

    public Map<String, Integer> getStateCounts() {
        return stateCounts;
    }

    public void setStateCounts(Map<String, Integer> stateCounts) {
        this.stateCounts = stateCounts;
    }

    public boolean isDeadlockDetected() {
        return deadlockDetected;
    }

    public void setDeadlockDetected(boolean deadlockDetected) {
        this.deadlockDetected = deadlockDetected;
    }

    public List<String> getDeadlockedThreads() {
        return deadlockedThreads;
    }

    public void setDeadlockedThreads(List<String> deadlockedThreads) {
        this.deadlockedThreads = deadlockedThreads;
    }
}
