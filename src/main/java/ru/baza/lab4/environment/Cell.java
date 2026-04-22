package ru.baza.lab4.environment;

import ru.baza.lab4.domain.MapLegend;
import ru.baza.lab4.domain.Position;

public final class Cell {

    private final Position position;
    private final MapLegend cellType;

    public Cell(Position position, MapLegend cellType) {
        this.position = position;
        this.cellType = cellType;
    }

    public Cell(int x, int y, MapLegend cellType) {
        this.position = new Position(x, y);
        this.cellType = cellType;
    }

    public Position getPosition() {
        return position;
    }

    public MapLegend getCellType() {
        return cellType;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "position=" + position +
                ", cellType=" + cellType +
                '}';
    }
}
