package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
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

        // 107957 too high
        // I can see the sample dropping into a cycle.
        // I'm now seeing matches but the weights aren't the same and I don't understand that.
        // OK, I can see a 65 period cycle forming : first iteration is 159 which matches 95.
        // So run it through to 200, and remember the weights. Then come back from the cycle.
        // 105656 for 174 will be higher than the next (wrong) answer
        // 105641 for 175 was too high
        // 105622 for 176 was not the right answer
        final long targetRange = 200;
        final long destination = 1000000000;
        final long period = 65; // eyeballed from runs
        final long leftOver = destination % period;
        final int targetIndex = (int) (200 - leftOver);
        final List<Long> targets = new ArrayList<>();
        final List<String> periods = new ArrayList<>();

        final Map<String, String> cache = new HashMap<>();
        final long start = System.currentTimeMillis() / 1000;

        periods.add(this.map);
        final boolean running = true;
        for (long spinCycle = 0; spinCycle < destination; spinCycle++) {
            if (spinCycle % 10000 == 0) {
                final long now = System.currentTimeMillis() / 1000;
                final double fraction = 1.0 * spinCycle / destination;
                System.out.println(spinCycle + " : " + cache.size() + " finish in " + (now - start) / fraction + " seconds.");
            }
            final String hash = this.getHash(this.map);
            if (cache.containsKey(hash)) {
                this.map = cache.get(hash);
            } else {
                this.tiltMap("N");
                this.tiltMap("W");
                this.tiltMap("S");
                this.tiltMap("E");
                cache.put(hash, this.map);
            }
//            for (int i = 0; i < periods.size(); i++) {
//                if (periods.get(i).equalsIgnoreCase(this.map)) {
//                    System.out.println("breaking on " + spinCycle + " which matches " + i + " : " + this.weighRocks());
//                    break;
//                }
//            }
//            periods.add(this.map);
//            targets.add(this.weighRocks());
//            if (!running) {
//                break;
//            }
        }
        if (DEBUG) {
            this.debugMap();
        }

//        for (int i = -10; i < 10; i++) {
//            System.out.println(i + " : " + targets.get(targetIndex + i));
//        }

//        return String.valueOf(targets.get(targetIndex));
        return String.valueOf(this.weighRocks());
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
