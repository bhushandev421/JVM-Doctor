package com.jvm.doctor.model;

public class HeapInfo {
    private String rawOutput;

    // Generic heap usage if parseable
    private Long heapUsedBytes;
    private Long heapMaxBytes;

    // Region specific
    private Long youngUsedBytes;
    private Long oldUsedBytes;
    private Long metaspaceUsedBytes;

    public String getRawOutput() {
        return rawOutput;
    }

    public void setRawOutput(String rawOutput) {
        this.rawOutput = rawOutput;
    }

    public Long getHeapUsedBytes() {
        return heapUsedBytes;
    }

    public void setHeapUsedBytes(Long heapUsedBytes) {
        this.heapUsedBytes = heapUsedBytes;
    }

    public Long getHeapMaxBytes() {
        return heapMaxBytes;
    }

    public void setHeapMaxBytes(Long heapMaxBytes) {
        this.heapMaxBytes = heapMaxBytes;
    }

    public Long getYoungUsedBytes() {
        return youngUsedBytes;
    }

    public void setYoungUsedBytes(Long youngUsedBytes) {
        this.youngUsedBytes = youngUsedBytes;
    }

    public Long getOldUsedBytes() {
        return oldUsedBytes;
    }

    public void setOldUsedBytes(Long oldUsedBytes) {
        this.oldUsedBytes = oldUsedBytes;
    }

    public Long getMetaspaceUsedBytes() {
        return metaspaceUsedBytes;
    }

    public void setMetaspaceUsedBytes(Long metaspaceUsedBytes) {
        this.metaspaceUsedBytes = metaspaceUsedBytes;
    }
}
