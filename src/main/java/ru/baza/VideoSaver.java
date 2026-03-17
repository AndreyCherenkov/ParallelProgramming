package ru.baza;

import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


//todo сделать потоковое сохранение
public class VideoSaver {
    private final int fps;

    private static final int FPS_30 = 30;

    private final ConcurrentHashMap<Integer, Frame> frameMap = new ConcurrentHashMap<>();

    public VideoSaver(int fps) {
        this.fps = fps;
    }

    public VideoSaver() {
        this(FPS_30);
    }

    public void saveVideo(List<Frame> frames, String filePath, boolean isColorVideo) {
        var frame = frames.getFirst();
        var width = frame.getWidth();
        var height = frame.getHeight();

        var frameSize = new Size(width, height);

        var videoWriter = new VideoWriter(
                filePath,
                VideoWriter.fourcc('m','p','4','v'),
                fps,
                frameSize,
                isColorVideo
        );
        try {
            if (!videoWriter.isOpened()) {
                throw new IllegalStateException("VideoWriter isn't open");
            }

            frames.forEach(f -> videoWriter.write(f.getMatrix()));
        } finally {
            videoWriter.release();
        }
    }
}
