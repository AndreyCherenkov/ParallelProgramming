package ru.baza.lab4.environment;

import ru.baza.lab4.domain.MapLegend;

import java.util.Arrays;
import java.util.List;

public class CityMap {

    private final Cell[][] map;
    private final List<Cell> spawnPoints;
    private final Cell deliverPoint;

    public CityMap(Cell[][] map) {
        this.map = map;

        var allCells = Arrays.stream(map)
                .flatMap(Arrays::stream)
                .toList();

        spawnPoints = allCells.stream()
                .filter(cell -> cell.getCellType().equals(MapLegend.BOT_SPAWN))
                .toList();

        deliverPoint = allCells.stream()
                .filter(cell -> cell.getCellType().equals(MapLegend.MAIN_AGENT_SPAWN))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No deliver point found"));
    }

    public Cell getCell(int x, int y) {
        if (y < 0 || y >= map.length) {
            throw new ArrayIndexOutOfBoundsException("Y out of bounds. Height: " + map.length + ", got: " + y);
        }
        if (x < 0 || x >= map[0].length) {
            throw new ArrayIndexOutOfBoundsException("X out of bounds. Width: " + map[0].length + ", got: " + x);
        }

        return map[y][x];
    }

    public List<Cell> getSpawnPoints() {
        return List.copyOf(spawnPoints);
    }

    public Cell getDeliverPoint() {
        return deliverPoint;
    }
}
