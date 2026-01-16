package com.jvm.doctor.parsers;

import com.jvm.doctor.model.VmInfo;

public class VmVersionParser {

    // Example: Java HotSpot(TM) 64-Bit Server VM version 11.0.12+8-LTS-237
    // Example: OpenJDK 64-Bit Server VM version 17.0.1+12

    public VmInfo parse(String output) {
        VmInfo info = new VmInfo();

        // Simple heuristics
        for (String line : output.split("\n")) {
            if (line.contains("Java HotSpot(TM)")) {
                info.setJvmName("Java HotSpot(TM)");
                info.setJavaVendor("Oracle");
            } else if (line.contains("OpenJDK")) {
                info.setJvmName("OpenJDK");
                info.setJavaVendor("OpenJDK"); // approximate
            }

            if (line.contains("version")) {
                // Try to extract version string
                // ... VM version 11.0.12 ...
                int vIndex = line.indexOf("version ");
                if (vIndex != -1) {
                    String rest = line.substring(vIndex + 8).trim();
                    info.setJavaVersion(rest);
                }
            }
        }

        // If we extracted a version, use it
        return info;
    }
}
