package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;
import java.util.stream.Collectors;

public class Year2024Day8 extends AdventOfCodeChallenge {

    private ArrayList<Antenna> antennas;
    private Map<String, Antenna> antennaMap;
    private ArrayList<AntiNode> antiNodes;
    private Map<String, AntiNode> antiNodeMap;

    @Override
    public String title() {
        return "Day 8: Resonant Collinearity";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 8);
    }

    @Override
    public String part1(final String[] input) {

        this.loadChallengeMap(input);
        this.drawChallengeMap();
        this.loadAntennas();
        final List<String> frequencies = this.findFrequencies();

        this.antiNodes = new ArrayList<>();
        this.antiNodeMap = new HashMap<>();
        for (final String frequency : frequencies) {
            this.addAntiNodesForFrequency(frequency);
        }

        return String.valueOf(this.antiNodes.size());
    }

    private void addAntiNodesForFrequency(final String frequency) {

        System.out.println("Looking for antinodes for frequency " + frequency);
        final List<Antenna> antennaList = this.antennas.stream().filter(a -> a.frequency.equalsIgnoreCase(frequency)).collect(Collectors.toList());

        System.out.println(" I have " + antennaList.size() + " antennas to check.");
        for (final Antenna antenna : antennaList) {
            System.out.println("  " + antenna);
        }

        for (final Antenna antenna : antennaList) {
            // is this just diagonals ?
            this.addAntiNodesForAntenna(antenna, -1, -1);
            this.addAntiNodesForAntenna(antenna, -1, 1);
            this.addAntiNodesForAntenna(antenna, 1, 1);
            this.addAntiNodesForAntenna(antenna, 1, -1);
            // I don't think so
            this.addAntiNodesForAntenna(antenna, -1, 0);
            this.addAntiNodesForAntenna(antenna, 1, 0);
            this.addAntiNodesForAntenna(antenna, 0, 1);
            this.addAntiNodesForAntenna(antenna, 0, -1);
        }
    }

    private void addAntiNodesForAntenna(final Antenna antenna, final int deltaX, final int deltaY) {

        // now I'm starting from an Antenna which is in the middle of the map, so I need to test both directions

        final List<String> coordsToCheck = new ArrayList<>();

        final List<Antenna> antennasOnBearing = new ArrayList<>();
        antennasOnBearing.add(antenna);
        System.out.println("  starting with " + antenna +
                " at " + this.getCoordKey(antenna.x, antenna.y) +
                " going " + this.getCoordKey(deltaX, deltaY)
        );

        int testX = antenna.x;
        int testY = antenna.y;
        coordsToCheck.add(this.getCoordKey(testX, testY));
        // first loop
        while (true) {
            testX = testX + deltaX;
            testY = testY + deltaY;
            if (this.getChallengeMapLetter(testX, testY) == null) {
//                System.out.println("   fell off map at " + this.getCoordKey(testX, testY));
                break; // off map;
            }
            coordsToCheck.add(this.getCoordKey(testX, testY));
            final Optional<Antenna> optionalAntenna = this.getAntennaForXY(testX, testY);
            if (optionalAntenna.isEmpty()) {
                continue;
            }
            final Antenna antennaOther = optionalAntenna.get();
            System.out.println("   found " + antennaOther + " at " + this.getCoordKey(testX, testY));
            if (antennaOther.frequency.equalsIgnoreCase(antenna.frequency)) {
                antennasOnBearing.add(antennaOther);
            }
        }
        testX = antenna.x;
        testY = antenna.y;
        // second loop
        while (true) {
            testX = testX - deltaX;
            testY = testY - deltaY;
            if (this.getChallengeMapLetter(testX, testY) == null) {
//                System.out.println("   fell off map at " + this.getCoordKey(testX, testY));
                break; // off map;
            }
            coordsToCheck.add(this.getCoordKey(testX, testY));
            final Optional<Antenna> optionalAntenna = this.getAntennaForXY(testX, testY);
            if (optionalAntenna.isEmpty()) {
                continue;
            }
            final Antenna antennaOther = optionalAntenna.get();
            System.out.println("   found " + antennaOther + " at " + this.getCoordKey(testX, testY));
            if (antennaOther.frequency.equalsIgnoreCase(antenna.frequency)) {
                antennasOnBearing.add(antennaOther);
            }
        }

        // finally : I have a list of coords to check and a list of antennas on this bearing
        if (antennasOnBearing.size() == 1) {
            System.out.println("  only 1 antenna on bearing.");
            return;
        }

        System.out.println("  checking coords " + coordsToCheck);

        for (final String coordToCheck : coordsToCheck) {
            final String[] parts = coordToCheck.split(",");
            final int x = Integer.parseInt(parts[0]);
            final int y = Integer.parseInt(parts[1]);
            final List<Integer> distances = new ArrayList<>();
            for (final Antenna antennaToCheck : antennasOnBearing) {
                final int distance = this.integerDistance(x, y, antennaToCheck);
                if (distance > 0) {
                    distances.add(distance);
                }
            }

            System.out.println("Checking coord " + coordToCheck + " with " + distances);
            for (final int firstDistance : distances) {
                for (final int secondDistance : distances) {
                    if (firstDistance == (secondDistance * 2)) {
                        final Optional<AntiNode> optionalAntiNode = this.getAntiNodeForXY(x, y, antenna.frequency);
                        if (optionalAntiNode.isPresent()) {
                            // I've already done this one;
                            continue;
                        }
                        final AntiNode antiNode = new AntiNode(x, y, antenna.frequency);
                        this.antiNodes.add(antiNode);
                        final String coordKey = this.getCoordKey(x, y);
                        this.antiNodeMap.put(coordKey, antiNode);
                        System.out.println("Adding antinode at " + coordKey + " for " + antenna.frequency);
                    }
                }
            }
        }
    }

    private String getCoordKey(final int x, final int y) {
        return x + "," + y;
    }

    private Optional<AntiNode> getAntiNodeForXY(final int x, final int y, final String frequency) {

        final String coordKey = this.getCoordKey(x, y);
        if (this.antiNodeMap.containsKey(coordKey)) {
            final AntiNode antiNode = this.antiNodeMap.get(coordKey);
            // it should never NOT match
            if (antiNode.frequency.equalsIgnoreCase(frequency)) {
                return Optional.of(antiNode);
            }
        }
        return Optional.empty();
    }

    private Optional<Antenna> getAntennaForXY(final int x, final int y, final String frequency) {

        final String coordKey = this.getCoordKey(x, y);
        if (this.antennaMap.containsKey(coordKey)) {
            final Antenna antenna = this.antennaMap.get(coordKey);
            // it should never NOT match
            if (antenna.frequency.equalsIgnoreCase(frequency)) {
                return Optional.of(antenna);
            }
        }
        return Optional.empty();
    }

    private Optional<Antenna> getAntennaForXY(final int x, final int y) {

        final String coordKey = this.getCoordKey(x, y);
        if (this.antennaMap.containsKey(coordKey)) {
            return Optional.of(this.antennaMap.get(coordKey));
        }
        return Optional.empty();
    }

    private List<String> findFrequencies() {

        final Set<String> frequencies = this.antennas.stream().map(a -> a.frequency).collect(Collectors.toSet());
        final List<String> frequencyList = new ArrayList<>(frequencies);
        frequencyList.sort(Comparator.naturalOrder());
        return frequencyList;
    }

    private void loadAntennas() {

        this.antennas = new ArrayList<>();
        this.antennaMap = new HashMap<>();

        for (int y = 0; y < this.mapHeight; y++) {
            for (int x = 0; x < this.mapWidth; x++) {
                final String frequency = this.getChallengeMapLetter(x, y);
                if (frequency.equalsIgnoreCase(".")) {
                    continue;
                }
                final Antenna antenna = this.getOrCreateAntenna(frequency, x, y);
            }
        }

        System.out.printf("Found %s antennas.%n", this.antennas.size());
    }

    private Antenna getOrCreateAntenna(final String frequency, final int x, final int y) {

        final String key = this.getCoordKey(x, y) + ":" + frequency;
        final Optional<Antenna> optionalAntenna = this.antennas.stream().filter(a -> a.key().equalsIgnoreCase(key))
                .findFirst();
        if (optionalAntenna.isPresent()) {
            return optionalAntenna.get();
        }

        final Antenna antenna = new Antenna(x, y, frequency);
        this.antennas.add(antenna);
        final String coordKey = this.getCoordKey(x, y);
        this.antennaMap.put(coordKey, antenna);
        return antenna;

    }

    // it's not manhattan distances;

    private int integerDistance(final int x, final int y, final Antenna antenna) {

        int distance = 0;
        int startX = x;
        int startY = y;
        while (true) {
            boolean changed = false;
            if (startX > antenna.x) {
                startX--;
                changed = true;
            }
            if (startX < antenna.x) {
                startX++;
                changed = true;
            }
            if (startY > antenna.y) {
                startY--;
                changed = true;
            }
            if (startY < antenna.y) {
                startY++;
                changed = true;
            }
            if (!changed) {
                return distance;
            }
            distance++;
        }
    }

    private int manhattanDistance(final Antenna antenna1, final Antenna antenna2) {

        return Math.abs(antenna2.x - antenna1.x) + Math.abs(antenna2.y - antenna1.y);
    }

    private int manhattanDistance(final int x, final int y, final Antenna antenna) {

        return Math.abs(x - antenna.x) + Math.abs(y - antenna.y);
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    static class Antenna {

        private final int x;
        private final int y;
        private final String frequency;

        public Antenna(final int x, final int y, final String frequency) {
            this.x = x;
            this.y = y;
            this.frequency = frequency;
        }

        public String key() {
            return this.x + "," + this.y + ":" + this.frequency;
        }

        @Override
        public String toString() {
            return this.key();
        }
    }

    static class AntiNode {

        private final int x;
        private final int y;
        private final String frequency;

        public AntiNode(final int x, final int y, final String frequency) {
            this.x = x;
            this.y = y;
            this.frequency = frequency;
        }

        public String key() {
            return this.x + "," + this.y + ":" + this.frequency;
        }

        @Override
        public String toString() {
            return this.key();
        }
    }

}
