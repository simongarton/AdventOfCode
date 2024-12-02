package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Year2024Day2 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 2: Red-Nosed Reports";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 2);
    }

    @Override
    public String part1(final String[] input) {

        final List<List<Integer>> levels = this.buildLevels(input);

        int safe = 0;
        for (final List<Integer> level : levels) {
            if (this.levelIsSafe(level)) {
                safe++;
            }
        }

        return String.valueOf(safe);
    }

    private List<List<Integer>> buildLevels(final String[] input) {

        final List<List<Integer>> levels = new ArrayList<>();
        for (final String line : input) {
            final String[] readings = line.split(" ");
            levels.add(Arrays.stream(Arrays.stream(readings).toArray()).map(s -> Integer.parseInt((String) s))
                    .collect(Collectors.toList()));
        }
        return levels;
    }

    private boolean levelIsSafe(final List<Integer> level) {

        final int delta = level.get(1) - level.get(0);
        for (int index = 1; index < level.size(); index++) {
            final int thisDelta = level.get(index) - level.get(index - 1);
            final int absoluteDelta = Math.abs(thisDelta);
            if (absoluteDelta < 1 || absoluteDelta > 3) {
                return false;
            }
            if (this.isPositive(delta) != this.isPositive(thisDelta)) {
                return false;
            }
        }

        return true;
    }

    private boolean isPositive(final int delta) {

        // overkill
        return delta > 0;
    }

    @Override
    public String part2(final String[] input) {

        final List<List<Integer>> levels = this.buildLevels(input);

        int safe = 0;
        for (final List<Integer> level : levels) {
            if (this.levelIsSafeWithVariations(level)) {
                safe++;
            }
        }

        return String.valueOf(safe);
    }

    private boolean levelIsSafeWithVariations(final List<Integer> level) {

        // simply iterate through the list, making a new list of levels with
        // one removed each time, and return true if any are safe

        if (this.levelIsSafe(level)) {
            return true;
        }

        for (int index = 0; index < level.size(); index++) {
            final List<Integer> newLevel = this.updateLevel(level, index);
            if (this.levelIsSafe(newLevel)) {
                return true;
            }
        }

        return false;
    }

    private List<Integer> updateLevel(final List<Integer> level, final int remove) {

        final List<Integer> newLevel = new ArrayList<>();
        for (int index = 0; index < level.size(); index++) {
            if (index != remove) {
                newLevel.add(level.get(index));
            }
        }
        return newLevel;
    }
}
