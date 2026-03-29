package ru.baza.lab3.processes;

import java.io.*;

public class WorkerCoordinator {

    private final ChildProcessContainer container;

    public WorkerCoordinator(ChildProcessContainer container) {
        this.container = container;
    }

    public void startProcesses(String classPath, Class<?> processClass) {
        try {
            container.runProcesses(classPath, processClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeWriters() {
        container.closeWriters();
    }

    public void stopProcesses() {
        container.stopProcesses();
    }

    public void writeData(int processNumber, String data) {
        var writer = container.getProcessContext(processNumber).getWriter();
        try {
            writer.write(data);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readData(int processNumber) {
        var reader = container.getProcessContext(processNumber).getReader();
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readError(int processNumber) {
        var reader = container.getProcessContext(processNumber).getErrorReader();
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getProcessesNumber() {
        return container.getProcessesNumber();
    }
}
