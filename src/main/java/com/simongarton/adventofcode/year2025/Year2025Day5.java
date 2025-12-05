package com.simongarton.adventofcode.year2025;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Year2025Day5 extends AdventOfCodeChallenge {

    private List<Range> ranges;

    @Override
    public String title() {
        return "Day 5: Cafeteria";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2025, 5);
    }

    @Override
    public String part1(final String[] input) {

        this.ranges = new ArrayList<>();
        final List<Long> numbers = new ArrayList<>();

        for (final String line : input) {

            if (line.isEmpty()) {
                continue;
            }

            if (line.contains("-")) {
                final Range range = this.buildRange(line);
                this.handleRange(range);
                this.ranges.sort(Comparator.comparing(Range::getStart).thenComparing(Range::getEnd));
                continue;
            }

            numbers.add(Long.parseLong(line));
        }

        long freshIngredients = 0;
        for (final Long number : numbers) {
            if (this.fresh(number)) {
                freshIngredients++;
            }
        }

        return String.valueOf(freshIngredients);
    }

    private boolean fresh(final Long number) {

        for (final Range range : this.ranges) {
            if (range.contains(number)) {
                return true;
            }
        }
        return false;
    }

    private void handleRange(final Range range) {

        int index = 0;
        while (index < this.ranges.size()) {
            final Range existing = this.ranges.get(index);
            if (existing.equals(range)) {
                return;
            }
            if (existing.overlaps(range)) {
                final Range mergedRange = existing.merge(range);
                this.ranges.remove(index);
                this.handleRange(mergedRange);
                return;
            }
            index++;
        }
        this.ranges.add(range);
    }

    private Range buildRange(final String line) {

        final String[] parts = line.split("-");
        return new Range(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
    }

    @Override
    public String part2(final String[] input) {

        this.ranges = new ArrayList<>();
        final List<Long> numbers = new ArrayList<>();

        for (final String line : input) {

            if (line.isEmpty()) {
                continue;
            }

            if (line.contains("-")) {
                final Range range = this.buildRange(line);
                this.handleRange(range);
                this.ranges.sort(Comparator.comparing(Range::getStart).thenComparing(Range::getEnd));
                continue;
            }

            numbers.add(Long.parseLong(line));
        }

        this.secondPass();

        final long freshIds = this.ranges.stream().map(Range::getRange).reduce(0L, Long::sum);

        return String.valueOf(freshIds);
    }

    private void secondPass() {

        // iterate through the ranges. if I find either is inside the other,
        // or they are the same, pull the right one out and
        // and repeat until nothing is happening

        boolean changesMade = true;
        while (changesMade) {
            int index = 0;
            changesMade = false;
            while (index < this.ranges.size() - 1) {
                final Range first = this.ranges.get(index);
                final Range second = this.ranges.get(index + 1);
                if (first.inside(second)) {
                    this.ranges.remove(index);
                    changesMade = true;
                    break;
                }
                if (second.inside(first)) {
                    this.ranges.remove(index + 1);
                    changesMade = true;
                    break;
                }
                if (first.sameRange(second)) {
                    this.ranges.remove(index);
                    changesMade = true;
                    break;
                }
                index++;
            }

        }
    }

    public static class Range {
        private final long start;
        private final long end;

        public Range(final long start, final long end) {
            this.start = start;
            this.end = end;
        }

        public long getStart() {
            return this.start;
        }

        public long getEnd() {
            return this.end;
        }

        public boolean overlaps(final Range other) {

            if (this.inside(other)) {
                return false;
            }

            if (other.end < this.start) {
                return false;
            }
            if (other.start > this.end) {
                return false;
            }
            return true;
        }

        public boolean inside(final Range biggerOne) {

            return ((this.start >= biggerOne.start) && (this.end <= biggerOne.end));
        }

        public Range merge(final Range other) {

            final long min = Math.min(this.start, other.start);
            final long max = Math.max(this.end, other.end);
            return new Range(min, max);
        }

        @Override
        public String toString() {
            return this.start + "-" + this.end;
        }

        public boolean sameRange(final Range range) {
            return (this.start == range.start) && (this.end == range.end);
        }

        public boolean contains(final Long number) {
            return this.start <= number && this.end >= number;
        }

        public Long getRange() {

            return this.end - this.start + 1;
        }
    }
}