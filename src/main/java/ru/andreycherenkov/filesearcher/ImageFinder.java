package ru.andreycherenkov.filesearcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class ImageFinder implements FileFinder {

    private static final String PNG_EXTENSION = ".png";
    private static final String JPG_EXTENSION = ".jpg";
    public static final List<String> IMAGE_EXTENSIONS = List.of(
            PNG_EXTENSION,
            JPG_EXTENSION
    );

    @Override
    public Collection<Path> findFiles() {
        List<Path> foundFiles = new ArrayList<>();
        Path startPath = Paths.get("." + File.separator);
        try (Stream<Path> pathStream = Files.walk(startPath)) {
            pathStream
                    .filter(Files::isRegularFile)
                    .filter(path -> !path.toString().contains(startPath + File.separator + "target"))
                    .filter(path -> !path.toString().contains("resources" + File.separator + "results"))
                    .filter(path -> IMAGE_EXTENSIONS.stream().anyMatch(ext -> path.toString().endsWith(ext)))
                    .forEach(foundFiles::add);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return foundFiles;
    }
}
