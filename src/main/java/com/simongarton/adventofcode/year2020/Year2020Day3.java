package com.simongarton.adventofcode.year2020;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.Arrays;
import java.util.List;

public class Year2020Day3 extends AdventOfCodeChallenge {

    @Override
    public Outcome run() {
        return this.runChallenge(2020, 3);
    }

    @Override
    public String part1(final String[] input) {
        final List<String> lines = Arrays.asList(input);
        return String.valueOf(this.countTrees(lines, 3, 1));
    }

    @Override
    public String part2(final String[] input) {
        final List<String> lines = Arrays.asList(input);
        long totalTrees = this.countTrees(lines, 1, 1);
        totalTrees = totalTrees * this.countTrees(lines, 3, 1);
        totalTrees = totalTrees * this.countTrees(lines, 5, 1);
        totalTrees = totalTrees * this.countTrees(lines, 7, 1);
        totalTrees = totalTrees * this.countTrees(lines, 1, 2);
        return String.valueOf(totalTrees);
    }

    private int countTrees(final List<String> lines, final int right, final int down) {
        int row = 0;
        int trees = 0;
        int col = 0;
        while (true) {
            row = row + down;
            if (row >= lines.size()) {
                break;
            }
            col = col + right;
            final String line = lines.get(row);
            final String plot = line.substring(col % line.length(), col % line.length() + 1);
            String what = "O";
            if (plot.equalsIgnoreCase("#")) {
                trees++;
                what = "X";
            }
            final StringBuilder output = new StringBuilder();
            while (output.length() <= col) {
                output.append(line);
            }
        }
        return trees;
    }
}
