package ru.baza;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

//todo фабрики для consumer-producer, сборки программы
public class Main {

    static {
        nu.pattern.OpenCV.loadLocally();
    }

    private static final Collection<String> files = List.of(
            "videos/15sec.mp4",
            "videos/30sec.mp4",
            "videos/60sec.mp4"
    );

    public static void main(String[] args) throws Exception {
        for (var file: files) {
            System.out.println("Filename: " + file);
            for (var consumerNum = 1; consumerNum <= 8; consumerNum*=2) { //todo больше 12 потребителей лучше не делать
                var avg = 0.0;
                for (var i = 0; i < 3; i++) {
                    var consumerPool = Executors.newFixedThreadPool(consumerNum);

                    var queue = new ArrayBlockingQueue<Frame>(100);

                    var producer = new FrameProducer(queue, consumerNum);
                    var consumer = new FrameConsumer(queue, consumerPool, new FrameProcessor());

                    var start = System.nanoTime();
                    producer.produce(file);

                    var futures = new ArrayList<Future<?>>();
                    for (var j = 0; j < consumerNum; j++) {
                        futures.add(consumer.consume());
                    }

                    for (Future<?> f : futures) {
                        f.get();
                    }
                    producer.shutdown();
                    consumerPool.shutdown();

                    var frames = new ArrayList<>(consumer.getProcessedFrames());

                    var saver = new VideoSaver();
                    saver.saveVideo(frames,  file + ".mp4", true); //todo refactor
                    var end = System.nanoTime();
                    var total = (end - start) / Math.pow(10, 9);
                    avg += total;
                }
                System.out.println("===================================");
                System.out.println("Num threads (consumers): " + consumerNum);
                System.out.println("Avg time: " + (avg / 3));
                System.out.println("===================================");
            }
        }
    }
}