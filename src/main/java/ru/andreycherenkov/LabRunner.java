package ru.andreycherenkov;

import ru.andreycherenkov.filesearcher.FileFinder;
import ru.andreycherenkov.filesearcher.Paths;
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

    private void executeLab1(Collection<Path> paths, Collection<Integer> threadCounts) {
        System.out.println("Lab1 starts");
        for (var thread: threadCounts) {
            long before = System.currentTimeMillis();
            for (var imagePath : paths) {
                int threshold = 150;
                int erosionStep = 1;
                BufferedImage image = imageProcessor.getBufferedImage(imagePath);
                int[][] binaryImage = imageProcessor.processImageUsingThreshold(image, threshold, thread);
                var erodedImage = imageProcessor.erode(binaryImage, erosionStep, thread);
                var result = imageProcessor.changeImageIntensive(erodedImage);
                imageProcessor.writeFile(result, imagePath, Paths.RESULTS_PATH_LAB_1);
            }
            long after = System.currentTimeMillis();
            long time = after - before;
            System.out.printf("Milliseconds: %d with thread count %d: \n", time, thread);
        }
    }

    private void executeLab2(Collection<Path> paths, Collection<Integer> threadCounts) {
        System.out.println("Lab2 starts");
        for (var thread : threadCounts) {
            long before = System.currentTimeMillis();
            for (var imagePath : paths) {
                var image = imageProcessor.getBufferedImage(imagePath);
                var shiftedImage = imageProcessor.shiftImage(image, 10, 10, thread); //10, 10 -  смещения по осям x и y
                var blurredImage = imageProcessor.applyBlurFilter(shiftedImage, thread);
                imageProcessor.writeFile(blurredImage, imagePath, Paths.RESULTS_PATH_LAB_2);
            }
            long after = System.currentTimeMillis();
            long time = after - before;
            System.out.printf("Milliseconds: %d with thread count %d: \n", time, thread);
        }
    }
}
