package ru.andreycherenkov.filesearcher;

import java.io.File;

public interface Paths {

    String RESULTS_PATH_LAB_1 = getPathForResults("lab1");
    String RESULTS_PATH_LAB_2 = getPathForResults("lab2");

    private static String getPathForResults(String labNumber) {
        String path = ". src main resources " + labNumber + " ";
        return path.replace(" ", File.separator);
    }

}
