package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

public class Year2022Day4 extends AdventOfCodeChallenge {

    @Override
    public boolean run() {
        return this.runChallenge(2022, 4);
    }

    @Override
    public String part1(final String[] input) {

        int total = 0;
        for (final String line : input) {
            if (this.fullOverlap(line)) {
                total++;
            }
        }

        return String.valueOf(total);
    }

    @Override
    public String part2(final String[] input) {

        int total = 0;
        for (final String line : input) {
            if (this.partialOverlap(line)) {
                total++;
            }
        }

        return String.valueOf(total);
    }

    private boolean fullOverlap(final String line) {
        final String[] assignments = line.split(",");
        final Range elf1 = new Range(assignments[0]);
        final Range elf2 = new Range(assignments[1]);

        return elf1.containedIn(elf2) || elf2.containedIn(elf1);
    }

    private boolean partialOverlap(final String line) {
        final String[] assignments = line.split(",");
        final Range elf1 = new Range(assignments[0]);
        final Range elf2 = new Range(assignments[1]);

        return elf1.overlaps(elf2);
    }

    private static final class Range {

        private final int low;
        private final int high;

        public Range(final String line) {
            final String[] parts = line.split("-");
            this.low = Integer.parseInt(parts[0]);
            this.high = Integer.parseInt(parts[1]);
        }

        public boolean overlaps(final Range other) {
            if (other.high < this.low) {
                return false;
            }
            if (other.low > this.high) {
                return false;
            }
            return true;
        }

        public boolean containedIn(final Range other) {
            if (other.low < this.low) {
                return false;
            }
            if (other.high > this.high) {
                return false;
            }
            return true;
        }
    }
}
