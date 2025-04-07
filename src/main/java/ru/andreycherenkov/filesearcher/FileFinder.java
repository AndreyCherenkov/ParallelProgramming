package ru.andreycherenkov.filesearcher;

import java.nio.file.Path;
import java.util.Collection;

public interface FileFinder {

    static String extractFileExtension() {
        return ""; //todo напмисать реализацию
    }

    Collection<Path> findFiles();

}
