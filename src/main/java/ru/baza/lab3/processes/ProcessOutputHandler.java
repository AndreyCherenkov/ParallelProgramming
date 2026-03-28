package ru.baza.lab3.processes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ProcessOutputHandler {
    public static final String POISON_PILL = "__END__";

    private final ConcurrentHashMap<Integer, List<Future<?>>> processFutures = new ConcurrentHashMap<>();
    private final BlockingQueue<String> actors = new LinkedBlockingDeque<>();
    private final WorkerCoordinator coordinator;
    private final ExecutorService executor;

    public ProcessOutputHandler(
            WorkerCoordinator coordinator,
            ExecutorService executor
    ) {
        this.coordinator = coordinator;
        this.executor = executor;
    }

    public void registerProcess(int processNumber) {
        //todo проверка числа процессов

        var futures = new ArrayList<Future<?>>();

        futures.add(executor.submit(() -> {
            String actorLine;
            try {
                while ((actorLine = coordinator.readData(processNumber)) != null) {
                    actors.put(actorLine);
                }
                actors.put(POISON_PILL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));

        futures.add(executor.submit(() -> {
            String error;
            while ((error = coordinator.readError(processNumber)) != null) {
                throw new RuntimeException(error);
            }
        }));

        processFutures.put(processNumber, futures);
    }

    public String takeActor() throws InterruptedException {
        return actors.take();
    }

    public void awaitCompletion() {
        processFutures.values().forEach(futures -> {
            futures.forEach(f -> {
                try {
                    f.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    public void stop() {
        executor.shutdown();
    }
}
