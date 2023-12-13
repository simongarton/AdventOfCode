package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;

public class Year2023Day13 extends AdventOfCodeChallenge {

    List<List<String>> grids;

    @Override
    public String title() {
        return "Day 13: Point of Incidence";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 13);
    }

    @Override
    public String part1(final String[] input) {

        this.grids = this.loadGrids(input);
        long score = 0;
        for (final List<String> grid : this.grids) {
            final long thisScore = this.scoreGrid(grid);
            System.out.println("grid " + grid + " scored " + thisScore);
            score += thisScore;
        }
        return String.valueOf(score);
    }

    private long scoreGrid(final List<String> grid) {
        final int width = grid.get(0).length();
        final int height = grid.size();

        for (int i = 0; i < width - 1; i++) {
            if (this.reflectsVertical(grid, i)) {
                return (i + 1); // 1 based
            }
        }
        for (int i = 0; i < height - 1; i++) {
            if (this.reflectsHorizontal(grid, i)) {
                return (long) (i + 1) * 100; // 1 based
            }
        }
        throw new RuntimeException("broke");
    }

    private boolean reflectsHorizontal(final List<String> grid, final int i) {
        // i is a row, after which comes the split
        int diff = 0;
        final int height = grid.size();
        while (true) {
            for (int col = 0; col < grid.get(0).length(); col++) {
                final String top = this.get(grid, col, i - diff);
                final String bottom = this.get(grid, col, i + diff + 1);
                if (!top.equalsIgnoreCase(bottom)) {
                    return false;
                }
            }
            diff = diff + 1;
            if ((i - diff) < 0 || diff == (height - 1 - i)) {
                return true;
            }
        }
    }

    private boolean reflectsVertical(final List<String> grid, final int i) {
        // i is a col, after which comes the split
        int diff = 0;
        final int width = grid.get(0).length();
        while (true) {
            for (int row = 0; row < grid.size(); row++) {
                final String left = this.get(grid, i - diff, row);
                final String right = this.get(grid, i + diff + 1, row);
                if (!left.equalsIgnoreCase(right)) {
                    return false;
                }
            }
            diff = diff + 1;
            if ((i - diff) < 0 || diff == (width - 1 - i)) {
                return true;
            }
        }
    }

    private String get(final List<String> grid, final int col, final int row) {
        return grid.get(row).substring(col, col + 1);
    }

    private List<List<String>> loadGrids(final String[] input) {
        final List<List<String>> grids = new ArrayList<>();
        final List<String> currentGrid = new ArrayList<>();
        for (final String line : input) {
            if (line.isEmpty()) {
                grids.add(new ArrayList<>(currentGrid));
                currentGrid.clear();
            } else {
                currentGrid.add(line);
            }
        }
        grids.add(new ArrayList<>(currentGrid));
        return grids;
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }
}
