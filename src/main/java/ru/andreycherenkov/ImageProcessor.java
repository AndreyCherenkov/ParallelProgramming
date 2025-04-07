package ru.andreycherenkov;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageProcessor {

    private static final String RESULTS_PATH = getPathForResults();

    public void writeFile(BufferedImage image, Path imagePath) {
        var pStr = imagePath.toString();
        var fileName = pStr.substring(pStr.lastIndexOf(File.separator) + 1);
        try {
            var fileExtension = pStr.substring(pStr.lastIndexOf(".") + 1);
            ImageIO.write(image,
                    fileExtension,
                    new File(RESULTS_PATH + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedImage getFinalImage(int[][] binaryImage, int step) {
        int[][] erodedImage = erode(binaryImage, step);
        int height = erodedImage.length;
        int width = erodedImage[0].length;
        var outputImage = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_RGB
        );
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                outputImage.setRGB(x, y, erodedImage[y][x] == 1 ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
            }
        }
        return outputImage;
    }

    public int[][] erode(int[][] binaryImage, int step) {
        int height = binaryImage.length;
        int width = binaryImage[0].length;
        int[][] erodedImage = new int[height][width];

        try (ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
            for (int y = step; y < height - step; y++) {
                final int row = y;
                executor.submit(() -> {
                    for (int x = step; x < width - step; x++) {
                        boolean erodePixel = true;
                        for (int dy = -step; dy <= step; dy++) {
                            for (int dx = -step; dx <= step; dx++) {
                                if (binaryImage[row + dy][x + dx] == 0) {
                                    erodePixel = false;
                                    break;
                                }
                            }
                            if (!erodePixel) break;
                        }
                        erodedImage[row][x] = erodePixel ? 1 : 0;
                    }
                });
            }
        }


        return erodedImage;
    }

    public int[][] processImageUsingThreshold(BufferedImage image, int threshold) {
        int[][] binaryImage = getBinaryImage(image);
        int height = binaryImage.length;
        int width = binaryImage[0].length;

        try (ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
            for (int y = 0; y < height; y++) {
                final int row = y;
                executor.submit(() -> {
                    for (int x = 0; x < width; x++) {
                        Color color = new Color(image.getRGB(x, row));
                        int intensity = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                        binaryImage[row][x] = intensity < threshold ? 0 : 1;
                    }
                });
            }
        }

        return binaryImage;
    }

    public BufferedImage getBufferedImage(Path imagePath) {
        try {
            return ImageIO.read(new File(String.valueOf(imagePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getPathForResults() {
        String path = ". src main resources results ";
        return path.replace(" ", File.separator);
    }

    private int[][] getBinaryImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        return new int[height][width];
    }
}
