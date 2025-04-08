package ru.andreycherenkov.filesearcher;

import java.nio.file.Path;
import java.util.Collection;

public interface FileFinder {

    String PNG_EXTENSION = ".png";
    String JPG_EXTENSION = ".jpg";

    static String extractFileExtension() {
        return ""; //todo напмисать реализацию
    }

    Collection<Path> findFiles();

}
