package ru.baza.lab3;

import ru.baza.lab3.parser.BestFilmsParser;
import ru.baza.lab3.processes.*;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;

import static ru.baza.lab3.processes.ProcessOutputHandler.POISON_PILL;

//todo уйти от map -> сделать process context
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        var start = System.nanoTime();

        var processNumber = 3;

        var parser = new BestFilmsParser();
        var mainPage = "https://en.wikipedia.org/wiki/Academy_Award_for_Best_Picture";
        var coordinator = new WorkerCoordinator(new ChildProcessContainer(processNumber)); //todo фабрики
        var handler = new ProcessOutputHandler(coordinator, Executors.newCachedThreadPool());
        coordinator.startProcesses(System.getProperty("java.class.path"), CastWorker.class);
        handler.registerProcess(0);
        handler.registerProcess(1);
        handler.registerProcess(2);

        parser.parse(URI.create(mainPage).toURL(), 2000, BestFilmsParser::toURL)
                .map(url -> url + "\n")
                .forEach(url -> {
                    coordinator.writeData(new Random().nextInt(3), url);
                });
        coordinator.closeWriters();

        var result = new HashMap<String, Integer>();

        while (processNumber != 0) {
            String actor = handler.takeActor();

            if (actor.equals(POISON_PILL)) {
                processNumber--;
                continue;
            }
            result.merge(actor, 1, Integer::sum);
        }

        handler.awaitCompletion();
        handler.stop();
        coordinator.stopProcesses();

        System.out.println(result.size());
        result.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
//                .limit(3)
                .forEach(entry -> System.out.println(entry.getKey() + "=" + entry.getValue()));

        var end = System.nanoTime();
        var total = (end - start) / Math.pow(10, 9);
        System.out.println("Total: " + total);
    }
}
