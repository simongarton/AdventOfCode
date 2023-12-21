package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Year2023Day21 extends AdventOfCodeChallenge {

    // must be odd
    private static final int GRID_SIZE = 9;

    private String map;
    private int width;
    private int height;
    List<List<Long>> gridCounts;
    Map<Integer, Integer> gridSteady;

    @Override
    public String title() {
        return "Day 21: Step Counter";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 21);
    }

    @Override
    public String part1(final String[] input) {

        this.loadMap(input);
        this.width = input[0].length();
        this.height = input.length;

        this.map = this.map.replace("S", "O");
        for (int step = 0; step < 64; step++) {
            this.walkies();
//            this.debugMap();
        }

        return String.valueOf(this.countGotSomewhere());
    }

    private void dumpGridCounts() {
        final List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder("index" + ",");
        for (int grid = 0; grid < GRID_SIZE * GRID_SIZE; grid++) {
            line.append(grid).append(",");
        }
        lines.add(line.substring(0, line.length() - 1));
        for (int index = 0; index < 64; index++) {
            line = new StringBuilder(index + ",");
            for (int grid = 0; grid < GRID_SIZE * GRID_SIZE; grid++) {
                line.append(this.gridCounts.get(grid).get(index)).append(",");
            }
            lines.add(line.toString());
        }
        try {
            Files.write(Path.of("gridCounts.csv"), lines);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkGrids(final int step) {
        for (int grid = 0; grid < GRID_SIZE * GRID_SIZE; grid++) {
            this.checkGrid(step, grid);
        }
    }

    private void checkGrid(final int step, final int grid) {
        if (this.gridSteady.get(grid) > 0L) {
            return;
        }
        final long count1 = this.gridCounts.get(grid).get(step - 1);
        final long count2 = this.gridCounts.get(grid).get(step);
        if ((count1 == 39 && count2 == 42) || (count1 == 42 && count2 == 39)) {
            final String cycle = count1 == 39 ? "even" : "odd";
            System.out.println("grid " + grid + " steady at step " + step + " and is on " + cycle + " cycle.");
            this.gridSteady.put(grid, step);
        }
    }

    private void countGrids() {
        int i = 0;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                final long result = this.countGrid(i, row, col);
                this.gridCounts.get(i).add(result);
                i++;
            }
        }
    }

    private long countGrid(final int grid, final int row, final int col) {
        // for this ith grid, count the 11x11 grid
        String line = "";
        final int startX = 11 * col;
        for (int i = 0; i < 11; i++) {
            final int index = ((i + (row * 11)) * this.width) + startX;
            final String fragment = this.map.substring(index, index + 11);
            line = line + fragment;
        }
        final long result = line.chars().filter(ch -> ch == 'O').count() + line.chars().filter(ch -> ch == 'S').count();
        return result;
    }

    private long countGotSomewhere() {
        return this.map.chars().filter(ch -> ch == 'O').count() + 1;
    }

    private void walkies() {

        final String original = this.map;
        for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
                if (this.getCell(original, row, col).equalsIgnoreCase("O")) {
                    this.walky(row, col);
                }
            }
        }
//        this.map = this.map.substring(0, this.start) + "S" + this.map.substring(this.start + 1);
    }

    private void walky(final int row, final int col) {

        this.maybe(row - 1, col);
        this.maybe(row + 1, col);
        this.maybe(row, col - 1);
        this.maybe(row, col + 1);
        this.map = this.replaceCharacter(this.map, row, col, ".");
    }

    private void maybe(final int row, final int col) {

        if (row < 0 || row >= this.height || col < 0 || col >= this.width) {
            return;
        }
        final String old = this.getCell(this.map, row, col);
        if (old.equalsIgnoreCase("#")) {
            return;
        }
        this.map = this.replaceCharacter(this.map, row, col, "O");
    }

    private String replaceCharacter(final String original, final int row, final int col, final String replacement) {

        final int index = (row * this.width) + col;
        return original.substring(0, index) + replacement + original.substring(index + 1);
    }

    private String getCell(final String original, final int row, final int col) {

        final int index = (row * this.width) + col;
        return original.substring(index, index + 1);
    }

    private void blankLine() {

        System.out.println();
    }

    private void debugMap() {

        this.debug(this.map);
    }

    private void debug(final String aMap) {
        for (int row = 0; row < this.height; row++) {
            final String line = aMap.substring(row * this.width, (row + 1) * this.width);
            System.out.println(line);
        }
        this.blankLine();
    }

    private void loadMap(final String[] input) {

        this.map = String.join("", input);
    }

    private void buildMap(final String[] input) {

        final int halfGridSize = GRID_SIZE / 2;

        String line = "";
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int i = 0; i < 11; i++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    if (row == halfGridSize && col == halfGridSize) {
                        line = line + input[i].replace("S", "O");
                    } else {
                        line = line + input[i].replace("S", ".");
                    }
                }
            }
        }
        this.map = line;
    }

    @Override
    public String part2(final String[] input) {

        this.buildMap(input);
        this.width = GRID_SIZE * input[0].length();
        this.height = GRID_SIZE * input.length;

        this.debugMap();

        this.gridCounts = new ArrayList<>();
        this.gridSteady = new HashMap<>();
        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
            this.gridCounts.add(new ArrayList<>());
            this.gridSteady.put(i, 0);
        }

        for (int step = 0; step < 64; step++) {
            this.walkies();
            this.countGrids();
            if (step > 0) {
                this.checkGrids(step);
            }
        }

        this.debugMap();
        this.dumpGridCounts();

        return String.valueOf(this.countGotSomewhere());
    }
}
