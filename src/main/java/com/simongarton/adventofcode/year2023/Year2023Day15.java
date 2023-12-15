package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

public class Year2023Day15 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 15: Lens Library";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 15);
    }

    @Override
    public String part1(final String[] input) {
        final String[] parts = input[0].split(",");
        long total = 0;
        for (final String part : parts) {
            total = total + this.hash(part);
        }
        return String.valueOf(total);
    }

    private long hash(final String part) {
        int current = 0;
        for (int index = 0; index < part.length(); index++) {
            current = current + this.ascii(part.substring(index, index + 1));
            current = current * 17;
            current = current % 256;
        }
        return current;
    }

    private int ascii(final String substring) {
        return substring.charAt(0);
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }
}
