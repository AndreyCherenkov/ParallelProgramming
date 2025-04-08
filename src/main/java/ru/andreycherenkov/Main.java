package ru.andreycherenkov;

import ru.andreycherenkov.filesearcher.FileFinder;
import ru.andreycherenkov.filesearcher.ImageFinder;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final FileFinder imageFinder = new ImageFinder();
    private static final ImageProcessor IMAGE_PROCESSOR = new ImageProcessor();

    public static void main(String[] args) {
        Collection<Path> paths = imageFinder.findFiles();
        List<Integer> threadCount = List.of(2, 4, 6, 8, 10, 12, 14, 16);

        executeLab1(paths, threadCount);
//        executeLab2(paths);
    }

    private static void executeLab1(Collection<Path> paths, List<Integer> threadCounts) {
        for (var thread: threadCounts) {
            long before = System.currentTimeMillis();
            for (var imagePath : paths) {
                int threshold = 150;
                int erosionStep = 1;
                BufferedImage image = IMAGE_PROCESSOR.getBufferedImage(imagePath);
                int[][] binaryImage = IMAGE_PROCESSOR.processImageUsingThreshold(image, threshold, thread);
                var erodedImage = IMAGE_PROCESSOR.erode(binaryImage, erosionStep, thread);
                var result = IMAGE_PROCESSOR.changeImageIntensive(erodedImage);
                IMAGE_PROCESSOR.writeFile(result, imagePath, Paths.RESULTS_PATH_LAB_1);
            }
            long after = System.currentTimeMillis();
            long time = after - before;
            System.out.printf("Milliseconds: %d with thread count %d: \n", time, thread);
        }
    }

    private static void executeLab2(Collection<Path> paths, List<Integer> threadCounts) {
        for (int thread : threadCounts) {
            ExecutorService executorService = Executors.newFixedThreadPool(thread);
            long startTime = System.currentTimeMillis();

            for (var filePath : paths) {
                executorService.submit(() -> IMAGE_PROCESSOR.processImage(filePath, 25, 25));
            }

            executorService.shutdown();
            long endTime = System.currentTimeMillis();
            System.out.println("Processing time with " + thread + " threads: " + (endTime - startTime) + " ms");
        }
    }
}
