package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.List;

public class Year2023Day1 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day10: Template code";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 1);
    }

    @Override
    public String part1(final String[] input) {
        long total = 0;
        for (final String line : input) {
            total += this.getNumber(line);
        }
        return String.valueOf(total);
    }

    private long getNumber(final String line) {
        String first = null;
        String last = null;
        for (int i = 0; i < line.length(); i++) {
            final String c = line.substring(i, i + 1);
            if (this.isNumeric(c)) {
                if (first == null) {
                    first = c;
                    last = c;
                } else {
                    last = c;
                }
            }
        }
        System.out.println(first + last);
        return Long.valueOf(first + last);
    }

    private boolean isNumeric(final String substring) {
        return List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9").contains(substring);
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }
}
