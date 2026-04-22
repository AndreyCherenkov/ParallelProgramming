package ru.baza.lab4.environment;

import ru.baza.lab4.domain.MapLegend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MapLoader {

    public static CityMap loadMap(String filePath) {
        Path path = Path.of(filePath);

        try {
            var lines = Files.readAllLines(path);

            if (lines.isEmpty()) {
                throw new IllegalArgumentException("Map file is empty");
            }

            var height = lines.size();
            var width = parseLine(lines.getFirst()).length;

            var map = new Cell[height][width];

            for (var y = 0; y < height; y++) {
                var tokens = parseLine(lines.get(y));

                if (tokens.length != width) {
                    throw new IllegalArgumentException(
                            "Inconsistent row width at line " + y +
                                    ": expected " + width + ", got " + tokens.length
                    );
                }

                for (var x = 0; x < width; x++) {
                    var type = Integer.parseInt(tokens[x]);
                    var enumType = MapLegend.fromMark(type);

                    map[y][x] = new Cell(x, y, enumType);
                }
            }

            return new CityMap(map);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load map from " + filePath, e);
        }
    }

    private static String[] parseLine(String line) {
        return line.trim().split("\\s+");
    }
}
