package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;

public class Year2022Day15 extends AdventOfCodeChallenge {

    // 4344721 is NOT right for part 1. Too low. But the sample works.
    // 4344721 is same answer for 2000001

    private List<Coord> sensors;
    private List<Coord> beacons;
    private Map<Coord, Coord> map;
    private Bounds bounds;

    @Override
    public boolean run() {
        return this.runChallenge(2022, 15);
    }

    @Override
    public String part1(final String[] input) {
        this.loadMap(input);
        return String.valueOf(this.countImpossibles(2000000));
//        return String.valueOf(this.countImpossibles(10));
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    private long countImpossibles(final int row) {
        final int[] rowData = new int[this.bounds.getWidth()];
        for (final Coord sensor : this.sensors) {
            if (sensor.getY() == row) {
                rowData[sensor.getX() - this.bounds.minX] = 1;
            }
        }
        for (final Coord sensor : this.sensors) {
            final Coord beacon = this.map.get(sensor);
            this.updateArrayWithSensorAndBeacon(sensor, beacon, rowData, row);
        }
        for (final Coord beacon : this.beacons) {
            if (beacon.getY() == row) {
                rowData[beacon.getX() - this.bounds.minX] = 0;
            }
        }
        return Arrays.stream(rowData).filter(p -> p == 1).count();
    }

    private void updateArrayWithSensorAndBeacon(final Coord sensor, final Coord beacon, final int[] rowData, final int row) {
        final int distance = sensor.manhattanDistance(beacon);
        if (Math.abs(sensor.getY() - row) > distance) {
            System.out.println("Sensor " + sensor + " has distance of " + distance + " and is too far away from " + row + " at " + Math.abs(sensor.getY() - row));
            return;
        }
        int updates = 0;
        for (int x = sensor.getX() - distance; x <= sensor.getX() + distance; x++) {
            if (x < this.bounds.minX || x > this.bounds.maxX) {
                continue;
            }
            final Coord test = new Coord(x + "," + row);
            if (sensor.manhattanDistance(test) > distance) {
                continue;
            }
            rowData[x - this.bounds.minX] = 1;
            updates++;
        }
        System.out.println("Sensor " + sensor + " has distance " + distance + " and made " + updates + " updates.");
    }

    private void loadMap(final String[] input) {
        this.beacons = new ArrayList<>();
        this.sensors = new ArrayList<>();
        this.map = new HashMap<>();
        for (final String line : input) {
            this.loadLine(line);
        }
        this.bounds = new Bounds();
        for (final Coord c : this.beacons) {
            this.updateBounds(c);
        }
        for (final Coord c : this.sensors) {
            this.updateBounds(c);
        }
    }

    private void updateBounds(final Coord coord) {
        if (this.bounds.minX == null || coord.getX() < this.bounds.minX) {
            this.bounds.minX = coord.getX();
        }
        if (this.bounds.minY == null || coord.getY() < this.bounds.minY) {
            this.bounds.minY = coord.getY();
        }
        if (this.bounds.maxX == null || coord.getX() > this.bounds.maxX) {
            this.bounds.maxX = coord.getX();
        }
        if (this.bounds.maxY == null || coord.getY() > this.bounds.maxY) {
            this.bounds.maxY = coord.getY();
        }
    }

    private void loadLine(final String line) {
        final String[] halves = line.split(":");
        final Coord sensor = this.loadSensor(halves[0]);
        final Coord beacon = this.loadBeacon(halves[1]);
        this.map.put(sensor, beacon);
    }

    private Coord loadBeacon(final String half) {
        final String[] parts = half.split("at ");
        final String address = parts[1].replace("x=", "")
                .replace("y=", "")
                .replace(" ", "");
        final Coord beacon = this.beacons.stream().filter(c -> c.toString().equalsIgnoreCase(address)).findFirst().orElse(null);
        if (beacon != null) {
            return beacon;
        }
        final Coord newBeacon = new Coord(address);
        this.beacons.add(newBeacon);
        return newBeacon;
    }

    private Coord loadSensor(final String half) {
        final String[] parts = half.split("at ");
        final String address = parts[1].replace("x=", "")
                .replace("y=", "")
                .replace(" ", "");
        final Coord coord = new Coord(address);
        this.sensors.add(coord);
        return coord;
    }

    public static final class Bounds {
        private Integer minX;
        private Integer minY;
        private Integer maxX;
        private Integer maxY;

        @Override
        public String toString() {
            return this.minX + "," + this.minY + " -> " + this.maxX + "," + this.maxY;
        }

        public int getWidth() {
            return 1 + this.maxX - this.minX;
        }

        public int getHeight() {
            return 1 + this.maxY - this.minY;
        }
    }
}
