package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.*;

public class Year2023Day11 extends AdventOfCodeChallenge {

    private List<Galaxy> galaxyList;
    private List<String> galaxyMap;
    private List<String> expandedGalaxyMap;
    private List<Integer> blankRows;
    private List<Integer> blankCols;
    private Map<String, Long> distances;

    private int width;
    private int height;
    private int expandedWidth;
    private int expandedHeight;

    @Override
    public String title() {
        return "Day 11: Cosmic Expansion";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 11);
    }

    @Override
    public String part1(final String[] input) {

        this.loadUniverse(input);
        this.findBlanks();
        this.expandUniverse();
        this.findExpandedGalaxies();
        this.getDistances();
        final long sumDistances = this.getSumDistances();

        return String.valueOf(sumDistances);
    }

    private void debugDistances() {
        for (final Map.Entry<String, Long> entry : this.distances.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }

    private long getSumDistances() {
        return this.distances.values().stream().mapToLong(Long::new).sum();
    }

    private void getDistances() {

        this.distances = new TreeMap<>();
        for (int i = 0; i < this.galaxyList.size(); i++) {
            for (int j = 0; j < this.galaxyList.size(); j++) {
                if (i == j) {
                    continue;
                }
                final String key = i < j ? i + "," + j : j + "," + i;
                if (this.distances.containsKey(key)) {
                    continue;
                }
                this.distances.put(key, this.manhattanDistance(this.galaxyList.get(i), this.galaxyList.get(j)));
            }
        }
    }

    private void getStupidDistances() {

        this.distances = new TreeMap<>();
        for (int i = 0; i < this.galaxyList.size(); i++) {
            for (int j = 0; j < this.galaxyList.size(); j++) {
                if (i == j) {
                    continue;
                }
                final String key = i < j ? i + "," + j : j + "," + i;
                if (this.distances.containsKey(key)) {
                    continue;
                }
                this.distances.put(key, this.stupidManhattanDistance(this.galaxyList.get(i), this.galaxyList.get(j)));
            }
        }
    }

    private Long manhattanDistance(final Galaxy from, final Galaxy to) {
        return (long) Math.abs(from.x - to.x) + Math.abs(from.y - to.y);
    }

    private Long stupidManhattanDistance(final Galaxy from, final Galaxy to) {

        long distance = 0;
        int x = from.getX();
        int y = from.getY();
        while (x != to.getX() || y != to.getY()) {
            int extras = 0;
            if (x < to.getX()) {
                x = x + 1;
                extras++;
            }
            if (x > to.getX()) {
                x = x - 1;
                extras++;
            }
            if (y < to.getY()) {
                y = y + 1;
                extras++;
            }
            if (y > to.getY()) {
                y = y - 1;
                extras++;
            }
            distance = distance + extras;
            if (this.blankRows.contains(y)) {
                distance = distance + 999999;
            }
            if (this.blankCols.contains(x)) {
                distance = distance + 999999;
            }
        }
        return distance;
    }

    private void findGalaxies() {

        this.galaxyList = new ArrayList<>();
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                if (this.getOriginalCoordinate(x, y).equalsIgnoreCase("#")) {
                    this.galaxyList.add(Galaxy.builder()
                            .x(x)
                            .y(y)
                            .build());
                }
            }
        }
    }

    private void findExpandedGalaxies() {

        this.galaxyList = new ArrayList<>();
        for (int x = 0; x < this.expandedWidth; x++) {
            for (int y = 0; y < this.expandedHeight; y++) {
                if (this.getExpandedCoordinate(x, y).equalsIgnoreCase("#")) {
                    this.galaxyList.add(Galaxy.builder()
                            .x(x)
                            .y(y)
                            .build());
                }
            }
        }
    }

    private void debugExpandedMap() {

        for (int row = 0; row < this.expandedHeight; row++) {
            System.out.println(this.expandedGalaxyMap.get(row));
        }
    }

    private void findBlanks() {

        this.blankRows = new ArrayList<>();
        this.blankCols = new ArrayList<>();
        for (int x = 0; x < this.width; x++) {
            boolean hasGalaxy = false;
            for (int y = 0; y < this.height; y++) {
                if (this.getOriginalCoordinate(x, y).equalsIgnoreCase("#")) {
                    hasGalaxy = true;
                    break;
                }
            }
            if (!hasGalaxy) {
                this.blankCols.add(x);
            }
        }

        for (int y = 0; y < this.height; y++) {
            boolean hasGalaxy = false;
            for (int x = 0; x < this.width; x++) {
                if (this.getOriginalCoordinate(x, y).equalsIgnoreCase("#")) {
                    hasGalaxy = true;
                    break;
                }
            }
            if (!hasGalaxy) {
                this.blankRows.add(y);
            }
        }
    }

    private String getOriginalCoordinate(final int x, final int y) {

        final String line = this.galaxyMap.get(y);
        return line.substring(x, x + 1);
    }

    private String getExpandedCoordinate(final int x, final int y) {

        final String line = this.expandedGalaxyMap.get(y);
        return line.substring(x, x + 1);
    }


    private void expandUniverse() {

        this.expandedGalaxyMap = new ArrayList<>();
        for (int y = 0; y < this.height; y++) {
            // quick eyeball, the first row of the real data isn't all blank
            String line = "";
            for (int x = 0; x < this.width; x++) {
                line = line + this.getOriginalCoordinate(x, y);
                if (this.blankCols.contains(x)) {
                    line = line + ".";
                }
            }
            this.expandedGalaxyMap.add(line);
            if (this.blankRows.contains(y + 1)) {
                this.expandedGalaxyMap.add(this.getBlankLine(line.length()));
            }
        }
        this.expandedHeight = this.expandedGalaxyMap.size();
        this.expandedWidth = this.expandedGalaxyMap.get(0).length();
    }

    private String getBlankLine(final int length) {

        return ".".repeat(length);
    }


    private void loadUniverse(final String[] input) {

        this.galaxyMap = Arrays.asList(input);
        this.width = input[0].length();
        this.height = input.length;
    }

    @Override
    public String part2(final String[] input) {

        this.loadUniverse(input);
        this.findBlanks();
        this.findGalaxies();
        this.getStupidDistances();
        final long sumDistances = this.getSumDistances();

        return String.valueOf(sumDistances);
    }

    @Data
    @Builder
    private static final class Galaxy {

        private int x;
        private int y;
    }
}
