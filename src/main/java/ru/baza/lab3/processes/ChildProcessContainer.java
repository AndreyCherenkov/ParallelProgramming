package ru.baza.lab3.processes;

import java.io.*;

public class ChildProcessContainer {

    private final Process[] processes;

    public ChildProcessContainer(int processesNumber) {
        this.processes = new Process[processesNumber];
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
            processes[i] = process;
            System.out.println(String.format("Process %d started", process.pid())); //todo why String.format() is redundant?
        }
    }

    protected void stopProcesses() {
        for (var process : processes) {
            if (process != null) {
                process.destroy();
                System.out.println(String.format("Process %d destroyed", process.pid()));
            }
        }
    }

    protected Process getProcess(int processNumber) {
        if (processNumber >= processes.length) {
            throw new IndexOutOfBoundsException(String.format("Only %d child processes exist", processes.length));
        }
        return processes[processNumber];
    }

    protected int getNumberProcesses() {
        return processes.length;
    }

    private void classHasMainMethod(Class<?> processClass) { //todo переписать под bool
        try {
            processClass.getMethod("main", String[].class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
