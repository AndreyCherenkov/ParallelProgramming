package ru.baza.lab3.processes;

import java.io.*;
import java.util.HashMap;

public class WorkerCoordinator {

    private final ChildProcessContainer container;
    private final ProcessInputWriter processInputWriter;
    private final ProcessOutputReader processOutputReader;

    public WorkerCoordinator(ChildProcessContainer container) {
        this.container = container;
        this.processInputWriter = new ProcessInputWriter(container, new HashMap<>()); //todo фабрики
        this.processOutputReader = new ProcessOutputReader(container, new HashMap<>(), new HashMap<>()); //todo фабрики
    }

    public void startProcesses(String classPath, Class<?> processClass) {
        try {
            container.runProcesses(classPath, processClass);
            processInputWriter.initWriters();
            processOutputReader.initReaders();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopProcesses() {
        container.stopProcesses();
    }

    public void closeWriters() {
        try {
            processInputWriter.closeWriters();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeData(int processNumber, String data) {
        var writer = processInputWriter.getWriter(processNumber);
        try {
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readData(int processNumber) {
        var reader = processOutputReader.getReader(processNumber);
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readError(int processNumber) {
        var reader = processOutputReader.getErrorReader(processNumber);
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
