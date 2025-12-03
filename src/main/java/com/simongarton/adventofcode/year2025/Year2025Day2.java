package com.simongarton.adventofcode.year2025;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Year2025Day2 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 2: Gift Shop";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2025, 2);
    }

    @Override
    public String part1(final String[] input) {

        final String[] ranges = input[0].split(",");
        final List<Long> invalidIDs = new ArrayList<>();
        for (final String range : ranges) {
            invalidIDs.addAll(this.findInvalidIds(range));
        }

        return String.valueOf(invalidIDs.stream().mapToLong(Long::longValue).sum());
    }

    private List<Long> findInvalidIds(final String range) {

        final List<Long> invalidIds = new ArrayList<>();
        final String[] parts = range.split("-");
        final long first = Long.parseLong(parts[0]);
        final long second = Long.parseLong(parts[1]);
        for (long i = first; i <= second; i++) {
            final String data = String.valueOf(i);
            if (data.length() % 2 == 1) {
                continue;
            }
            if (data.substring(0, data.length() / 2).equalsIgnoreCase(data.substring(data.length() / 2))) {
                invalidIds.add(Long.parseLong(data));
            }
        }
        return invalidIds;
    }

    @Override
    public String part2(final String[] input) {

        final String[] ranges = input[0].split(",");
        final List<Long> invalidIDs = new ArrayList<>();
        for (final String range : ranges) {
            invalidIDs.addAll(this.findSpecialInvalidIds(range));
        }

        return String.valueOf(invalidIDs.stream().mapToLong(Long::longValue).sum());
    }

    private List<Long> findSpecialInvalidIds(final String range) {

        final List<Long> invalidIds = new ArrayList<>();
        final String[] parts = range.split("-");
        final long first = Long.parseLong(parts[0]);
        final long second = Long.parseLong(parts[1]);
        for (long i = first; i <= second; i++) {
            final String data = String.valueOf(i);
            final int length = data.length();
            for (int divisor = 1; divisor < length; divisor++) {
                if (length % divisor != 0) {
                    continue;
                }
                final int divisions = length / divisor;
                final Set<String> numberParts = new HashSet<>();

                for (int d = 0; d < divisions; d++) {
                    final String numberPart = data.substring(d * divisor, (d + 1) * divisor);
                    numberParts.add(numberPart);
                }
                // 222220-222224 [222222, 222222, 222222] repeats multiple times and we only count it once
                if (numberParts.size() == 1) {
                    final long dataValue = Long.parseLong(data);
                    if (!(invalidIds.contains(dataValue))) {
                        invalidIds.add(dataValue);
                    }
                }
            }
        }
        return invalidIds;
    }
}
