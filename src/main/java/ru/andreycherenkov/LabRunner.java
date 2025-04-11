package ru.andreycherenkov;

import ru.andreycherenkov.filesearcher.FileFinder;
import ru.andreycherenkov.filesearcher.ProjectPaths;
import ru.andreycherenkov.processor.ImageProcessor;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class LabRunner {

    private final FileFinder imageFinder;
    private final ImageProcessor imageProcessor;

    public LabRunner(FileFinder imageFinder, ImageProcessor imageProcessor) {
        this.imageFinder = imageFinder;
        this.imageProcessor = imageProcessor;
    }

    public void execute() {
        Collection<Path> paths = imageFinder.findFiles();
        List<Integer> threadCount = List.of(1, 2, 4, 6, 8, 10, 12, 14, 16);
        executeLab1(paths, threadCount);
        executeLab2(paths, threadCount);
    }

    public void executeLab1(Collection<Path> paths, Collection<Integer> threadCounts) {
        System.out.println("Lab1 starts");
        for (var thread: threadCounts) {

            for (var imagePath : paths) {
                long before = System.currentTimeMillis();

                int threshold = 125;
                int erosionStep = 1;
                BufferedImage image = imageProcessor.getBufferedImage(imagePath);
                int[][] binaryImage = imageProcessor.processImageUsingThreshold(image, threshold, thread);
                var erodedImage = imageProcessor.erode(binaryImage, erosionStep, thread);
                var result = imageProcessor.changeImageIntensive(erodedImage);
                imageProcessor.writeFile(result, imagePath, ProjectPaths.RESULTS_PATH_LAB_1, thread);

                long after = System.currentTimeMillis();
                long time = after - before;
                System.out.println(imagePath);
                System.out.printf("Milliseconds: %d with thread count %d: \n", time, thread);
            }
        }
    }

    public void executeLab2(Collection<Path> paths, Collection<Integer> threadCounts) {
        System.out.println("Lab2 starts");
        for (var thread : threadCounts) {
            for (var imagePath : paths) {

                long before = System.currentTimeMillis();
                var image = imageProcessor.getBufferedImage(imagePath);
                var shiftedImage = imageProcessor.shiftImage(image, 10, 10, thread);
                var blurredImage = imageProcessor.applyBlurFilter(shiftedImage, thread);
                imageProcessor.writeFile(blurredImage, imagePath, ProjectPaths.RESULTS_PATH_LAB_2, thread);

                long after = System.currentTimeMillis();
                long time = after - before;
                System.out.println(imagePath);
                System.out.printf("Milliseconds: %d with thread count %d: \n", time, thread);
                System.out.println("/////////////////////////////////////////////");
            }
        }
    }
}
