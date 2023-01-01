package com.simongarton.adventofcode.year2020;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Year2020Day9 extends AdventOfCodeChallenge {

    private static final int TEST_LENGTH = 5;
    private static final int LENGTH = 25;

    @Override
    public String title() {
        return "Day 9: Encoding Error";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2020, 9);
    }

    @Override
    public String part1(final String[] input) {
        final List<String> lines = Arrays.asList(input);
        final List<Long> numbers = lines.stream().map(Long::parseLong).collect(Collectors.toList());
        final long error = this.findError(numbers, TEST_LENGTH);
        return String.valueOf(this.findWeakness(error, numbers));
    }

    @Override
    public String part2(final String[] input) {
        final List<String> lines = Arrays.asList(input);
        final List<Long> numbers = lines.stream().map(Long::parseLong).collect(Collectors.toList());
        final long error = this.findError(numbers, LENGTH);
        return String.valueOf(this.findWeakness(error, numbers));
    }

    private long findWeakness(final long target, final List<Long> numbers) {
        for (int i = 0; i < numbers.size(); i++) {
            for (int j = i + 1; j < numbers.size(); j++) {
                long total = 0;
                long lowest = numbers.get(i);
                long highest = 0;
                for (int k = i; k < j; k++) {
                    total += numbers.get(k);
                    if (lowest > numbers.get(k)) {
                        lowest = numbers.get(k);
                    }
                    if (highest < numbers.get(k)) {
                        highest = numbers.get(k);
                    }
                }
                if (total == target) {
                    return lowest + highest;
                }
            }
        }
        return 0L;
    }

    private long findError(final List<Long> data, final int length) {
        final List<Long> numbers = new ArrayList<>();
        int index = 0;
        while (index < length) {
            numbers.add(data.get(index));
            index++;
        }
        while (index < data.size()) {
            final long next = data.get(index);
            if (!this.valid(next, numbers)) {
                return next;
            }
            index++;
            numbers.remove(0);
            numbers.add(next);
        }
        return -1;
    }

    private boolean valid(final long next, final List<Long> numbers) {
        for (int i = 0; i < numbers.size(); i++) {
            for (int j = 0; j < numbers.size(); j++) {
                if (i == j) {
                    continue;
                }
                if (next == numbers.get(i) + numbers.get(j)) {
                    return true;
                }
            }
        }
        return false;
    }
}
