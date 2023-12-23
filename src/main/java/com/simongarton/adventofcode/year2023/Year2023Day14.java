package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


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
        this.width = input[0].length();
        this.height = input.length;
        if (DEBUG) {
            this.debugMap();
        }
        this.tiltMap("N");
        if (DEBUG) {
            this.debugMap();
        }
        final long roundRocks = this.weighRocks();
        return String.valueOf(roundRocks);
    }

    private void loadMap(final String[] input) {
        this.map = String.join("", input);
    }

    private void debugMap() {
        for (int row = 0; row < this.height; row++) {
            final String line = this.map.substring(row * this.width, (row + 1) * this.width);
            System.out.println(line);
        }
        this.blankLine();
    }

    private void tiltMap(final String dir) {
        switch (dir) {
            case "N":
                this.north();
                break;
            case "W":
                this.west();
                break;
            case "S":
                this.south();
                break;
            case "E":
                this.east();
                break;
            default:
                throw new RuntimeException(dir);
        }
//        System.out.println(" after " + dir + "\n");
//        this.debugMap();
    }

    private void north() {
        // x 0 - 9
        // y 1 to 9
        for (int col = 0; col < this.width; col++) {
            for (int row = 1; row < this.height; row++) {
                if (this.getRock(row, col).equalsIgnoreCase("O")) {
                    this.slideRockNorth(row, col);
                }
            }
        }
    }

    private void west() {
        for (int row = 0; row < this.height; row++) {
            for (int col = 1; col < this.width; col++) {
                if (this.getRock(row, col).equalsIgnoreCase("O")) {
                    this.slideRockWest(row, col);
                }
            }
        }
    }

    private void south() {
        // x 0 - 9
        // y 8 to 0
        for (int col = 0; col < this.width; col++) {
            for (int row = this.height - 2; row >= 0; row--) {
                if (this.getRock(row, col).equalsIgnoreCase("O")) {
                    this.slideRockSouth(row, col);
                }
            }
        }
    }

    private void east() {
        for (int row = 0; row < this.height; row++) {
            for (int col = this.width - 2; col >= 0; col--) {
                if (this.getRock(row, col).equalsIgnoreCase("O")) {
                    this.slideRockEast(row, col);
                }
            }
        }
    }

    private boolean slideRockNorth(final int row, final int col) {
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

    private boolean slideRockWest(final int row, final int col) {
        int destination = col - 1;
        if (!this.getRock(row, destination).equalsIgnoreCase(".")) {
            return false;
        }
        while (destination > 0 && this.getRock(row, destination - 1).equalsIgnoreCase(".")) {
            destination--;
        }
        final int from = (row * this.width) + col;
        final int to = (row * this.width) + destination;
        this.map = this.replaceCharacter(this.map, from, ".");
        this.map = this.replaceCharacter(this.map, to, "O");
        return true;
    }

    private boolean slideRockSouth(final int row, final int col) {
        int destination = row + 1;
        if (!this.getRock(destination, col).equalsIgnoreCase(".")) {
            return false;
        }
        while (destination < (this.height - 1) && this.getRock(destination + 1, col).equalsIgnoreCase(".")) {
            destination++;
        }
        final int from = (row * this.width) + col;
        final int to = (destination * this.width) + col;
        this.map = this.replaceCharacter(this.map, from, ".");
        this.map = this.replaceCharacter(this.map, to, "O");
        return true;
    }

    private boolean slideRockEast(final int row, final int col) {
        int destination = col + 1;
        if (!this.getRock(row, destination).equalsIgnoreCase(".")) {
            return false;
        }
        while (destination < (this.width - 1) && this.getRock(row, destination + 1).equalsIgnoreCase(".")) {
            destination++;
        }
        final int from = (row * this.width) + col;
        final int to = (row * this.width) + destination;
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
        if (row >= this.height) {
            System.out.println("foo");
        }
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

        this.loadMap(input);
        this.width = input[0].length();
        this.height = input.length;
        if (DEBUG) {
            this.debugMap();
        }

        final Map<String, Long> mapIndexes = new HashMap<>();
        final Map<Long, Long> mapWeights = new HashMap<>();

        long firstOccurrence = 0;
        long lastOccurrence = 0;
        long period = 0;

        final long destination = 1000000000;

        for (long spinCycle = 0; spinCycle < destination; spinCycle++) {
            mapWeights.put(spinCycle, this.weighRocks());
            this.tiltMap("N");
            this.tiltMap("W");
            this.tiltMap("S");
            this.tiltMap("E");

            final String hash = this.getHash(this.map);
            if (mapIndexes.containsKey(hash)) {
                firstOccurrence = mapIndexes.get(hash);
                lastOccurrence = spinCycle;
                period = lastOccurrence - firstOccurrence;
                break;
            } else {
                mapIndexes.put(hash, spinCycle);
            }
            if (DEBUG) {
                this.debugMap();
            }
        }

        final long remainder = destination - lastOccurrence;
        final long cycles = remainder / period;
        final long leftOver = remainder - (cycles * period);

        if (DEBUG) {
            this.debugMap();
        }

        return String.valueOf(mapWeights.get(firstOccurrence + leftOver));
    }

    private String getHash(final String line) {
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(line.getBytes());
        final byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest).toUpperCase();
    }
}
