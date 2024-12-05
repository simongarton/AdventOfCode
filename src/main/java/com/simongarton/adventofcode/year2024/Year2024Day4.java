package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;

public class Year2024Day4 extends AdventOfCodeChallenge {

    private String[] input;
    private int rows;
    private int cols;

    @Override
    public String title() {
        return "Day 4: Ceres Search";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 4);
    }

    @Override
    public String part1(final String[] input) {

        this.input = input;
        this.rows = input.length;
        this.cols = input[0].length();

        return String.valueOf(this.countXmasPatternsBruteForce());

        // return String.valueOf(this.countXmasPatterns());
    }

    private int countXmasPatternsBruteForce() {

        int total = 0;
        final List<int[]> directions = List.of(
                new int[]{-1, -1},
                new int[]{0, -1},
                new int[]{1, -1},
                new int[]{-1, 0},
                new int[]{1, 0},
                new int[]{-1, 1},
                new int[]{0, 1},
                new int[]{1, 1}
        );

        for (int y = 0; y < this.rows; y++) {
            for (int x = 0; x < this.cols; x++) {
                for (final int[] delta : directions) {
                    final String word = this.buildWord(x, y, delta);
                    if (word.equalsIgnoreCase("XMAS")) {
                        total++;
                    }
                }
            }
        }
        return total;
    }

    private String buildWord(final int x, final int y, final int[] delta) {
        final StringBuilder word = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            word.append(this.safeGetChar(x + (i * delta[0]), y + (i * delta[1])));
        }
        return word.toString();
    }

    private String safeGetChar(final int x, final int y) {

        if (x < 0 || x >= this.cols) {
            return "-";
        }
        if (y < 0 || y >= this.rows) {
            return "-";
        }
        return this.input[y].charAt(x) + "";
    }

    private int countXmasPatterns() {

        final List<String> startCoords = new ArrayList<>();

        for (int y = 0; y < this.rows; y++) {
            for (int x = 0; x < this.cols; x++) {
                if (this.getChar(x, y).equalsIgnoreCase("X")) {
                    final String coordDetails = this.buildCoordDetails(x, y, "X", "");
                    startCoords.add(coordDetails);
                }
            }
        }
        final List<String> mCoords = this.checkCoordsForLetter("M", startCoords);
        final List<String> aCoords = this.checkCoordsForLetter("A", mCoords);
        final List<String> sCoords = this.checkCoordsForLetter("S", aCoords);

        return sCoords.size();
    }

    private String buildCoordDetails(final int x, final int y, final String letter, final String direction) {

        return String.format("(%s,%s=%s[%s]) ", x, y, letter, direction);
    }

    private List<String> checkCoordsForLetter(final String letter, final List<String> testCoords) {

        final List<String> nextCoords = new ArrayList<>();
        for (final String coord : testCoords) {
            final List<String> neighbourCoords = this.coordHasNeighbours(coord, letter);
            for (final String neighbourCoord : neighbourCoords) {
                nextCoords.add(coord + neighbourCoord);
            }
        }
        return nextCoords;
    }

    private String getLastCoord(final String coordList) {

        final String[] parts = coordList.split(" ");
        return parts[parts.length - 1];
    }

    private List<String> coordHasNeighbours(final String coord, final String letter) {

        final List<String> neighbourCoords = new ArrayList<>();

        final String actualCoord = this.getLastCoord(coord);
        final String currentDirection = this.getDirection(actualCoord);

        for (int deltaX = -1; deltaX < 2; deltaX++) {
            for (int deltaY = -1; deltaY < 2; deltaY++) {
                final String thisDirection = String.format("%s,%s", deltaX, deltaY);
                if ((deltaX == deltaY) && (deltaY == 0)) {
                    continue;
                }

                final String newCoord = this.newCoordWithDelta(actualCoord, deltaX, deltaY, thisDirection);
                if (this.coordHasValue(newCoord, letter)) {
                    if (!currentDirection.isEmpty() && !currentDirection.equalsIgnoreCase(thisDirection)) {
                        continue;
                    }
                    neighbourCoords.add(newCoord);
                }
            }
        }
        return neighbourCoords;
    }

    private int getX(final String coord) {

        final String cleanCoord = coord.trim().replace("(", "").replace(")", "");
        final String[] parts = cleanCoord.split("=");
        final String[] coords = parts[0].split(",");
        return Integer.parseInt(coords[0]);
    }

    private int getY(final String coord) {

        final String cleanCoord = coord.trim().replace("(", "").replace(")", "");
        final String[] parts = cleanCoord.split("=");
        final String[] coords = parts[0].split(",");
        return Integer.parseInt(coords[1]);
    }

    private String getLetter(final String coord) {

        final String cleanCoord = coord.trim().replace("(", "").replace(")", "");
        final String[] parts = cleanCoord.split("=");
        return parts[1].split("\\[")[0];
    }

    private String getDirection(final String coord) {

        final String cleanCoord = coord.trim().replace("(", "").replace(")", "");
        final String[] parts = cleanCoord.split("=");
        final String result = parts[1].split("\\[")[1];
        return result.replace("]", "");
    }

    private String newCoordWithDelta(final String actualCoord, final int deltaX, final int deltaY, final String direction) {

        final String cleanCoord = actualCoord.trim().replace("(", "").replace(")", "");
        final String[] parts = cleanCoord.split("=");
        final String[] coords = parts[0].split(",");
        final String letter = parts[1].split("\\[")[0];
        final int x = Integer.parseInt(coords[0]) + deltaX;
        final int y = Integer.parseInt(coords[1]) + deltaY;
        return this.buildCoordDetails(x, y, letter, direction);
    }

    private boolean coordHasValue(final String coord, final String letter) {

        final int x = this.getX(coord);
        final int y = this.getY(coord);
        if (x < 0 || x >= this.cols) {
            return false;
        }
        if (y < 0 || y >= this.rows) {
            return false;
        }
        return this.getChar(x, y).equalsIgnoreCase(letter);
    }

    private String getChar(final int x, final int y) {

        return this.input[y].charAt(x) + "";
    }

    @Override
    public String part2(final String[] input) {

        this.input = input;
        this.rows = input.length;
        this.cols = input[0].length();

        return String.valueOf(this.countXmasXs().size());
    }

    private List<String> countXmasXs() {

        final List<String> startCoords = new ArrayList<>();

        for (int y = 0; y < this.rows; y++) {
            for (int x = 0; x < this.cols; x++) {
                if (this.getChar(x, y).equalsIgnoreCase("A")) {
                    final String coordDetails = this.buildCoordDetails(x, y, "A", "");
                    startCoords.add(coordDetails);
                }
            }
        }

        final List<String> validCoords = new ArrayList<>();
        for (final String coord : startCoords) {
            if (this.validCoord(coord)) {
                validCoords.add(coord);
            }
        }

        return validCoords;
    }

    private boolean validCoord(final String coord) {

        final String tl = this.getCharAt(coord, -1, -1);
        final String tr = this.getCharAt(coord, 1, -1);
        final String bl = this.getCharAt(coord, -1, 1);
        final String br = this.getCharAt(coord, 1, 1);

        boolean forward = false;
        boolean backward = false;
        if ((tl.equalsIgnoreCase("M") && br.equalsIgnoreCase("S"))) {
            forward = true;
        }
        if ((tl.equalsIgnoreCase("S") && br.equalsIgnoreCase("M"))) {
            forward = true;
        }
        if ((tl.equalsIgnoreCase("M") && br.equalsIgnoreCase("S"))) {
            forward = true;
        }
        if ((tr.equalsIgnoreCase("S") && bl.equalsIgnoreCase("M"))) {
            backward = true;
        }
        if ((tr.equalsIgnoreCase("M") && bl.equalsIgnoreCase("S"))) {
            backward = true;
        }
        return forward && backward;
    }

    private String getCharAt(final String coord, final int deltaX, final int deltaY) {

        final int x = this.getX(coord) + deltaX;
        final int y = this.getY(coord) + deltaY;

        if (x < 0 || x >= this.cols) {
            return "";
        }
        if (y < 0 || y >= this.rows) {
            return "";
        }

        return this.getChar(x, y);
    }
}
