package ru.baza;

import org.opencv.core.Mat;

import static java.lang.Integer.compare;
import static ru.baza.DefaultConfig.RGB_FORMAT;

public final class Frame implements Comparable<Frame> {

    public static final Frame POISON_PILL = new Frame(-1, null);

    private final Mat matrix;
    private final ThreadLocal<double[]> buffer = ThreadLocal.withInitial(() -> new double[RGB_FORMAT]);
    private final int index;

    public Frame(int index, Mat matrix) {
        this.index = index;
        this.matrix = matrix;
    }

    public Mat getMatrix() {
        return matrix;
    }

    public void setPixel(int row, int column, double[] channels) {
        matrix.put(row, column, channels);
    }

    //Возвращает пиксель в трех каналах (при CV_8UC3)
    public double[] getPixel(int row, int column) {
        return matrix.get(row, column);
    }

    public double[] getBufferedPixel(int x, int y) {
        var buf = buffer.get();
        var pixel = matrix.get(x, y);

        buf[0] = pixel[0];
        buf[1] = pixel[1];
        buf[2] = pixel[2];

        return buf;
    }

    public int getWidth() {
        return matrix.width();
    }

    public int getHeight() {
        return matrix.height();
    }

    @Override
    public int compareTo(Frame other) {
        if (this == POISON_PILL) return 1;
        if (other == POISON_PILL) return -1;
        return compare(this.index, other.index);
    }
}
