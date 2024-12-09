package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;
import java.util.stream.Collectors;

public class Year2024Day8 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;

    private Map<String, Antenna> antennaMap;
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

        final List<String> frequencies = this.setup(input);

        for (final String frequency : frequencies) {
            this.addAntiNodesForFrequency(frequency);
        }

        for (final AntiNode antiNode : this.getAntiNodes()) {
            this.putChallengeMapLetter(antiNode.x, antiNode.y, "#");
        }

        if (DEBUG) {
            this.drawChallengeMap();
        }

        return String.valueOf(this.getAntiNodes().size());
    }

    private List<Antenna> getAntennas() {

        return List.copyOf(this.antennaMap.values());
    }

    private List<AntiNode> getAntiNodes() {

        return List.copyOf(this.antiNodeMap.values());
    }

    private List<String> setup(final String[] input) {
        this.loadChallengeMap(input);
        if (DEBUG) {
            this.drawChallengeMap();
        }
        this.loadAntennas();
        final List<String> frequencies = this.findFrequencies();

        this.antiNodeMap = new HashMap<>();

        return frequencies;
    }

    private void addAntiNodesForFrequency(final String frequency) {

        final List<Antenna> antennaList = this.getAntennas().stream()
                .filter(a -> a.frequency.equals(frequency)) // not case sensitive !
                .collect(Collectors.toList());

        for (final Antenna antenna1 : antennaList) {
            for (final Antenna antenna2 : antennaList) {
                if (antenna1.key().equalsIgnoreCase(antenna2.key())) {
                    continue;
                }
                final int deltaX = antenna2.x - antenna1.x;
                final int deltaY = antenna2.y - antenna1.y;
                final int x = antenna2.x + deltaX;
                final int y = antenna2.y + deltaY;

                if (this.getChallengeMapLetter(x, y) == null) {
                    continue;
                }

                this.createAntiNodeIfNeeded(x, y, frequency);
            }
        }
    }

    private void createAntiNodeIfNeeded(final int x, final int y, final String frequency) {

        final String coordKey = this.getCoordKey(x, y);
        if (this.getAntiNodeForXY(x, y).isEmpty()) {
            final AntiNode antiNode = new AntiNode(x, y, frequency);
            this.antiNodeMap.put(coordKey, antiNode);
        }
    }

    private String getCoordKey(final int x, final int y) {

        return x + "," + y;
    }

    private void addAntiNodesForResonantFrequency(final String frequency) {

        final List<Antenna> antennaList = this.getAntennas().stream()
                .filter(a -> a.frequency.equals(frequency)) // not case sensitive !
                .collect(Collectors.toList());

        for (final Antenna antenna1 : antennaList) {
            for (final Antenna antenna2 : antennaList) {
                if (antenna1.key().equalsIgnoreCase(antenna2.key())) {
                    continue;
                }
                final int deltaX = antenna2.x - antenna1.x;
                final int deltaY = antenna2.y - antenna1.y;
                int x = antenna2.x;
                int y = antenna2.y;

                while (true) {
                    // off map
                    if (this.getChallengeMapLetter(x, y) == null) {
                        break;
                    }

                    this.createAntiNodeIfNeeded(x, y, frequency);

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

    private List<String> findFrequencies() {

        return this.getAntennas().stream()
                .map(a -> a.frequency)
                .distinct()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    private void loadAntennas() {

        this.antennaMap = new HashMap<>();

        for (int y = 0; y < this.mapHeight; y++) {
            for (int x = 0; x < this.mapWidth; x++) {
                final String frequency = this.getChallengeMapLetter(x, y);
                if (frequency.equalsIgnoreCase(".")) {
                    continue;
                }
                this.getOrCreateAntenna(frequency, x, y);
            }
        }
    }

    private Antenna getOrCreateAntenna(final String frequency, final int x, final int y) {

        final String key = this.getCoordKey(x, y) + ":" + frequency;
        final Optional<Antenna> optionalAntenna = this.getAntennas().stream().filter(a -> a.key().equals(key))
                .findFirst();
        if (optionalAntenna.isPresent()) {
            return optionalAntenna.get();
        }

        final Antenna antenna = new Antenna(x, y, frequency);
        final String coordKey = this.getCoordKey(x, y);
        this.antennaMap.put(coordKey, antenna);
        return antenna;
    }

    @Override
    public String part2(final String[] input) {

        final List<String> frequencies = this.setup(input);

        for (final String frequency : frequencies) {
            this.addAntiNodesForResonantFrequency(frequency);
        }

        for (final AntiNode antiNode : this.getAntiNodes()) {
            this.putChallengeMapLetter(antiNode.x, antiNode.y, "#");
        }

        if (DEBUG) {
            this.drawChallengeMap();
        }

        return String.valueOf(this.getAntiNodes().size());
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
