package com.jvm.doctor.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "jvm-doctor", mixinStandardHelpOptions = true, version = "jvm-doctor 1.0", description = "A developer-facing CLI tool for JVM diagnosis", subcommands = {
        LiveCommand.class,
        ListCommand.class
})
public class Main implements Runnable {

    @Override
    public void run() {
        // Default behavior if no subcommand executed: show help
        new CommandLine(new Main()).usage(System.out);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
