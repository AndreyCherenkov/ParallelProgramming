package ru.baza;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class FrameConsumer {

    private final BlockingQueue<Frame> queue;
    private final ExecutorService executorService;
    private final FrameProcessor frameProcessor;
    private final ConcurrentSkipListSet<Frame> processedFrames = new ConcurrentSkipListSet<>();

    public FrameConsumer(
            BlockingQueue<Frame> queue,
            ExecutorService executorService,
            FrameProcessor frameProcessor
    ) {
        this.queue = queue;
        this.executorService = executorService;
        this.frameProcessor = frameProcessor;
    }

    public Future<?> consume(int threshold) {
        return executorService.submit(() -> {
            while (true) {
                try {
                    var frame = queue.take();
                    if (frame.equals(Frame.POISON_PILL)) {
                        break;
                    }
                    frameProcessor.process(frame, threshold);
                    processedFrames.add(frame);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public ConcurrentSkipListSet<Frame> getProcessedFrames() {
        return processedFrames;
    }
}
