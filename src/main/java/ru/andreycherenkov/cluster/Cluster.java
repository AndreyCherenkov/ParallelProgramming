package ru.andreycherenkov.cluster;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
    Point centroid;
    List<Point> points;

    Cluster(Point centroid) {
        this.centroid = centroid;
        this.points = new ArrayList<>();
    }

    void clear() {
        points.clear();
    }

    void addPoint(Point point) {
        points.add(point);
    }

    void updateCentroid() {
        if (points.isEmpty()) return;

        var sumX = 0D;
        var sumY = 0D;
        for (var point : points) {
            sumX += point.x;
            sumY += point.y;
        }
        centroid = new Point(sumX / points.size(), sumY / points.size());
    }
}
