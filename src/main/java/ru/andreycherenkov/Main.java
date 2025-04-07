package ru.andreycherenkov;

import ru.andreycherenkov.filesearcher.FileFinder;
import ru.andreycherenkov.filesearcher.ImageFinder;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Collection;

public class Main {

    private static final FileFinder imageFinder = new ImageFinder();
    private static final ImageProcessor IMAGE_PROCESSOR = new ImageProcessor();

    public static void main(String[] args) {
        long before = System.currentTimeMillis();

        Collection<Path> paths = imageFinder.findFiles();
        for (var imagePath : paths) {
            int threshold = 150;
            int erosionStep = 1;
            BufferedImage image = IMAGE_PROCESSOR.getBufferedImage(imagePath);
            int[][] binaryImage = IMAGE_PROCESSOR.processImageUsingThreshold(image, threshold);
            var result = IMAGE_PROCESSOR.getFinalImage(binaryImage, erosionStep);
            IMAGE_PROCESSOR.writeFile(result, imagePath);
        }
        long after = System.currentTimeMillis();
        long time = after - before;
        System.out.printf("Milliseconds: %d \n", time);
    }
}
