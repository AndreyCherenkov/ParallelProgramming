package ru.baza.lab4.domain;

public enum MapLegend {
    ROAD(0),
    OBSTACLE(1),
    MAIN_AGENT_SPAWN(2),
    BOT_SPAWN(3),
    FINISH(4);

    private int mark;

    MapLegend(int mark) {
        this.mark = mark;
    }

    public int getMark() {
        return mark;
    }

    public static MapLegend fromMark(int mark) {
        switch (mark) {
            case 0 -> {
                return ROAD;
            }
            case 1 -> {
                return OBSTACLE;
            }
            case 2 -> {
                return MAIN_AGENT_SPAWN;
            }
            case 3 -> {
                return BOT_SPAWN;
            }
            case 4 -> {
                return FINISH;
            }
            default -> throw new IllegalStateException();
        }
    }
}
