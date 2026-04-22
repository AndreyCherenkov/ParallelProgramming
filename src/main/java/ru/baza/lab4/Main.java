package ru.baza.lab4;

import ru.baza.lab4.environment.MapLoader;


public class Main {
    public static void main(String[] args) {
        var map = MapLoader.loadMap("./src/main/java/ru/baza/lab4/map.txt");
    }
}
