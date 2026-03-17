package ru.baza;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.*;

public class Main {

    static {
        nu.pattern.OpenCV.loadLocally();
    }

    private static final String PATH_15_SEC = "videos/15sec.mp4";
    private static final String PATH_30_SEC = "videos/30sec.mp4";
    private static final String PATH_60_SEC = "videos/60sec.mp4";

    public static void main(String[] args) throws Exception {
        var consumerNum = 8;
        var consumerPool = Executors.newFixedThreadPool(consumerNum);

        var queue = new ArrayBlockingQueue<Frame>(100);

        var producer = new FrameProducer(queue);
        var consumer = new FrameConsumer(queue, consumerPool, new FrameProcessor());

        var start = System.nanoTime();
        producer.produce(PATH_30_SEC);

        var futures = new ArrayList<Future<?>>();
        for (var i = 0; i < consumerNum; i++) {
            futures.add(consumer.consume(64));
        }
        // ⬇️ ЖДЁМ завершения consumer


        for (Future<?> f : futures) {
            f.get();
        }
        // ⬇️ shutdown пулов
        producer.shutdown();
        consumerPool.shutdown();

        // ⬇️ сохраняем видео
        var frames = new ArrayList<>(consumer.getProcessedFrames());

        var saver = new VideoSaver();
        saver.saveVideo(frames, 30, "new_output.mp4", true);
        var end = System.nanoTime();
        var total = (end - start) / Math.pow(10, 9);
        System.out.println("Total time: " + total);
    }

//    private static void runTests(String inputVideoPath, String outputFileName) {
//        var loader = new FrameProducer();
//        var processor = new FrameProcessor();
//        var saver = new VideoSaver();
//
//        System.out.println("================================");
//        System.out.println("Tests for " + inputVideoPath);
//        for (var threads = 1; threads <= 16; threads *= 2) {
//            var loadAvg = 0.0;
//            var avg = 0.0;
//
//            for (var iteration = 0; iteration < 3; iteration++) {
//                var s = System.nanoTime();
//                var frames = loader.produce(inputVideoPath);
//                var e = System.nanoTime();
//                var t = (e - s)  / 1_000_000_000.0;
//                loadAvg += t;
//
//                var start = System.nanoTime();
//                try (var executor = Executors.newFixedThreadPool(threads)) {
//
//                    for (var frame : frames) {
//                        executor.submit(() -> {
//                            var matrix = processor.getIntensityMatrix(frame);
//                            var mask = processor.getLowIntensityPixelMask(matrix, 64);
//                            processor.drawRedBorders(frame, mask);
//                        });
//                    }
//
//                    executor.shutdown();
//                }
//
//                saver.saveVideo(frames, 25, outputFileName, true);
//
//                var end = System.nanoTime();
//                var total = (end - start) / 1_000_000_000.0;
//                avg += total;
//            }
//
//            System.out.println("----- Average for " + threads + " threads: " + (avg / 3) + " | " + "load time: " + (loadAvg / 3) + " -----");
//        }
//        System.out.println("Saved video: " + outputFileName);
//        System.out.println("================================");
//    }
}