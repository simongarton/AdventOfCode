package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.LinkedHashMap;
import java.util.Map;

public class Year2023Day1 extends AdventOfCodeChallenge {

    private static final Map<String, String> NUMBER_MAP = new LinkedHashMap<>();

    static {
        NUMBER_MAP.put("one", "1");
        NUMBER_MAP.put("two", "2");
        NUMBER_MAP.put("three", "3");
        NUMBER_MAP.put("four", "4");
        NUMBER_MAP.put("five", "5");
        NUMBER_MAP.put("six", "6");
        NUMBER_MAP.put("seven", "7");
        NUMBER_MAP.put("eight", "8");
        NUMBER_MAP.put("nine", "9");
    }

    @Override
    public String title() {
        return "Day1: Trebuchet?!";
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
        return Long.valueOf(first + last);
    }

    private boolean isNumeric(final String substring) {
        return NUMBER_MAP.containsValue(substring);
    }

    @Override
    public String part2(final String[] input) {
        long total = 0;
        for (final String line : input) {
            final long number = this.getNumberAlphanumeric(line);
            total += number;
        }
        return String.valueOf(total);
    }

    private long getNumberAlphanumeric(final String line) {
        final String first = this.findNumber(line, true);
        final String last = this.findNumber(line, false);
        return Long.valueOf(first + last);
    }

    private String findNumber(final String line, final boolean first) {
        int position = 0;
        String result = null;
        while (position < line.length()) {
            final String c = line.substring(position, position + 1);
            if (this.isNumeric(c)) {
                if (first) {
                    return c;
                } else {
                    result = c;
                }
            }
            for (final Map.Entry<String, String> entry : NUMBER_MAP.entrySet()) {
                final String target = entry.getKey();
                final int targetLength = target.length();
                if (line.length() >= (position + targetLength)) {
                    final String source = line.substring(position, position + targetLength);
                    if (source.equalsIgnoreCase(target)) {
                        if (first) {
                            return entry.getValue();
                        }
                        result = entry.getValue();
                    }
                }
            }
            position++;
        }
        return result;
    }
}
