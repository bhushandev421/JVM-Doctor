package com.jvm.doctor.parsers;

import com.jvm.doctor.model.JstatSample;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JstatGcutilParser {

    /*
     * S0 S1 E O M CCS YGC YGCT FGC FGCT GCT
     * 0.00 100.00 6.16 16.89 94.20 88.75 2 0.005 0 0.000 0.005
     */

    public List<JstatSample> parse(List<String> lines) {
        List<JstatSample> samples = new ArrayList<>();
        if (lines == null || lines.size() < 2)
            return samples;

        // Header is line 0
        String[] headers = lines.get(0).trim().split("\\s+");

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            String[] parts = line.split("\\s+");

            if (parts.length != headers.length)
                continue;

            // Assume 1s interval so just use index as relative timestamp
            JstatSample sam = new JstatSample(i);
            Map<String, Double> val = new HashMap<>();

            for (int k = 0; k < headers.length; k++) {
                try {
                    val.put(headers[k], Double.parseDouble(parts[k]));
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
            sam.setValues(val);
            samples.add(sam);
        }

        return samples;
    }
}
