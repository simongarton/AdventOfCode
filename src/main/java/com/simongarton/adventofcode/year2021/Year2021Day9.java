package com.simongarton.adventofcode.year2021;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Year2021Day9 extends AdventOfCodeChallenge {

    private String map;
    private int width;
    private int height;

    @Override
    public String title() {
        return "Day 9: Smoke Basin";
    }

    // not my original code ! Class missing from repo, rebuilt 2022.

    @Override
    public Outcome run() {
        return this.runChallenge(2021, 9);
    }

    @Override
    public String part1(final String[] input) {
        this.loadMap(input);
        int lowPoints = 0;
        for (int col = 0; col < this.width; col++) {
            for (int row = 0; row < this.height; row++) {
                lowPoints += this.riskLevel(col, row);
            }
        }
        return String.valueOf(lowPoints);
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    private void loadMap(final String[] input) {
        this.width = input[0].length();
        this.height = input.length;
        this.map = Arrays.asList(input).stream().collect(Collectors.joining());
    }

    private String getMap(final int col, final int row) {
        if (row < 0 || row >= this.height) {
            return "";
        }
        if (col < 0 || col >= this.width) {
            return "";
        }
        final int index = (row * this.width) + col;
        return this.map.substring(index, index + 1);
    }

    private int riskLevel(final int col, final int row) {
        final int targetLevel = Integer.parseInt(this.getMap(col, row));
        if (!this.neighbourHigher(targetLevel, col - 1, row)) {
            return 0;
        }
        if (!this.neighbourHigher(targetLevel, col + 1, row)) {
            return 0;
        }
        if (!this.neighbourHigher(targetLevel, col, row - 1)) {
            return 0;
        }
        if (!this.neighbourHigher(targetLevel, col, row + 1)) {
            return 0;
        }
        return targetLevel + 1;
    }

    private boolean neighbourHigher(final int targetLevel, final int col, final int row) {
        final String point = this.getMap(col, row);
        if (point.isEmpty()) {
            return true;
        }
        final int testLevel = Integer.parseInt(point);
        return testLevel > targetLevel;
    }
}
