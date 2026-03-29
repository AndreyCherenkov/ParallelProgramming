package ru.baza.lab3.processes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class ChildProcessContainer {

    private final ProcessContext[] processes;

    private volatile boolean isRunning = false;

    public ChildProcessContainer(int processesNumber) {
        this.processes = new ProcessContext[processesNumber];
    }

    protected void runProcesses(String classPath, Class<?> processClass) throws IOException {
        var className = processClass.getName();
        classHasMainMethod(processClass);

        for (var i = 0; i < processes.length; i++) {
            var pb = new ProcessBuilder(
                    "java",
                    "-cp",
                    classPath,
                    className
            );
            var process = pb.start();
            var writer = getProcessWriter(process);
            var reader = getProcessReader(process);
            var errorReader = getProcessErrorReader(process);
            processes[i] = new ProcessContext.Builder()
                    .process(process)
                    .writer(writer)
                    .reader(reader)
                    .errorReader(errorReader)
                    .build();

            isRunning = true;
            System.out.printf("Process %d started\n", process.pid());
        }
    }

    protected void closeWriters() {
        for (var processContext : processes) {
            try {
                processContext.getWriter().close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void stopProcesses() {
        for (var processContext : processes) {
            var process = processContext.getProcess();
            try {
                process.destroy();
                if (!process.waitFor(5, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                }
                processContext.getReader().close();
                processContext.getErrorReader().close();
            } catch (IOException | InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            System.out.printf("Process %d destroyed\n", processContext.getProcess().pid());
        }
        isRunning = false;
    }

    protected ProcessContext getProcessContext(int contextNumber) {
        var processesNumber = processes.length;
        if (isRunning && contextNumber < processesNumber) {
            return processes[contextNumber];
        }
        throw new ArrayIndexOutOfBoundsException(String.format("Only %d processes exist", processesNumber));
    }

    protected int getProcessesNumber() {
        if (isRunning) {
            return processes.length;
        }
        throw new IllegalStateException("Processes are not running");
    }

    private void classHasMainMethod(Class<?> processClass) { //todo переписать под bool
        try {
            processClass.getMethod("main", String[].class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedWriter getProcessWriter(Process process) {
        return new BufferedWriter(
                new OutputStreamWriter(
                        process.getOutputStream(),
                        StandardCharsets.UTF_8
                )
        );
    }

    private BufferedReader getProcessReader(Process process) {
        return new BufferedReader(new InputStreamReader(process.getInputStream()));
    }

    private BufferedReader getProcessErrorReader(Process process) {
        return new BufferedReader(new InputStreamReader(process.getErrorStream()));
    }
}
