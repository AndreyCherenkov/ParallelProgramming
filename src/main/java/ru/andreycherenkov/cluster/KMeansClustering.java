package ru.andreycherenkov.cluster;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.Random;

public class KMeansClustering {
    private List<Point> dataPoints;
    private List<Cluster> clusters;
    private int numClusters;

    public KMeansClustering(List<Point> dataPoints, int numClusters) {
        this.dataPoints = dataPoints;
        this.numClusters = numClusters;
        this.clusters = new ArrayList<>();
    }

    private static List<Point> loadData(String filename) throws IOException {
        List<Point> points = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                try {
                    double x = Double.parseDouble(values[11]);
                    double y = Double.parseDouble(values[36]);
                    points.add(new Point(x, y));
                } catch (NumberFormatException ignored) {}
            }
        }
        return points;
    }

    private static List<Point> scaleData(List<Point> data) {
        double minCreatinine = Double.MAX_VALUE;
        double maxCreatinine = Double.MIN_VALUE;
        double minHCO3 = Double.MAX_VALUE;
        double maxHCO3 = Double.MIN_VALUE;

        // Находим минимальные и максимальные значения для x и y
        for (Point point : data) {
            if (point.x < minCreatinine) minCreatinine = point.x;
            if (point.x > maxCreatinine) maxCreatinine = point.x;
            if (point.y < minHCO3) minHCO3 = point.y;
            if (point.y > maxHCO3) maxHCO3 = point.y;
        }

        // Создаем массив для нормализованных точек
        List<Point> scaledData = new ArrayList<>();
        for (Point point : data) {
            double scaledX = (point.x - minCreatinine) / (maxCreatinine - minCreatinine);
            double scaledY = (point.y - minHCO3) / (maxHCO3 - minHCO3);
            scaledData.add(new Point(scaledX, scaledY));
        }

        return scaledData;
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
        initializeClusters();
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

    public void printClusters() {
        for (int i = 0; i < clusters.size(); i++) {
            System.out.println("Cluster " + (i + 1) + ":");
            System.out.println(" Centroid: (" + clusters.get(i).centroid.x + ", " + clusters.get(i).centroid.y + ")");
            System.out.println(" Points:");
            for (Point point : clusters.get(i).points) {
                System.out.println("  (" + point.x + ", " + point.y + ")");
            }
        }
    }

    public double calculateDaviesBouldinIndex() {
        int numClusters = clusters.size();
        double[][] S = new double[numClusters][numClusters];
        double[] R = new double[numClusters];

        // Вычисляем расстояния между центроидами кластеров
        for (int i = 0; i < numClusters; i++) {
            for (int j = 0; j < numClusters; j++) {
                if (i != j) {
                    S[i][j] = clusters.get(i).centroid.distance(clusters.get(j).centroid);
                }
            }
        }

        // Вычисляем внутренние расстояния для каждого кластера
        for (int i = 0; i < numClusters; i++) {
            double sumDistance = 0;
            for (Point point : clusters.get(i).points) {
                sumDistance += point.distance(clusters.get(i).centroid);
            }
            R[i] = sumDistance / clusters.get(i).points.size();
        }

        // Вычисляем индекс Дана
        double dbIndex = 0;
        for (int i = 0; i < numClusters; i++) {
            double maxRatio = 0;
            for (int j = 0; j < numClusters; j++) {
                if (i != j && S[i][j] != 0) {
                    double ratio = (R[i] + R[j]) / S[i][j];
                    maxRatio = Math.max(maxRatio, ratio);
                }
            }
            dbIndex += maxRatio;
        }

        return dbIndex / numClusters;
    }

    public static void main(String[] args) throws IOException {
        List<Point> dataPoints = loadData("D:\\JavaProjects\\ParallelProgramming\\src\\main\\resources\\BD-Patients.csv");
        int k = 5; // Количество кластеров
        KMeansClustering kMeans = new KMeansClustering(scaleData(dataPoints), k);
        kMeans.run();
        kMeans.printClusters();

        double dbIndex = kMeans.calculateDaviesBouldinIndex();
        System.out.println("Davies-Bouldin Index: " + dbIndex);
    }
}

