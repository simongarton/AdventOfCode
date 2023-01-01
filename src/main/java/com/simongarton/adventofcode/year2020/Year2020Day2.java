package com.simongarton.adventofcode.year2020;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.Arrays;
import java.util.List;

public class Year2020Day2 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 2: Password Philosophy";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2020, 2);
    }

    @Override
    public String part1(final String[] input) {
        return String.valueOf(this.countValid1(Arrays.asList(input)));
    }

    @Override
    public String part2(final String[] input) {
        return String.valueOf(this.countValid2(Arrays.asList(input)));
    }

    private int countValid1(final List<String> lines) {
        int valid = 0;
        for (final String line : lines) {
            if (this.validLine1(line)) {
                valid++;
            }
        }
        return valid;
    }

    private int countValid2(final List<String> lines) {
        int valid = 0;
        for (final String line : lines) {
            if (this.validLine2(line)) {
                valid++;
            }
        }
        return valid;
    }

    private boolean validLine1(final String line) {
        final String[] parts = line.split(" ");
        final String[] ranges = parts[0].split("-");
        final int low = Integer.parseInt(ranges[0]);
        final int high = Integer.parseInt(ranges[1]);
        final String letter = parts[1].replace(":", "");
        final String target = parts[2];
        final int count = this.count(target, letter);
        return count >= low && count <= high;
    }

    public boolean validLine2(final String line) {
        final String[] parts = line.split(" ");
        final String[] ranges = parts[0].split("-");
        final int low = Integer.parseInt(ranges[0]) - 1;
        final int high = Integer.parseInt(ranges[1]) - 1;
        final String letter = parts[1].replace(":", "");
        final String target = parts[2];
        if (high >= target.length()) {
            return false;
        }
        int score = 0;
        if (target.substring(low, low + 1).equalsIgnoreCase(letter)) {
            score++;
        }
        if (target.substring(high, high + 1).equalsIgnoreCase(letter)) {
            score++;
        }
        return score == 1;
    }

    private int count(final String input, final String search) {
        return (input.length() - input.replace(search, "").length()) / search.length();
    }
}
