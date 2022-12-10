package com.simongarton.adventofcode.year2019;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;

public class Year2019Day3 extends AdventOfCodeChallenge {

    int x;
    int y;
    List<String> wire1;
    List<String> wire2;
    List<Coord> path1 = new ArrayList<>();
    List<Coord> path2 = new ArrayList<>();

    @Override
    public boolean run() {
        return this.runChallenge(2019, 3);
    }

    @Override
    public String part1(final String[] input) {
        this.loadWires(input);
        this.mapWires();
        return String.valueOf(this.findIntersections());
    }

    private void loadWires(final String[] input) {
        final List<String> wires = Arrays.asList(input);
        this.wire1 = Arrays.asList(wires.get(0).split(","));
        this.wire2 = Arrays.asList(wires.get(1).split(","));
    }

    @Override
    public String part2(final String[] input) {
        this.loadWires(input);
        this.mapWiresWithDistance();
        return String.valueOf(this.findIntersectionsWithDistance());
    }

    private int findIntersectionsWithDistance() {
        final Map<String, Coord> points1 = new HashMap<>();
        final Map<String, Coord> points2 = new HashMap<>();
        for (final Coord c : this.path1) {
            if (!points1.containsKey(c.asCoord())) {
                points1.put(c.asCoord(), c);
            }
        }
        for (final Coord c : this.path2) {
            if (!points2.containsKey(c.asCoord())) {
                points2.put(c.asCoord(), c);
            }
        }
        final Coord origin = new Coord(0, 0);
        int best = 0;
        Coord bestCoord = origin;
        for (final Map.Entry<String, Coord> entry : points1.entrySet()) {
            if (!points2.containsKey(entry.getKey())) {
                continue;
            }
            final Coord point1 = points1.get(entry.getKey());
            final Coord point2 = points2.get(entry.getKey());
            final int distance = point1.distance + point2.distance;
            if (distance > 0) {
                if ((best == 0) || (distance < best)) {
                    best = distance;
                    bestCoord = point1;
                }
            }
        }
        return best;
    }

    private int findIntersections() {
        final Map<String, Coord> points1 = new HashMap<>();
        final Map<String, Coord> points2 = new HashMap<>();
        this.path1.forEach(c -> points1.put(c.asCoord(), c));
        this.path2.forEach(c -> points2.put(c.asCoord(), c));
        final Coord origin = new Coord(0, 0);
        int best = 0;
        Coord bestCoord = origin;
        for (final Map.Entry<String, Coord> entry : points1.entrySet()) {
            if (!points2.containsKey(entry.getKey())) {
                continue;
            }
            final Coord point = points1.get(entry.getKey());
            final int distance = this.manhattanDistance(point, origin);
            if (distance > 0) {
                if ((best == 0) || (distance < best)) {
                    best = distance;
                    bestCoord = point;
                }
            }
        }
        return best;
    }

    private int manhattanDistance(final Coord point, final Coord origin) {
        return Math.abs(point.x - origin.x) + Math.abs(point.y - origin.y);
    }

    private void mapWires() {
        this.mapWire(this.wire1, this.path1);
        this.mapWire(this.wire2, this.path2);
    }

    private void mapWiresWithDistance() {
        this.mapWireWithDistance(this.wire1, this.path1);
        this.mapWireWithDistance(this.wire2, this.path2);
    }

    private void mapWireWithDistance(final List<String> wire, final List<Coord> path) {
        this.x = 0;
        this.y = 0;
        path.clear();
        int travelledDistance = 0;
        path.add(new Coord(this.x, this.y));
        for (final String v : wire) {
            travelledDistance = this.mapTurnWithDistance(v, path, travelledDistance);
        }
    }

    private void mapWire(final List<String> wire, final List<Coord> path) {
        this.x = 0;
        this.y = 0;
        path.clear();
        path.add(new Coord(this.x, this.y));
        wire.forEach(v -> this.mapTurn(v, path));
    }

    private int mapTurnWithDistance(final String turn, final List<Coord> path, int travelledDistance) {
        final String cardinal = turn.substring(0, 1);
        final int distance = Integer.parseInt(turn.substring(1));
        switch (cardinal) {
            case "U":
                for (int i = 0; i < distance; i++) {
                    this.y = this.y + 1;
                    travelledDistance = travelledDistance + 1;
                    path.add(new Coord(this.x, this.y, travelledDistance));
                }
                break;
            case "D":
                for (int i = 0; i < distance; i++) {
                    this.y = this.y - 1;
                    travelledDistance = travelledDistance + 1;
                    path.add(new Coord(this.x, this.y, travelledDistance));
                }
                break;
            case "R":
                for (int i = 0; i < distance; i++) {
                    this.x = this.x + 1;
                    travelledDistance = travelledDistance + 1;
                    path.add(new Coord(this.x, this.y, travelledDistance));
                }
                break;
            case "L":
                for (int i = 0; i < distance; i++) {
                    this.x = this.x - 1;
                    travelledDistance = travelledDistance + 1;
                    path.add(new Coord(this.x, this.y, travelledDistance));
                }
                break;
            default:
                throw new RuntimeException("Bad direction " + cardinal);
        }
        return travelledDistance;
    }

    private void mapTurn(final String turn, final List<Coord> path) {
        final String cardinal = turn.substring(0, 1);
        final int distance = Integer.parseInt(turn.substring(1));
        switch (cardinal) {
            case "U":
                for (int i = 0; i < distance; i++) {
                    this.y = this.y + 1;
                    path.add(new Coord(this.x, this.y));
                }
                break;
            case "D":
                for (int i = 0; i < distance; i++) {
                    this.y = this.y - 1;
                    path.add(new Coord(this.x, this.y));
                }
                break;
            case "R":
                for (int i = 0; i < distance; i++) {
                    this.x = this.x + 1;
                    path.add(new Coord(this.x, this.y));
                }
                break;
            case "L":
                for (int i = 0; i < distance; i++) {
                    this.x = this.x - 1;
                    path.add(new Coord(this.x, this.y));
                }
                break;
            default:
                throw new RuntimeException("Bad direction " + cardinal);
        }
        path.add(new Coord(this.x, this.y));
    }

    private static class Coord {
        public int x;
        public int y;
        public int distance = 0;

        public Coord(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

        public Coord(final int x, final int y, final int distance) {
            this.x = x;
            this.y = y;
            this.distance = distance;
        }

        public String asCoord() {
            return this.x + "," + this.y;
        }
    }
}
