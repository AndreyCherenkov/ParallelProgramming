package ru.andreycherenkov.filesearcher;

import java.nio.file.Path;
import java.util.Collection;

public interface FileFinder {

    Collection<Path> findFiles();

}
