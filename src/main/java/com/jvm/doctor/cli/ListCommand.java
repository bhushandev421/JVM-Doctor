package com.jvm.doctor.cli;

import com.jvm.doctor.collectors.ProcessLister;
import picocli.CommandLine.Command;

import java.util.List;

@Command(name = "list", description = "Show java processes")
public class ListCommand implements Runnable {

    @Override
    public void run() {
        ProcessLister lister = new ProcessLister();
        List<ProcessLister.JvmProcess> procs = lister.listProcesses();

        System.out.println("PID\tName");
        System.out.println("---\t----");
        for (ProcessLister.JvmProcess p : procs) {
            System.out.printf("%d\t%s%n", p.pid, p.name);
        }
    }
}
