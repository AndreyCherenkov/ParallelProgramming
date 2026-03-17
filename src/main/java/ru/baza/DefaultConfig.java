package ru.baza;

public interface DefaultConfig {

    int RGB_FORMAT = 3;

    //OpenCV indexes (BGR)
    int RED_COLOR_INDEX = 2;
    int GREEN_COLOR_INDEX = 1;
    int BLUE_COLOR_INDEX = 0;

    int DEFAULT_THRESHOLD = 64;
    double[] RED_PIXEL = new double[]{0, 0, 255}; // In OpenCV BGR color format

}
