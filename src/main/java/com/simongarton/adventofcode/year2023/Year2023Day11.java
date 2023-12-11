package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Year2023Day11 extends AdventOfCodeChallenge {

    private final List<Galaxy> galaxyList = new ArrayList<>();
    private final List<String> galaxyMap = new ArrayList<>();
    private final List<String> expandedGalaxyMap = new ArrayList<>();
    private final List<Integer> blankRows = new ArrayList<>();
    private final List<Integer> blankCols = new ArrayList<>();
    private int width;
    private int height;
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
        this.debugExpandedMap();

        return String.valueOf(0);
    }

    private void debugExpandedMap() {
        for (int row = 0; row < this.expandedHeight; row++) {
            System.out.println(this.expandedGalaxyMap.get(row));
        }
    }

    private void findBlanks() {
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

    private void expandUniverse() {
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
    }

    private String getBlankLine(final int length) {
        return ".".repeat(length);
    }


    private void loadUniverse(final String[] input) {
        this.galaxyMap.addAll(Arrays.asList(input));
        this.width = input[0].length();
        this.height = input.length;
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    @Data
    @Builder
    private static final class Galaxy {

        private int x;
        private int y;
    }
}
