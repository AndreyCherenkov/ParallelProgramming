package ru.andreycherenkov;


import ru.andreycherenkov.filesearcher.ImageFinder;
import ru.andreycherenkov.processor.ImageProcessor;

//todo ПОМЕНЯЙ MAIN КЛАСС В JAVA FX ЗАФИСИМОСТЯХ
public class Main {

    public static void main(String[] args) {
        LabRunner labRunner = new LabRunner(new ImageFinder(), new ImageProcessor());
//        labRunner.execute();
    }

}
