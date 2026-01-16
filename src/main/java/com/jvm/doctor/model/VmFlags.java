package com.jvm.doctor.model;

import java.util.ArrayList;
import java.util.List;

public class VmFlags {
    private List<String> rawFlags = new ArrayList<>();

    private Long xms;
    private Long xmx;
    private Long maxMetaspaceSize;
    private Double maxRAMPercentage;

    private boolean useG1GC;
    private boolean useZGC;
    private boolean useShenandoahGC;
    private boolean useParallelGC;
    private boolean useSerialGC;

    public List<String> getRawFlags() {
        return rawFlags;
    }

    public void setRawFlags(List<String> rawFlags) {
        this.rawFlags = rawFlags;
    }

    public Long getXms() {
        return xms;
    }

    public void setXms(Long xms) {
        this.xms = xms;
    }

    public Long getXmx() {
        return xmx;
    }

    public void setXmx(Long xmx) {
        this.xmx = xmx;
    }

    public Long getMaxMetaspaceSize() {
        return maxMetaspaceSize;
    }

    public void setMaxMetaspaceSize(Long maxMetaspaceSize) {
        this.maxMetaspaceSize = maxMetaspaceSize;
    }

    public Double getMaxRAMPercentage() {
        return maxRAMPercentage;
    }

    public void setMaxRAMPercentage(Double maxRAMPercentage) {
        this.maxRAMPercentage = maxRAMPercentage;
    }

    public boolean isUseG1GC() {
        return useG1GC;
    }

    public void setUseG1GC(boolean useG1GC) {
        this.useG1GC = useG1GC;
    }

    public boolean isUseZGC() {
        return useZGC;
    }

    public void setUseZGC(boolean useZGC) {
        this.useZGC = useZGC;
    }

    public boolean isUseShenandoahGC() {
        return useShenandoahGC;
    }

    public void setUseShenandoahGC(boolean useShenandoahGC) {
        this.useShenandoahGC = useShenandoahGC;
    }

    public boolean isUseParallelGC() {
        return useParallelGC;
    }

    public void setUseParallelGC(boolean useParallelGC) {
        this.useParallelGC = useParallelGC;
    }

    public boolean isUseSerialGC() {
        return useSerialGC;
    }

    public void setUseSerialGC(boolean useSerialGC) {
        this.useSerialGC = useSerialGC;
    }
}
