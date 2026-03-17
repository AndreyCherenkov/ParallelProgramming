package ru.baza;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FrameProducer {

    public static final Integer CONSUMERS_NUMBER = 8;

    private final BlockingQueue<Frame> queue;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public FrameProducer(BlockingQueue<Frame> queue) {
        this.queue = queue;
    }

    public void produce(String filePath) {
        var capture = new VideoCapture(filePath);
        isOpened(capture, filePath);
        var index = new AtomicInteger();

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

            for (var i = 0; i < CONSUMERS_NUMBER; i++) {
                try {
                    queue.put(Frame.POISON_PILL);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }

    private static void isOpened(VideoCapture capture, String filePath) {
        if (!capture.isOpened()) {
            throw new IllegalStateException("Cannot open video: " + filePath);
        }
    }
}
