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

        for (final AntiNode antiNode : this.antiNodes) {
            this.putChallengeMapLetter(antiNode.x, antiNode.y, "#");
        }

        this.drawChallengeMap();

        return String.valueOf(this.antiNodes.size());
    }

    private void addAntiNodesForFrequency(final String frequency) {

        System.out.println("Looking for antinodes for frequency " + frequency);
        final List<Antenna> antennaList = this.antennas.stream().filter(a -> a.frequency.equalsIgnoreCase(frequency)).collect(Collectors.toList());

        System.out.println(" I have " + antennaList.size() + " antennas to check.");
        for (final Antenna antenna : antennaList) {
            System.out.println("  " + antenna);
        }

        for (int x = 0; x < this.mapWidth; x++) {
            for (int y = 0; y < this.mapHeight; y++) {
                // is there already an antinode here ?
                if (this.getAntiNodeForXY(x, y, frequency).isPresent()) {
                    continue;
                }
                // is there going to be a node here ?
                final List<Double> distances = new ArrayList<>();
                for (final Antenna antenna : antennaList) {
                    distances.add(this.pythag(x, y, antenna.x, antenna.y));
                }

                System.out.println("testing " + this.getCoordKey(x, y) + " with " + distances);
                for (final double d1 : distances) {
                    for (final double d2 : distances) {
                        if (d1 == d2) {
                            continue;
                        }
                        if (d1 == (d2 * 2)) {
                            System.out.println("found at " + this.getCoordKey(x, y) + " using " + d1 + "," + d2);
                            final AntiNode antiNode = new AntiNode(x, y, frequency);
                            this.antiNodes.add(antiNode);
                            this.antiNodeMap.put(this.getCoordKey(x, y), antiNode);
                        }
                    }
                }
            }
        }


    }

    private Double pythag(final int x, final int y, final int x1, final int y1) {

        final double pythag = Math.sqrt(Math.pow(x - x1, 2) + Math.pow(y - y1, 2));
        return Math.round(pythag * 100.0) / 100.0;
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
