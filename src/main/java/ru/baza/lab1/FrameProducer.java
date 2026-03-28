package ru.baza.lab1;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FrameProducer {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final BlockingQueue<Frame> queue;
    public final int consumerNumber;

    public FrameProducer(BlockingQueue<Frame> queue, int consumerNumber) {
        this.queue = queue;
        this.consumerNumber = consumerNumber;
    }

    public void produce(String filePath) {
        var capture = new VideoCapture(filePath);
        isOpened(capture, filePath);
        var index = new AtomicInteger(); //todo при однопоточном режиме можно сделать non atomic -> сделать многопоточный producer?

        var frame = new Mat();
        executorService.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted() && capture.read(frame)) {
                    queue.put(new Frame(index.getAndIncrement(), frame.clone()));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                capture.release();
            }

            for (var i = 0; i < consumerNumber; i++) {
                try {
                    queue.put(Frame.POISON_PILL);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public void shutdown() throws InterruptedException {
        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }
    }

    private static void isOpened(VideoCapture capture, String filePath) {
        if (!capture.isOpened()) {
            throw new IllegalStateException("Cannot open video: " + filePath);
        }
    }
}
