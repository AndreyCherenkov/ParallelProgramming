package ru.andreycherenkov;


import ru.andreycherenkov.filesearcher.ImageFinder;
import ru.andreycherenkov.processor.ImageProcessor;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        LabRunner labRunner = new LabRunner(new ImageFinder(), new ImageProcessor());
        Collection<Path> paths = new ImageFinder().findFiles();
        List<Integer> threadCount = List.of(1, 2, 4, 6, 8, 10, 12, 14, 16);
        labRunner.executeLab1(paths, threadCount);
    }

}
