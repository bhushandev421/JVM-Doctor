package com.jvm.doctor.parsers;

import com.jvm.doctor.model.VmFlags;
import java.util.ArrayList;
import java.util.List;

public class VmFlagsParser {

    public VmFlags parse(String output) {
        VmFlags flags = new VmFlags();
        List<String> raw = new ArrayList<>();

        // Output from 'VM.flags' is often one single line with lots of flags
        // -XX:MaxHeapSize=... -XX:+UseG1GC ...

        String[] tokens = output.split("\\s+");
        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty())
                continue;
            if (token.equals("Result:"))
                continue; // "Result:" prefix sometimes

            raw.add(token);

            if (token.contains("UseG1GC"))
                flags.setUseG1GC(isPlus(token));
            if (token.contains("UseZGC"))
                flags.setUseZGC(isPlus(token));
            if (token.contains("UseShenandoahGC"))
                flags.setUseShenandoahGC(isPlus(token));
            if (token.contains("UseParallelGC"))
                flags.setUseParallelGC(isPlus(token));
            if (token.contains("UseSerialGC"))
                flags.setUseSerialGC(isPlus(token));

            if (token.contains("MaxHeapSize="))
                parseBytes(token, "MaxHeapSize=").ifPresent(flags::setXmx);
            if (token.contains("InitialHeapSize="))
                parseBytes(token, "InitialHeapSize=").ifPresent(flags::setXms);
            if (token.contains("MaxMetaspaceSize="))
                parseBytes(token, "MaxMetaspaceSize=").ifPresent(flags::setMaxMetaspaceSize);
            if (token.contains("MaxRAMPercentage="))
                parseDouble(token, "MaxRAMPercentage=").ifPresent(flags::setMaxRAMPercentage);
        }

        flags.setRawFlags(raw);
        return flags;
    }

    private boolean isPlus(String token) {
        return token.contains("+");
    }

    private java.util.Optional<Long> parseBytes(String token, String prefix) {
        try {
            int idx = token.indexOf(prefix);
            String val = token.substring(idx + prefix.length());
            return java.util.Optional.of(Long.parseLong(val));
        } catch (Exception e) {
            return java.util.Optional.empty();
        }
    }

    private java.util.Optional<Double> parseDouble(String token, String prefix) {
        try {
            int idx = token.indexOf(prefix);
            String val = token.substring(idx + prefix.length());
            return java.util.Optional.of(Double.parseDouble(val));
        } catch (Exception e) {
            return java.util.Optional.empty();
        }
    }
}
