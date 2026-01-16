package com.jvm.doctor.model;

import java.util.HashMap;
import java.util.Map;

public class JstatSample {
    private long timestamp;
    private Map<String, Double> values = new HashMap<>();

    public JstatSample(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Map<String, Double> getValues() {
        return values;
    }

    public void setValues(Map<String, Double> values) {
        this.values = values;
    }

    public Double getValue(String key) {
        return values.get(key);
    }
}
