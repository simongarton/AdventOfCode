package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.common.Coord;

import java.util.*;

public class Year2022Day15 extends AdventOfCodeChallenge {

    // 4344721 is NOT right for part 1. Too low. But the sample works.
    // 4344721 is same answer for 2000001
    // 4344722 isn't right, either.
    // 4355287 when I redid the calc isn't right, either. 4355288 is the width
    // 4344721 got another way so consistently wrong.

    private List<Coord> sensors;
    private List<Coord> beacons;
    private Map<Coord, Coord> map;
    private Bounds bounds;

    @Override
    public String title() {
        return "Day 15: Beacon Exclusion Zone";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2022, 15);
    }

    @Override
    public String part1(final String[] input) {
        this.loadMap(input);
        this.analyseRow(2000000);
        return String.valueOf(this.countImpossibles(2000000));
//        return String.valueOf(this.countImpossibles(10));
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    private void analyseRow(final int row) {
//        System.out.printf("Bounds is %s,%s to %s,%s.\n",
//                this.bounds.minX,
//                this.bounds.minY,
//                this.bounds.maxX,
//                this.bounds.maxY);
//        System.out.printf("Row is %s wide.\n", this.bounds.getWidth());
        int coordsOutOfRange = 0;
        for (int x = this.bounds.minX; x <= this.bounds.maxX; x++) {
            final Coord c = new Coord(x, row);
            int withinRange = 0;
            for (final Coord sensor : this.sensors) {
                final Coord beacon = this.map.get(sensor);
                final int beaconDistance = beacon.manhattanDistance(sensor);
                final int xDistance = c.manhattanDistance(sensor);
                if (xDistance < beaconDistance) {
                    withinRange++;
                }
            }
            if (withinRange == 0) {
                coordsOutOfRange++;
            }
        }
        // this got to 4344722 - which I have seen before. And I know there's just one beacon on this row, so -1.
//        System.out.printf("%s coords out of range, leaving %s\n", coordsOutOfRange, this.bounds.getWidth() - coordsOutOfRange);
    }

    private long countImpossibles(final int row) {
//        System.out.printf("Bounds is %s,%s to %s,%s.\n",
//                this.bounds.minX,
//                this.bounds.minY,
//                this.bounds.maxX,
//                this.bounds.maxY);
//        System.out.printf("Row is %s wide.\n", this.bounds.getWidth());
        final int[] rowData = new int[this.bounds.getWidth()];
        for (final Coord sensor : this.sensors) {
            final Coord beacon = this.map.get(sensor);
            this.updateArrayWithSensorAndBeacon(sensor, beacon, rowData, row);
        }

        // there are no sensors on this row
        for (final Coord sensor : this.sensors) {
            if (sensor.getY() == row) {
//                System.out.printf("Removing sensor at %s,%s\n", sensor.getX(), sensor.getY());
                rowData[sensor.getX() - this.bounds.minX] = 0;
            }
        }

        // looking back, surely if there is a beacon here, the missing beacon
        // can't be here ? but setting it to = 1 gives the wrong error for the
        // sample (and a wrong for part 1). There's only 1 beacon on this row : x=745731, y=2000000
        // ah no, a beacon can be closest to more than one sensor.
        for (final Coord beacon : this.beacons) {
            if (beacon.getY() == row) {
//                System.out.printf("Removing beacon at %s,%s\n", beacon.getX(), beacon.getY());
                rowData[beacon.getX() - this.bounds.minX] = 0;
            }
        }
        return Arrays.stream(rowData).filter(p -> p == 1).count();
    }

    private int updateArrayWithSensorAndBeacon(final Coord sensor, final Coord beacon, final int[] rowData, final int row) {
        final int distance = sensor.manhattanDistance(beacon);
        // this doesn't change the outcome
        final int deltay = Math.abs(sensor.getY() - row);
        if (deltay > distance) {
//            System.out.println("Sensor " + sensor +
//                    " has distance of " + distance +
//                    " and is too far away from " + row +
//                    " to it's beacon at " + beacon.getX() + "," + beacon.getY() +
//                    " at " + sensor.getY() +
//                    " leaving a vertical distance of " + deltay);
            return 0;
        }
        int updates = 0;
        final int deltax = distance - deltay;
        final int left = sensor.getX() - deltax;
        final int right = sensor.getX() + deltax;
        for (int x = left; x <= right; x++) {
            try {
                rowData[x - this.bounds.minX] = 1;
                updates++;
            } catch (final ArrayIndexOutOfBoundsException e) {

            }
        }
//        System.out.println("Sensor " + sensor + " has distance " + distance + " and made " + updates + " updates.");
        return updates;
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
