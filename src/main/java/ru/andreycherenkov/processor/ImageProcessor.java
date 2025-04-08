package ru.andreycherenkov.processor;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class ImageProcessor {

    public void writeFile(BufferedImage image, Path imagePath, String resultsPath) {
        var pStr = imagePath.toString();
        var fileName = pStr.substring(pStr.lastIndexOf(File.separator) + 1);
        try {
            var fileExtension = pStr.substring(pStr.lastIndexOf(".") + 1);
            ImageIO.write(image,
                    fileExtension,
                    new File(resultsPath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        int width = image.getWidth();
        int height = image.getHeight();
        return new int[height][width];
    }

    //Лабораторная работа 1, 2.., программа А
    public BufferedImage changeImageIntensive(int[][] erodedImage) {
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

    public int[][] erode(int[][] binaryImage, int step, int threadCount) {
        int height = binaryImage.length;
        int width = binaryImage[0].length;
        int[][] erodedImage = new int[height][width];

        try (ExecutorService executor = newFixedThreadPool(threadCount)) {
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

    public int[][] processImageUsingThreshold(BufferedImage image, int threshold, int threadCount) {
        int[][] binaryImage = getBinaryImage(image);
        int height = binaryImage.length;
        int width = binaryImage[0].length;
        try (ExecutorService executor = newFixedThreadPool(threadCount)) {
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

    //Лабораторная работа 2, программа В
    public BufferedImage shiftImage(BufferedImage image,
                                    int shiftX,
                                    int shiftY,
                                    int threadCount) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics g = newImage.getGraphics();
        g.setColor(new Color(187, 38, 73));
        g.fillRect(0, 0, width, height);
        g.dispose();

        try (ExecutorService executor = newFixedThreadPool(threadCount)) {
            for (int x = 0; x < width; x++) {
                final int finalX = x;
                executor.submit(() -> {
                    for (int y = 0; y < height; y++) {
                        int newX = finalX + shiftX;
                        int newY = y + shiftY;
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

        BufferedImage blurredImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        try (ExecutorService executor = newFixedThreadPool(threadCount)) {
            for (int x = 1; x < image.getWidth() - 1; x++) {
                final int finalX = x;
                executor.submit(() -> {
                    for (int y = 1; y < image.getHeight() - 1; y++) {
                        float r = 0, g = 0, b = 0;
                        for (int kx = -1; kx <= 1; kx++) {
                            for (int ky = -1; ky <= 1; ky++) {
                                int rgb = image.getRGB(finalX + kx, y + ky);
                                Color color = new Color(rgb);
                                r += color.getRed() * blurKernelCore[(kx + 1) * 3 + (ky + 1)];
                                g += color.getGreen() * blurKernelCore[(kx + 1) * 3 + (ky + 1)];
                                b += color.getBlue() * blurKernelCore[(kx + 1) * 3 + (ky + 1)];
                            }
                        }
                        blurredImage.setRGB(finalX, y, new Color(Math.min((int) r, 255), Math.min((int) g, 255), Math.min((int) b, 255)).getRGB());
                    }
                });
            }
            executor.shutdown();
        }
        return blurredImage;
    }
}
