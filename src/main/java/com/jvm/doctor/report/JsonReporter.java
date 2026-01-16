package com.jvm.doctor.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jvm.doctor.model.DiagnosisContext;
import com.jvm.doctor.model.Finding;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonReporter {
    private final ObjectMapper mapper;

    public JsonReporter() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void report(DiagnosisContext ctx, List<Finding> findings, File outputFile, boolean verbose)
            throws IOException {
        ObjectNode root = mapper.createObjectNode();

        // Meta
        ObjectNode meta = root.putObject("meta");
        meta.put("toolVersion", "1.0.0");
        meta.put("timestamp", ctx.getTimestamp() != null ? ctx.getTimestamp().toString() : "");
        meta.put("pid", ctx.getPid());
        meta.put("profile", ctx.getProfile());

        // Snapshot
        ObjectNode snapshot = root.putObject("snapshot");
        snapshot.putPOJO("vm", ctx.getVmInfo());
        snapshot.putPOJO("flags", ctx.getVmFlags());
        snapshot.putPOJO("heap", ctx.getHeapInfo());
        snapshot.putPOJO("threads", ctx.getThreadInfo());

        // Samples
        ObjectNode samples = root.putObject("samples");
        samples.putPOJO("jstatGcutil", ctx.getJstatSamples());

        // Findings
        ArrayNode findingsNode = root.putArray("findings");
        for (Finding f : findings) {
            findingsNode.addPOJO(f);
        }

        // Raw (if verbose)
        if (verbose) {
            ObjectNode raw = root.putObject("raw");
            if (ctx.getVmInfo() != null)
                raw.put("VM.version", ctx.getVmInfo().toString()); // Simplified
            if (ctx.getHeapInfo() != null)
                raw.put("GC.heap_info", ctx.getHeapInfo().getRawOutput());
            // In a real impl, we'd store the raw strings in context
        }

        if (outputFile != null) {
            mapper.writeValue(outputFile, root);
            System.out.println("JSON report written to " + outputFile.getAbsolutePath());
        } else {
            // Print to stdout (or return string? usually file for JSON)
            System.out.println(mapper.writeValueAsString(root));
        }
    }
}
