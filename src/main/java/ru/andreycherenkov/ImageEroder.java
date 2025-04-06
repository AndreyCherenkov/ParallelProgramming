package ru.andreycherenkov;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ImageEroder {

    private static final String RESULTS_PATH = "src/main/resources/results/";

    public void writeErodedImage(Path imagePath, int[][] binaryImage, int step) throws IOException {
        BufferedImage image = ImageIO.read(new File(String.valueOf(imagePath)));
        int[][] erodedImage = erode(binaryImage, step);
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                outputImage.setRGB(x, y, erodedImage[y][x] == 1 ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
            }
        }
        String pStr = imagePath.toString();
        String fileName = pStr.substring(pStr.lastIndexOf(File.separator) + 1);
        ImageIO.write(outputImage, "png", new File(RESULTS_PATH + fileName));
    }

    private int[][] erode(int[][] binaryImage, int step) {
        int height = binaryImage.length;
        int width = binaryImage[0].length;
        int[][] erodedImage = new int[height][width];

        try (ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
            List<Future<Void>> futures = new ArrayList<>();

            for (int y = step; y < height - step; y++) {
                final int row = y;
                futures.add(executor.submit(() -> {
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
                    return null;
                }));
            }

            for (Future<Void> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return erodedImage;
    }
}
