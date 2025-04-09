package ru.andreycherenkov.filesearcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static ru.andreycherenkov.filesearcher.ProjectPaths.START_PATH;

public class ImageFinder implements FileFinder {

    private static final List<String> IMAGE_EXTENSIONS = List.of(
            PNG_EXTENSION,
            JPG_EXTENSION
    );

    @Override
    public Collection<Path> findFiles() {
        var foundFiles = new ArrayList<Path>();
        var startPath = Paths.get(START_PATH);
        try (Stream<Path> pathStream = Files.walk(startPath)) {
            pathStream
                    .filter(Files::isRegularFile)
                    .filter(path -> !path.toString().contains(startPath + File.separator + "target"))
                    .filter(path -> !path.toString().contains("resources" + File.separator + "lab"))
                    .filter(path -> path.toString().contains("resources"))
                    .filter(path -> IMAGE_EXTENSIONS.stream().anyMatch(ext -> path.toString().endsWith(ext)))
                    .forEach(foundFiles::add);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return foundFiles;
    }
}
