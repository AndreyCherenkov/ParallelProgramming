package ru.baza.lab3.processes;

import java.io.BufferedReader;
import java.io.BufferedWriter;

public final class ProcessContext {
    private final Process process;
    private final BufferedWriter writer;
    private final BufferedReader reader;
    private final BufferedReader errorReader;

    private ProcessContext(Process process, BufferedWriter writer, BufferedReader reader, BufferedReader errorReader) {
        this.process = process;
        this.writer = writer;
        this.reader = reader;
        this.errorReader = errorReader;
    }

    public Process getProcess() {
        return process;
    }

    public BufferedWriter getWriter() {
        return writer;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public BufferedReader getErrorReader() {
        return errorReader;
    }

    public static class Builder {
        private Process process;
        private BufferedWriter writer;
        private BufferedReader reader;
        private BufferedReader errorReader;

        public Builder process(Process process) {
            this.process = process;
            return this;
        }

        public Builder writer(BufferedWriter writer) {
            this.writer = writer;
            return this;
        }

        public Builder reader(BufferedReader reader) {
            this.reader = reader;
            return this;
        }

        public Builder errorReader(BufferedReader errorReader) {
            this.errorReader = errorReader;
            return this;
        }

        public ProcessContext build() {
            return new ProcessContext(
                    process,
                    writer,
                    reader,
                    errorReader
            );
        }
    }
}
