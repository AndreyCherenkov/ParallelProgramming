package ru.andreycherenkov.cluster;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KMeansVisualization extends Application {

    private List<Point> dataPoints = new ArrayList<>();
    private List<Cluster> clusters = new ArrayList<>();
    private int numClusters = 3; // Количество кластеров
    private int canvasWidth = 800; // Ширина канваса
    private int canvasHeight = 600; // Высота канваса

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        generateRandomDataPoints(100); // Генерация случайных точек
        initializeClusters();
        run(); // Запуск алгоритма K-средних

        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawClusters(gc);

        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, canvasWidth, canvasHeight);
        primaryStage.setTitle("K-Means Clustering Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void generateRandomDataPoints(int numPoints) {
        Random random = new Random();
        for (int i = 0; i < numPoints; i++) {
            double x = random.nextDouble() * canvasWidth;
            double y = random.nextDouble() * canvasHeight;
            dataPoints.add(new Point(x, y));
        }
    }

    public void initializeClusters() {
        Random random = new Random();
        for (int i = 0; i < numClusters; i++) {
            int randomIndex = random.nextInt(dataPoints.size());
            clusters.add(new Cluster(dataPoints.get(randomIndex)));
        }
    }

    public void assignPointsToClusters() {
        for (Cluster cluster : clusters) {
            cluster.clear();
        }

        for (Point point : dataPoints) {
            Cluster closestCluster = null;
            double minDistance = Double.MAX_VALUE;

            for (Cluster cluster : clusters) {
                double distance = point.distance(cluster.centroid);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestCluster = cluster;
                }
            }

            if (closestCluster != null) {
                closestCluster.addPoint(point);
            }
        }
    }

    public void updateClusters() {
        for (Cluster cluster : clusters) {
            cluster.updateCentroid();
        }
    }

    public boolean hasConverged(List<Point> previousCentroids) {
        for (int i = 0; i < clusters.size(); i++) {
            if (clusters.get(i).centroid.distance(previousCentroids.get(i)) > 1e-6) {
                return false;
            }
        }
        return true;
    }

    public void run() {
        List<Point> previousCentroids = new ArrayList<>();

        do {
            previousCentroids.clear();
            for (Cluster cluster : clusters) {
                previousCentroids.add(cluster.centroid);
            }

            assignPointsToClusters();
            updateClusters();
        } while (!hasConverged(previousCentroids));
    }

    public void drawClusters(GraphicsContext gc) {
        // Очистка канваса
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        // Рисование точек данных
        for (Point point : dataPoints) {
            gc.fillOval(point.x - 3, point.y - 3, 6, 6); // Рисуем точки
        }

        // Рисование кластеров
        for (Cluster cluster : clusters) {

            gc.setFill(cluster.color);
            gc.fillOval(cluster.centroid.x - 5, cluster.centroid.y - 5, 10, 10); // Рисуем центроиды

            // Рисуем точки в кластере
            gc.setFill(cluster.color.deriveColor(1, 1, 1, 0.3)); // Полупрозрачный цвет для точек кластера
            for (Point point : cluster.points) {
                gc.fillOval(point.x - 3, point.y - 3, 6, 6); // Рисуем точки кластера
            }
        }
    }
}
