package com.slava.localization;

import java.util.List;

public class NearestNeighbour {
    private List<Position> trainingSet;

    private List<String> stations;

    public NearestNeighbour(List<Position> trainingSet, List<String > stations) {
        this.trainingSet = trainingSet;
        this.stations = stations;
    }

    public List<Float> classify(Position point) {
        Position nearestNeighbor = null;
        double minDistance = Double.MAX_VALUE;

        for (Position example : trainingSet) {
            double distance = computeDistance(point, example);
            if (distance < minDistance) {
                minDistance = distance;
                nearestNeighbor = example;
            }
        }
        point.putX(nearestNeighbor.getX());
        point.putY(nearestNeighbor.getY());
        return nearestNeighbor.get();
    }

    private double computeDistance(Position a, Position b) {
        double distance = 0;
        for (int i = 0; i < stations.size(); i++) {
            distance += Math.pow(a.getSS(stations.get(i)) - b.getSS(stations.get(i)), 2);
        }
        return Math.sqrt(distance);
    }
}
