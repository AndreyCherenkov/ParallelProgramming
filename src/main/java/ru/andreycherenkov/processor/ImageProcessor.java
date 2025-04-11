package ru.andreycherenkov.processor;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class ImageProcessor {

    public void writeFile(BufferedImage image, Path imagePath, String resultsPath, int threadCount) {
        var pStr = imagePath.toString();
        var fileName = pStr.substring(pStr.lastIndexOf(File.separator) + 1, pStr.lastIndexOf("."));

        if (!resultsPath.endsWith(File.separator)) {
            resultsPath += File.separator;
        }

        try {
            var fileExtension = pStr.substring(pStr.lastIndexOf(".") + 1);
            File outputFile = new File(resultsPath + fileName + "_threads_" + threadCount + "." + fileExtension);
            ImageIO.write(image, fileExtension, outputFile);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при записи файла: " + e.getMessage(), e);
        }
    }



    public BufferedImage getBufferedImage(Path imagePath) {
        try {
            return ImageIO.read(new File(String.valueOf(imagePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int[][] getBinaryImage(BufferedImage image) {
        var width = image.getWidth();
        var height = image.getHeight();
        return new int[height][width];
    }

    //Лабораторная работа 1, 2.., программа А
    public BufferedImage changeImageIntensive(int[][] erodedImage) {
        var height = erodedImage.length;
        var width = erodedImage[0].length;
        var outputImage = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_RGB
        );
        for (var y = 0; y < height; y++) {
            for (var x = 0; x < width; x++) {
                outputImage.setRGB(x, y, erodedImage[y][x] == 1 ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
            }
        }
        return outputImage;
    }

    public int[][] erode(int[][] binaryImage, int step, int threadCount) {
        var height = binaryImage.length;
        var width = binaryImage[0].length;
        int[][] erodedImage = new int[height][width];

        try (var executor = newFixedThreadPool(threadCount)) {
            for (var y = step; y < height - step; y++) {
                final var row = y;
                executor.submit(() -> {
                    for (var x = step; x < width - step; x++) {
                        var erodePixel = true;
                        for (var dy = -step; dy <= step; dy++) {
                            for (var dx = -step; dx <= step; dx++) {
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

    public int[][] processImageUsingThreshold(BufferedImage image, int threshold, int threadCount) {
        int[][] binaryImage = getBinaryImage(image);
        var height = binaryImage.length;
        var width = binaryImage[0].length;
        try (var executor = newFixedThreadPool(threadCount)) {
            for (var y = 0; y < height; y++) {
                final var row = y;
                executor.submit(() -> {
                    for (var x = 0; x < width; x++) {
                        var color = new Color(image.getRGB(x, row));
                        var intensity = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                        binaryImage[row][x] = intensity < threshold ? 0 : 1;
                    }
                });
            }
        }
        return binaryImage;
    }

    //Лабораторная работа 2, программа В
    public BufferedImage shiftImage(BufferedImage image,
                                    int shiftX,
                                    int shiftY,
                                    int threadCount) {
        var width = image.getWidth();
        var height = image.getHeight();
        var newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        var g = newImage.getGraphics();
        g.setColor(new Color(187, 38, 73));
        g.fillRect(0, 0, width, height);
        g.dispose();

        try (var executor = newFixedThreadPool(threadCount)) {
            for (var x = 0; x < width; x++) {
                final var finalX = x;
                executor.submit(() -> {
                    for (var y = 0; y < height; y++) {
                        var newX = finalX + shiftX;
                        var newY = y + shiftY;
                        if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                            newImage.setRGB(newX, newY, image.getRGB(finalX, y));
                        }
                    }
                });
            }
        }
        return newImage;
    }

    public BufferedImage applyBlurFilter(BufferedImage image, int threadCount) {
        float[] blurKernelCore = {
                1f / 9f, 1f / 9f, 1f / 9f,
                1f / 9f, 1f / 9f, 1f / 9f,
                1f / 9f, 1f / 9f, 1f / 9f
        };

        var blurredImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        try (var executor = newFixedThreadPool(threadCount)) {
            for (var x = 1; x < image.getWidth() - 1; x++) {
                final var finalX = x;
                executor.submit(() -> {
                    for (var y = 1; y < image.getHeight() - 1; y++) {
                        float r = 0, g = 0, b = 0;
                        for (var kx = -1; kx <= 1; kx++) {
                            for (var ky = -1; ky <= 1; ky++) {
                                var rgb = image.getRGB(finalX + kx, y + ky);
                                var color = new Color(rgb);
                                r += color.getRed() * blurKernelCore[(kx + 1) * 3 + (ky + 1)];
                                g += color.getGreen() * blurKernelCore[(kx + 1) * 3 + (ky + 1)];
                                b += color.getBlue() * blurKernelCore[(kx + 1) * 3 + (ky + 1)];
                            }
                        }
                        blurredImage.setRGB(finalX, y, new Color(Math.min((int) r, 255), Math.min((int) g, 255), Math.min((int) b, 255)).getRGB());
                    }
                });
            }
        }
        return blurredImage;
    }
}
