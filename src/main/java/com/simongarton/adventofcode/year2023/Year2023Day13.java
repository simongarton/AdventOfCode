package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            final long thisScore = this.scoreGrid(this.findBreaks(grid));
            System.out.println("grid " + grid + " scored " + thisScore);
            score += thisScore;
        }
        return String.valueOf(score);
    }

    private long scoreGrid(final List<Break> breaks) {
        long score = 0;
        for (final Break gridBreak : breaks) {
            score += (gridBreak.isHorizontal() ? 100L * gridBreak.getIndex() : gridBreak.getIndex());
        }
        return score;
    }

    private List<Break> findBreaks(final List<String> grid) {

        final List<Break> breaks = new ArrayList<>();

        final int width = grid.get(0).length();
        final int height = grid.size();

        for (int i = 0; i < width - 1; i++) {
            if (this.reflectsVertical(grid, i)) {
                breaks.add(Break.builder().index(i + 1).horizontal(false).build());
            }
        }
        for (int i = 0; i < height - 1; i++) {
            if (this.reflectsHorizontal(grid, i)) {
                breaks.add(Break.builder().index(i + 1).horizontal(true).build());
            }
        }
        return breaks;
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

        this.grids = this.loadGrids(input);
        long score = 0;
        for (final List<String> grid : this.grids) {
            final Set<Break> newBreaks = this.findNewBreaks(grid);
            score = score + this.scoreGrid(new ArrayList<>(newBreaks));
            System.out.println(grid + " " + newBreaks);
        }
        return String.valueOf(score);
    }

    private Set<Break> findNewBreaks(final List<String> grid) {
        final List<Break> existingBreaks = this.findBreaks(grid);
//        System.out.println("Having a look at " + grid);
        final Set<Break> uniqueNewBreaks = new HashSet<>();

        final int width = grid.get(0).length();
        final int height = grid.size();
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
//                System.out.println(" changing w " + w + " h " + h);
                final List<String> newGrid = this.flipGrid(grid, w, h);
                final List<Break> newBreaks = this.findBreaks(newGrid);
                final List<Break> differentBreaks = this.findNewBreaks(existingBreaks, newBreaks);
                if (!differentBreaks.isEmpty()) {
//                    System.out.println("  " + differentBreaks);
                    uniqueNewBreaks.addAll(differentBreaks);
                }
            }
        }
        return uniqueNewBreaks;
    }

    private List<String> flipGrid(final List<String> grid, final int x, final int y) {
        final List<String> newGrid = new ArrayList<>();
        int row = 0;
        for (final String line : grid) {
            if (row == y) {
                newGrid.add(this.replaceCharacter(line, x, this.notWhatWasThere(grid.get(row).substring(x, x + 1))));
            } else {
                newGrid.add(line);
            }
            row++;
        }
        return newGrid;
    }

    private String notWhatWasThere(final String substring) {
        if (substring.equalsIgnoreCase("#")) {
            return ".";
        } else {
            return "#";
        }
    }

    private String replaceCharacter(final String original, final Integer index, final String replacement) {
        return original.substring(0, index) + replacement + original.substring(index + 1);
    }

    private List<Break> findNewBreaks(final List<Break> existingBreaks, final List<Break> newBreaks) {
        final List<Break> breaks = new ArrayList<>();
        for (final Break newBreak : newBreaks) {
            if (!existingBreaks.contains(newBreak)) {
                breaks.add(newBreak);
            }
        }
        return breaks;
    }

    private List<Break> compareBreaks(final List<Break> existingBreaks, final List<Break> newBreaks) {
        final List<Break> breaks = new ArrayList<>();
        if (newBreaks.size() != existingBreaks.size()) {
            return this.differentCounts(newBreaks, existingBreaks);
        }
        for (int i = 0; i < newBreaks.size(); i++) {
            if (newBreaks.get(i).getIndex() != existingBreaks.get(i).getIndex() ||
                    newBreaks.get(i).isHorizontal() != existingBreaks.get(i).isHorizontal()) {
                breaks.add(newBreaks.get(i));
            }
        }
        return breaks;
    }

    private List<Break> differentCounts(final List<Break> newBreaks, final List<Break> existingBreaks) {
        final List<Break> breaks = new ArrayList<>();
        for (final Break existingBreak : existingBreaks) {
            if (!newBreaks.contains(existingBreak)) {
                breaks.add(existingBreak);
            }
        }
        for (final Break newBreak : newBreaks) {
            if (!existingBreaks.contains(newBreak)) {
                breaks.add(newBreak);
            }
        }
        return breaks;
    }

    @Data
    @Builder
    private static final class Break {
        private int index;
        private boolean horizontal;
    }
}
