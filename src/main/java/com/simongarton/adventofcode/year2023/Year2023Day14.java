package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Year2023Day14 extends AdventOfCodeChallenge {

    private String map;
    private int width;
    private int height;

    private static final boolean DEBUG = false;

    @Override
    public String title() {
        return "Day 14: Parabolic Reflector Dish";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 14);
    }

    @Override
    public String part1(final String[] input) {

        this.loadMap(input);
        if (DEBUG) {
            this.debugMap();
            this.blankLine();
        }
        this.width = input[0].length();
        this.height = input.length;
        this.tiltMap("N");
        if (DEBUG) {
            this.debugMap();
        }
        final long roundRocks = this.weighRocks();
        return String.valueOf(roundRocks);
    }

    private void loadMap(final String[] input) {
        this.map = Arrays.stream(input).collect(Collectors.joining());
    }

    private void blankLine() {
        System.out.println();
    }

    private void debugMap() {
        for (int row = 0; row < this.height; row++) {
            final String line = this.map.substring(row * this.width, (row + 1) * this.width);
            System.out.println(line);
        }
    }

    private void tiltMap(final String n) {
        for (int col = 0; col < this.width; col++) {
            for (int row = 1; row < this.height; row++) {
                boolean moved = false;
                if (this.getRock(row, col).equalsIgnoreCase("O")) {
                    moved = this.slideRock(row, col);
                }
            }
        }
    }

    private boolean slideRock(final int row, final int col) {
        int destination = row - 1;
        if (!this.getRock(destination, col).equalsIgnoreCase(".")) {
            return false;
        }
        while (destination > 0 && this.getRock(destination - 1, col).equalsIgnoreCase(".")) {
            destination--;
        }
        final int from = (row * this.width) + col;
        final int to = (destination * this.width) + col;
        this.map = this.replaceCharacter(this.map, from, ".");
        this.map = this.replaceCharacter(this.map, to, "O");
        return true;
    }

    private String replaceCharacter(final String original, final Integer index, final String replacement) {
        return original.substring(0, index) + replacement + original.substring(index + 1, original.length());
    }

    private String getRock(final int row, final int col) {
        if (row < 0) {
            throw new RuntimeException("got row " + row);
        }
        final int index = (row * this.width) + col;
        return this.map.substring(index, index + 1);
    }

    private long weighRocks() {
        long weight = 0;
        for (int col = 0; col < this.width; col++) {
            for (int row = 0; row < this.height; row++) {
                if (this.getRock(row, col).equalsIgnoreCase("O")) {
                    weight += this.height - row;
                }
            }
        }
        return weight;
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }
}
