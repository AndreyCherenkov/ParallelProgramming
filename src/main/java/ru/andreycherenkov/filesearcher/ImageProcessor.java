package ru.andreycherenkov.filesearcher;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageProcessor {

    public static BufferedImage loadImage(String filePath) throws IOException {
        return ImageIO.read(new File(filePath));
    }

    public static BufferedImage shiftImage(BufferedImage image, int shiftX, int shiftY) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Заполнение нового изображения цветом (RGB: 187, 38, 73)
        Graphics g = newImage.getGraphics();
        g.setColor(new Color(187, 38, 73));
        g.fillRect(0, 0, width, height);
        g.dispose();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int newX = x + shiftX;
                int newY = y + shiftY;
                if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                    newImage.setRGB(newX, newY, image.getRGB(x, y));
                }
            }
        }
        return newImage;
    }

    public static BufferedImage applyBlurFilter(BufferedImage image) {
        float[] kernel = {
                1f / 16f, 2f / 16f, 1f / 16f,
                2f / 16f, 4f / 16f, 2f / 16f,
                1f / 16f, 2f / 16f, 1f / 16f
        };

        BufferedImage blurredImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 1; x < image.getWidth() - 1; x++) {
            for (int y = 1; y < image.getHeight() - 1; y++) {
                float r = 0, g = 0, b = 0;
                for (int kx = -1; kx <= 1; kx++) {
                    for (int ky = -1; ky <= 1; ky++) {
                        int rgb = image.getRGB(x + kx, y + ky);
                        Color color = new Color(rgb);
                        r += color.getRed() * kernel[(kx + 1) * 3 + (ky + 1)];
                        g += color.getGreen() * kernel[(kx + 1) * 3 + (ky + 1)];
                        b += color.getBlue() * kernel[(kx + 1) * 3 + (ky + 1)];
                    }
                }
                blurredImage.setRGB(x, y, new Color(Math.min((int) r, 255), Math.min((int) g, 255), Math.min((int) b, 255)).getRGB());
            }
        }
        return blurredImage;
    }

    public static void processImage(String filePath, int shiftX, int shiftY) {
        try {
            BufferedImage image = loadImage(filePath);
            BufferedImage shiftedImage = shiftImage(image, shiftX, shiftY);
            BufferedImage blurredImage = applyBlurFilter(shiftedImage);
            ImageIO.write(blurredImage, "jpg", new File(filePath));
            System.out.println("Processed " + filePath);
        } catch (IOException e) {
            System.err.println("Error processing " + filePath + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String[] fileSizes = {"10240x7680.jpg", "12800x9600.jpg", "20480x15360.jpg"};
        int[] threadCounts = {2, 4, 6, 8, 10, 12, 14, 16};

        for (var path: new ImageFinder().findFiles()){
            for (int numThreads : threadCounts) {
                ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
                long startTime = System.currentTimeMillis();

                for (String filePath : fileSizes) {
                    executorService.submit(() -> processImage(String.valueOf(path), 1, 0));
                    executorService.submit(() -> processImage(String.valueOf(path), 0, 1));
                }

                executorService.shutdown();
                while (!executorService.isTerminated()) {
                    // Ждем завершения всех потоков
                }

                long endTime = System.currentTimeMillis();
                System.out.println("Processing time with " + numThreads + " threads: " + (endTime - startTime) + " ms");
            }
        }
    }
}
