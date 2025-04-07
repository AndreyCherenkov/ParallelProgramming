package ru.andreycherenkov.filesearcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class TableFinder implements FileFinder {

    private static final String CSV_EXTENSION = ".csv";
    public static final List<String> IMAGE_EXTENSIONS = List.of(
            CSV_EXTENSION
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
