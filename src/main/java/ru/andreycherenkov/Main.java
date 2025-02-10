package ru.andreycherenkov;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;

public class Main {

    private static final String SOURCE_PATH = "src/main/resources/";
    private static final String RESULTS_PATH = "src/main/resources/results/";
    private static final List<Long> TIMES_LIST = new ArrayList<>();

    public static void main(String[] args) {
        long before = System.currentTimeMillis();

        try {
            BufferedImage image = ImageIO.read(new File(SOURCE_PATH + "input.jpg"));

            int threshold = 150;
            int erosionStep = 2;

            // Преобразование изображения в черно-белое по порогу
            int width = image.getWidth();
            int height = image.getHeight();
            int[][] binaryImage = new int[height][width];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color color = new Color(image.getRGB(x, y));
                    int intensity = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                    binaryImage[y][x] = intensity < threshold ? 0 : 1;
                }
            }

            // Выполнение эрозии
            int[][] erodedImage = erode(binaryImage, erosionStep);

            // Создание выходного изображения
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    outputImage.setRGB(x, y, erodedImage[y][x] == 1 ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
                }
            }

            // Сохранение результата в файл
            ImageIO.write(outputImage, "png", new File(RESULTS_PATH + "output.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        long after = System.currentTimeMillis();
        long time = after - before;
        TIMES_LIST.add(time);
        System.out.printf("Milliseconds: %d \n", time);
        System.out.printf("Среднее время выполнения программы: %f", getAverageTime(TIMES_LIST));
    }

    private static int[][] erode(int[][] binaryImage, int step) {
        int height = binaryImage.length;
        int width = binaryImage[0].length;
        int[][] erodedImage = new int[height][width];

        for (int y = step; y < height - step; y++) {
            for (int x = step; x < width - step; x++) {
                boolean erodePixel = true;
                for (int dy = -step; dy <= step; dy++) {
                    for (int dx = -step; dx <= step; dx++) {
                        if (binaryImage[y + dy][x + dx] == 0) {
                            erodePixel = false;
                            break;
                        }
                    }
                    if (!erodePixel) break;
                }
                erodedImage[y][x] = erodePixel ? 1 : 0;
            }
        }

        return erodedImage;
    }

    //среднее время выполнения программы
    private static double getAverageTime(List<Long> times) {
        LongSummaryStatistics statistics = times.stream()
                .mapToLong(Long::longValue)
                .summaryStatistics();

        return statistics.getAverage();
    }

}