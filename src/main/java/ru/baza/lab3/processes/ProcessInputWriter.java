package ru.baza.lab3.processes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ProcessInputWriter {

    private final ChildProcessContainer container;
    private final Map<Integer, Writer> processWriterMap;

    protected ProcessInputWriter(ChildProcessContainer container, Map<Integer, Writer> processWriterMap) {
        this.container = container;
        this.processWriterMap = processWriterMap;
    }

    protected Writer getWriter(int processNumber) {
        var processesNumber = container.getNumberProcesses();
        if (processNumber >= processesNumber) {
            throw new IndexOutOfBoundsException(String.format("Only %d child processes exist", processesNumber));
        }
        return processWriterMap.get(processNumber);
    }

    protected void initWriters() {
        for (var i = 0; i < container.getNumberProcesses(); i++) {
            var writer = getProcessWriter(i);
            processWriterMap.put(i, writer);
        }
    }

    public void closeWriters() throws IOException {
        for (var i = 0; i < container.getNumberProcesses(); i++) {
            processWriterMap.get(i).close();
        }
    }

    private BufferedWriter getProcessWriter(int processNumber) {
        return new BufferedWriter(
                new OutputStreamWriter(
                        container.getProcess(processNumber).getOutputStream(),
                        StandardCharsets.UTF_8
                )
        );
    }
}
