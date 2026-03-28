package ru.baza.lab1;

import static ru.baza.lab1.DefaultConfig.RED_COLOR_INDEX;
import static ru.baza.lab1.DefaultConfig.GREEN_COLOR_INDEX;
import static ru.baza.lab1.DefaultConfig.BLUE_COLOR_INDEX;
import static ru.baza.lab1.DefaultConfig.RED_PIXEL;

public class FrameProcessor {

    public static final int DEFAULT_THRESHOLD = 64;

    public void process(Frame frame) {
        var matrix = getIntensityMatrix(frame);
        var mask = getLowIntensityPixelMask(matrix, DEFAULT_THRESHOLD);
        drawRedBorders(frame, mask);
    }

    public int[][] getIntensityMatrix(Frame frame) {
        var height = frame.getHeight();
        var width = frame.getWidth();
        var matrix = new int[height][width];

        for (var row = 0; row < height; row++) {
            for (var col = 0; col < width; col++) {
                var pixel = frame.getBufferedPixel(row, col);
                var r = pixel[RED_COLOR_INDEX];
                var g = pixel[GREEN_COLOR_INDEX];
                var b = pixel[BLUE_COLOR_INDEX];
                matrix[row][col] = (int) ((r + g + b) / 3);
            }
        }
        return matrix;
    }

    public boolean[][] getLowIntensityPixelMask(int[][] intensityMatrix, int threshold) {

        var rows = intensityMatrix.length;
        var columns = intensityMatrix[0].length;

        var mask = new boolean[rows][columns];

        for (var row = 0; row < intensityMatrix.length; row++) {
            for (var column = 0; column < intensityMatrix[0].length; column++) {
                mask[row][column] = intensityMatrix[row][column] < threshold;
            }
        }
        return mask;
    }

    public void drawRedBorders(Frame frame, boolean[][] mask) {

        var height = frame.getHeight();
        var width = frame.getWidth();

        for (var row = 0; row < height; row++) {
            for (var column = 0; column < width; column++) {

                if (!mask[row][column]) {
                    continue;
                }

                var isBorder = false;

                for (var rowOffset = -1; rowOffset <= 1 && !isBorder; rowOffset++) {
                    for (var columnOffset = -1; columnOffset <= 1; columnOffset++) {

                        if (rowOffset == 0 && columnOffset == 0) {
                            continue;
                        }

                        var newRow = row + rowOffset;
                        var newColumn = column + columnOffset;

                        if (newRow < 0 || newRow >= height || newColumn < 0 || newColumn >= width) {
                            isBorder = true;
                            break;
                        }

                        if (!mask[newRow][newColumn]) {
                            isBorder = true;
                            break;
                        }
                    }
                }

                if (isBorder) {
                    frame.setPixel(row, column, RED_PIXEL);
                }
            }
        }
    }
}
