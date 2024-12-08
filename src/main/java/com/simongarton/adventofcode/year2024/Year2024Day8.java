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
        final List<Antenna> antennaList = this.antennas.stream()
                .filter(a -> a.frequency.equals(frequency)) // not case sensitive !
                .collect(Collectors.toList());

        System.out.println(" I have " + antennaList.size() + " antennas to check.");
        for (final Antenna antenna : antennaList) {
            System.out.println("  " + antenna);
        }

        for (final Antenna antenna1 : antennaList) {
            for (final Antenna antenna2 : antennaList) {
                System.out.println(" looking " + antenna1 + " with " + antenna2);
                if (antenna1.key().equalsIgnoreCase(antenna2.key())) {
                    continue;
                }
                System.out.println(" comparing " + antenna1 + " with " + antenna2);
                final int deltaX = antenna2.x - antenna1.x;
                final int deltaY = antenna2.y - antenna1.y;
                final int x = antenna2.x + deltaX;
                final int y = antenna2.y + deltaY;
                System.out.println("  looking at " + this.getCoordKey(x, y));

                if (this.getChallengeMapLetter(x, y) == null) {
                    System.out.println("  off map");
                    continue;
                }

                final String coordKey = this.getCoordKey(x, y);
                if (this.getAntiNodeForXY(x, y).isPresent()) {
                    System.out.println("  node there");
                    continue;
                }
                final AntiNode antiNode = new AntiNode(x, y, frequency);
                this.antiNodes.add(antiNode);
                this.antiNodeMap.put(coordKey, antiNode);
                System.out.println("  creating antinode " + antiNode);
            }
        }
    }

    private String getCoordKey(final int x, final int y) {
        return x + "," + y;
    }

    private void addAntiNodesForResonantFrequency(final String frequency) {

        System.out.println("Looking for antinodes for frequency " + frequency);
        final List<Antenna> antennaList = this.antennas.stream()
                .filter(a -> a.frequency.equals(frequency)) // not case sensitive !
                .collect(Collectors.toList());

        System.out.println(" I have " + antennaList.size() + " antennas to check.");
        for (final Antenna antenna : antennaList) {
            System.out.println("  " + antenna);
        }

        for (final Antenna antenna1 : antennaList) {
            for (final Antenna antenna2 : antennaList) {
                System.out.println(" looking " + antenna1 + " with " + antenna2);
                if (antenna1.key().equalsIgnoreCase(antenna2.key())) {
                    continue;
                }
                System.out.println(" comparing " + antenna1 + " with " + antenna2);
                final int deltaX = antenna2.x - antenna1.x;
                final int deltaY = antenna2.y - antenna1.y;
                int x = antenna2.x;
                int y = antenna2.y;

                while (true) {
                    System.out.println("  looking at " + this.getCoordKey(x, y));

                    if (this.getChallengeMapLetter(x, y) == null) {
                        System.out.println("  off map");
                        break;
                    }

                    final String coordKey = this.getCoordKey(x, y);
                    if (this.getAntiNodeForXY(x, y).isPresent()) {
                        System.out.println("  node there");
                        x = x + deltaX;
                        y = y + deltaY;
                        continue;
                    }
                    final AntiNode antiNode = new AntiNode(x, y, frequency);
                    this.antiNodes.add(antiNode);
                    this.antiNodeMap.put(coordKey, antiNode);
                    System.out.println("  creating antinode " + antiNode);

                    x = x + deltaX;
                    y = y + deltaY;
                }
            }
        }
    }

    private Optional<AntiNode> getAntiNodeForXY(final int x, final int y) {

        final String coordKey = this.getCoordKey(x, y);
        if (this.antiNodeMap.containsKey(coordKey)) {
            final AntiNode antiNode = this.antiNodeMap.get(coordKey);
            return Optional.of(antiNode);
        }
        return Optional.empty();
    }

    private Optional<Antenna> getAntennaForXY(final int x, final int y, final String frequency) {

        final String coordKey = this.getCoordKey(x, y);
        if (this.antennaMap.containsKey(coordKey)) {
            final Antenna antenna = this.antennaMap.get(coordKey);
            // it should never NOT match
            if (antenna.frequency.equals(frequency)) {
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
        final Optional<Antenna> optionalAntenna = this.antennas.stream().filter(a -> a.key().equals(key))
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

        this.loadChallengeMap(input);
        this.drawChallengeMap();
        this.loadAntennas();
        final List<String> frequencies = this.findFrequencies();

        this.antiNodes = new ArrayList<>();
        this.antiNodeMap = new HashMap<>();
        for (final String frequency : frequencies) {
            this.addAntiNodesForResonantFrequency(frequency);
        }

        for (final AntiNode antiNode : this.antiNodes) {
            this.putChallengeMapLetter(antiNode.x, antiNode.y, "#");
        }

        this.drawChallengeMap();

        return String.valueOf(this.antiNodes.size());
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
