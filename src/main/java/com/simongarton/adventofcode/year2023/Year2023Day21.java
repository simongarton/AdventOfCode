package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Year2023Day21 extends AdventOfCodeChallenge {

    /*

    Well indeed. I think I should be (eventually) able to solve this by inspecting repetition. Based on using the
    sample grid, and expanding it manually when loading it in, I can see various things:

    - I focus on sub-grids : 11x11 grids around the original
    - I look for them to become complete : they alternate between 39 and 42 plots
    - there is a clear pattern, repeating every 11 steps, where I can see a set of new grids complete
    - the first pattern is complete at 31 steps; then at 42, 53, 64, etc
    - each pattern adds eight complete
    - on the first step of the repeat (so step 31, step 42 etc.)
    -    I get N complete grids on an even cycle where N = 3 * the pattern index
         (step 31 = pattern 1, step 42 = pattern 2 etc)
    -    4 steps later (35, 46 etc) I get M complete grids on an even cycle, where M is the pattern index
    -    1 step later a single complete grid on an ODD cycle.
    -    1 step later a single complete grid on an even cycle.
    -    2 steps later 2 complete grids on an even cycle.
    - at which point I have added 4 * pattern index grids (4 corners !)
    -    pattern 1 added 8 grids, pattern 2 added 12, 3 added 16

    So every 11 steps, starting from 31, I can tell you how many complete grids I have, and what cycle of (39, 42)
    the are on.

    What I can't tell you is how many incomplete grids there are - well, I can predict the number I'm going to add,
    having completed pattern 3, I will be adding (4 * next pattern index) = 16 grids. Some of them may not be started
    (almost certainly false) but I don't know if the next row out is also started !

    I looked at "warm up patterns" - once a grid gets some, how does it grow ? But I seemed to get quite a range -
    after 128 steps in a 99x99 repeating sub-grid pattern, I had 15 patterns : 4 had 45 repeats, 2 had 8, 2 had 9 and
    7 only had 1 repeat.

    AND ! All of this is done with the sample grid, will I even be able to tackle this with the full grid ?
    Looks like I can build a grid 99 x 99 but I haven't seen a complete step yet. 19 x 19 is working slowly, but slows
    down quickly ... and after a couple of minutes (a) hasn't completed one grid and (b) hit's a range check on
    getCell(), so something is wrong.

    Abandoning.

     */

    // must be odd
    private static final int GRID_SIZE = 9;

    private String map;
    private int width;
    private int originalWidth;
    private int height;
    private List<List<Long>> gridCounts;
    private Map<Integer, Integer> gridSteady;
    private Map<Integer, String> warmUps;
    private Map<String, Integer> warmUpDefinitions;
    private List<String> warmUpDefinitionList;

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
        }

        return String.valueOf(this.countGotSomewhere());
    }

    // this is how I found the patterns - dump to CSV and inspect.
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

    // this gives me some logging : each time a grid completes, write it out, the step, if it's odd or even
    // and the warmup pattern number
    private void checkGrid(final int step, final int grid) {
        if (this.gridSteady.get(grid) > 0L) {
            return;
        }
        final long count1 = this.gridCounts.get(grid).get(step - 1);
        final long count2 = this.gridCounts.get(grid).get(step);
        if ((count1 == 39 && count2 == 42) || (count1 == 42 && count2 == 39)) {
            final String cycle = count1 == 39 ? "even" : "odd";
            final String warmUp = this.warmUps.get(grid);
//            System.out.println("grid " + grid + " steady at step " + step + " and is on "
//                    + cycle + " cycle after warmup " + warmUp);
            this.gridSteady.put(grid, step);
            this.warmUpDefinitions.put(warmUp, this.warmUpDefinitions.getOrDefault(warmUp, 0) + 1);
            if (!this.warmUpDefinitionList.contains(warmUp)) {
                this.warmUpDefinitionList.add(warmUp);
            }
            final String line = grid + "," + step + "," + cycle + "," + this.warmupDefinition(warmUp);
            System.out.println(line);
            return;
        }
        if (count1 == 0 && count2 == 0) {
            return;
        }
        if (count1 == 0) {
            final String line = count2 + ",";
            this.warmUps.put(grid, line);
        }
        // first grid is special
        final String warmup = this.warmUps.get(grid) == null ? "1" : this.warmUps.get(grid);
        this.warmUps.put(grid, warmup + count2 + ",");
    }

    private int warmupDefinition(final String warmUp) {
        return this.warmUpDefinitionList.indexOf(warmUp);
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
        final String result;
        try {
            result = original.substring(index, index + 1);
        } catch (final StringIndexOutOfBoundsException e) {
            System.out.println(row + "," + col + " " + original);
            throw new RuntimeException(e);
        }
        return result;
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
            for (int i = 0; i < this.originalWidth; i++) {
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

        this.originalWidth = input[0].length();
        this.buildMap(input);
        this.width = GRID_SIZE * input[0].length();
        this.height = GRID_SIZE * input.length;

        this.gridCounts = new ArrayList<>();
        this.gridSteady = new HashMap<>();
        this.warmUps = new HashMap<>();
        this.warmUpDefinitions = new TreeMap<>();
        this.warmUpDefinitionList = new ArrayList<>();
        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
            this.gridCounts.add(new ArrayList<>());
            this.gridSteady.put(i, 0);
        }

        for (int step = 0; step < 10; step++) {
            this.walkies();
            this.countGrids();
            if (step > 0) {
                this.checkGrids(step);
            }
        }

//        this.dumpGridCounts();
//        this.dumpWarmUpDefinitions();

        return String.valueOf(-1);
    }

    private void dumpWarmUpDefinitions() {
        this.blankLine();
        System.out.println("warmups");
        for (final Map.Entry<String, Integer> entry : this.warmUpDefinitions.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }
}
