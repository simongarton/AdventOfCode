package com.simongarton.adventofcode.year2020;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Year2020Day5 extends AdventOfCodeChallenge {

    @Override
    public boolean run() {
        return this.runChallenge(2020, 5);
    }

    @Override
    public String part1(final String[] input) {

        final List<String> lines = Arrays.asList(input);
        long maxId = 0;
        final Map<Long, String> ids = new HashMap<>();
        for (final String line : lines) {
            final long id = this.analysePass(line);
            if (id > maxId) {
                maxId = id;
            }
            ids.put(id, line);
        }
        return String.valueOf(maxId);
    }

    @Override
    public String part2(final String[] input) {

        final List<String> lines = Arrays.asList(input);
        long maxId = 0;
        final Map<Long, String> ids = new HashMap<>();
        for (final String line : lines) {
            final long id = this.analysePass(line);
            if (id > maxId) {
                maxId = id;
            }
            ids.put(id, line);
        }
        for (long l = 1; l < maxId - 1; l++) {
            if ((ids.get(l - 1) != null) &&
                    (ids.get(l) == null) &&
                    (ids.get(l + 1) != null)) {
                return String.valueOf(l);
            }
        }
        throw new RuntimeException("not found");
    }

    private long analysePass(final String line) {
        final long row = this.binarySplit(line, 7, 0, 0, 127, "F", "B");
        final long col = this.binarySplit(line, 3, 7, 0, 7, "L", "R");
        return ((row * 8) + col);
    }

    private long binarySplit(final String line, final int limit, final int offset, long min, long max, final String lower, final String upper) {
        for (int i = 0; i < limit; i++) {
            final long range = (max - min) / 2;
            if (line.substring(offset + i, offset + i + 1).equalsIgnoreCase(lower)) {
                max = min + range;
            } else {
                min = min + range + 1;
            }
        }
        return min;
    }
}
