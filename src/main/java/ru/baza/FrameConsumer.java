package ru.baza;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.*;

import static ru.baza.Frame.POISON_PILL;

public class FrameConsumer {

    private final BlockingQueue<Frame> queue;
    private final ExecutorService executorService;
    private final FrameProcessor frameProcessor;

    //todo протестировать с обоими вариантами/уйти от хранения кадров в памяти; убрать у потребителя обязанность по хранению обработанных кадров
//    private final ConcurrentLinkedQueue<Frame> processedFrames = new ConcurrentLinkedQueue<>();
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

    public Future<?> consume() {
        return executorService.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    var frame = queue.take();
                    if (POISON_PILL == frame) {
                        break;
                    }
                    frameProcessor.process(frame);
                    processedFrames.add(frame);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public Collection<Frame> getProcessedFrames() {
        return Collections.unmodifiableCollection(processedFrames);
    }
}
