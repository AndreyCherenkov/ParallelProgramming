package ru.andreycherenkov;

import ru.andreycherenkov.filesearcher.FileFinder;
import ru.andreycherenkov.filesearcher.ImageFinder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    private static final String RESULTS_PATH = "src/main/resources/results/";

    private static final FileFinder imageFinder = new ImageFinder();
    private static final ImageEroder imageEroder = new ImageEroder();

//    private ExecutorService executor;

    public static void main(String[] args) {
        long before = System.currentTimeMillis();

        Collection<Path> paths = imageFinder.findFiles();
        try {
            for (var imagePath : paths) {
                BufferedImage image = ImageIO.read(new File(String.valueOf(imagePath)));

                int threshold = 150;
                int erosionStep = 1;

                int width = image.getWidth();
                int height = image.getHeight();
                int[][] binaryImage = new int[height][width];

                try (ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
                    List<Future<Void>> futures = new ArrayList<>();

                    for (int y = 0; y < height; y++) {
                        final int row = y;
                        futures.add(executor.submit(() -> {
                            for (int x = 0; x < width; x++) {
                                Color color = new Color(image.getRGB(x, row));
                                int intensity = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                                binaryImage[row][x] = intensity < threshold ? 0 : 1;
                            }
                            return null;
                        }));
                    }

                    for (Future<Void> future : futures) {
                        future.get();
                    }
                }

                imageEroder.writeErodedImage(imagePath, binaryImage, erosionStep);
            }

        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        long after = System.currentTimeMillis();
        long time = after - before;
        System.out.printf("Milliseconds: %d \n", time);
    }
}
