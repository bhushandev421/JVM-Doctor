package com.jvm.doctor.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DiagnosisContext {
    private VmInfo vmInfo;
    private VmFlags vmFlags;
    private HeapInfo heapInfo;
    private ThreadInfo threadInfo;
    private List<JstatSample> jstatSamples = new ArrayList<>();

    // Metadata
    private long pid;
    private LocalDateTime timestamp;
    private String profile;

    public VmInfo getVmInfo() {
        return vmInfo;
    }

    public void setVmInfo(VmInfo vmInfo) {
        this.vmInfo = vmInfo;
    }

    public VmFlags getVmFlags() {
        return vmFlags;
    }

    public void setVmFlags(VmFlags vmFlags) {
        this.vmFlags = vmFlags;
    }

    public HeapInfo getHeapInfo() {
        return heapInfo;
    }

    public void setHeapInfo(HeapInfo heapInfo) {
        this.heapInfo = heapInfo;
    }

    public ThreadInfo getThreadInfo() {
        return threadInfo;
    }

    public void setThreadInfo(ThreadInfo threadInfo) {
        this.threadInfo = threadInfo;
    }

    public List<JstatSample> getJstatSamples() {
        return jstatSamples;
    }

    public void setJstatSamples(List<JstatSample> jstatSamples) {
        this.jstatSamples = jstatSamples;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
