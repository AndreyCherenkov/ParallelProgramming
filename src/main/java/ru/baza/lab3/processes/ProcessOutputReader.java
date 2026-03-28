package ru.baza.lab3.processes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

public class ProcessOutputReader {

    private final ChildProcessContainer container;
    private final Map<Integer, BufferedReader> processReaderMap;
    private final Map<Integer, BufferedReader> processErrorReaderMap;

    public ProcessOutputReader(
            ChildProcessContainer container,
            Map<Integer, BufferedReader> processReaderMap,
            Map<Integer, BufferedReader> processErrorReaderMap
    ) {
        this.container = container;
        this.processReaderMap = processReaderMap;
        this.processErrorReaderMap = processErrorReaderMap;
    }

    protected void initReaders() {
        for (var i = 0; i < container.getNumberProcesses(); i++) {
            var reader = getProcessReader(i);
            var errorReader = getProcessErrorReader(i);
            processReaderMap.put(i, reader);
            processErrorReaderMap.put(i, errorReader);
        }
    }

    protected BufferedReader getReader(int processNumber) {
        var processesNumber = container.getNumberProcesses();
        if (processNumber >= processesNumber) {
            throw new IndexOutOfBoundsException(String.format("Only %d child processes exist", processesNumber));
        }
        return processReaderMap.get(processNumber);
    }

    protected BufferedReader getErrorReader(int processNumber) {
        var processesNumber = container.getNumberProcesses();
        if (processNumber >= processesNumber) {
            throw new IndexOutOfBoundsException(String.format("Only %d child processes exist", processesNumber));
        }
        return processErrorReaderMap.get(processNumber);
    }

    private BufferedReader getProcessReader(int processNumber) {
        return new BufferedReader(new InputStreamReader(container.getProcess(processNumber).getInputStream()));
    }

    private BufferedReader getProcessErrorReader(int processNumber) {
        return new BufferedReader(new InputStreamReader(container.getProcess(processNumber).getErrorStream()));
    }
}
