package com.slava.localization;

import java.util.HashMap;
import java.util.List;
public class Database {

    List<String> stations;
    List<Position> points;
    public Database() {
    }

    public void put(HashMap<String, Float> signals, float x, float y) {
        for(String key: signals.keySet()) {
            if (!stations.contains(key)) stations.add(key);
        }
        Position point = new Position(x, y, signals);
        points.add(point);
    }

    public List<Float> find(Position target)
    {
        NearestNeighbour nearestNeighbour = new NearestNeighbour(points, stations);
        return nearestNeighbour.classify(target);
    }
}
