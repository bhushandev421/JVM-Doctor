package com.jvm.doctor.model;

public class VmInfo {
    private String javaVersion;
    private String javaVendor;
    private String jvmName;
    private String jvmVersion;

    // Getters and Setters and toString
    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public String getJavaVendor() {
        return javaVendor;
    }

    public void setJavaVendor(String javaVendor) {
        this.javaVendor = javaVendor;
    }

    public String getJvmName() {
        return jvmName;
    }

    public void setJvmName(String jvmName) {
        this.jvmName = jvmName;
    }

    public String getJvmVersion() {
        return jvmVersion;
    }

    public void setJvmVersion(String jvmVersion) {
        this.jvmVersion = jvmVersion;
    }

    @Override
    public String toString() {
        return "VmInfo{" +
                "javaVersion='" + javaVersion + '\'' +
                ", javaVendor='" + javaVendor + '\'' +
                ", jvmName='" + jvmName + '\'' +
                ", jvmVersion='" + jvmVersion + '\'' +
                '}';
    }
}
