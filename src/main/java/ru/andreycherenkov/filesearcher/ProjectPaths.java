package ru.andreycherenkov.filesearcher;

import java.io.File;

public interface ProjectPaths {

    String START_PATH = "." + File.separator;
    String RESULTS_PATH_LAB_1 = getPathForResults("lab1");
    String RESULTS_PATH_LAB_2 = getPathForResults("lab2");

    private static String getPathForResults(String labNumber) {
        var path = ". src main resources " + labNumber + " ";
        return path.replace(" ", File.separator);
    }

}
