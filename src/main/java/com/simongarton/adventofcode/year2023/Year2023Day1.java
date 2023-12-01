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
        return Long.valueOf(first + last);
    }

    private boolean isNumeric(final String substring) {
        return NUMBER_MAP.containsValue(substring);
    }

    @Override
    public String part2(final String[] input) {
        long total = 0;
        for (final String line : input) {
            final String fixedLine = this.fixLine(line);
            final long number = this.getNumber(fixedLine);
            total += number;
//            System.out.println(number + ": " + line + " -> " + fixedLine + " = " + number + " total " + total);
            System.out.println(number + " : " + fixedLine);
        }
        return String.valueOf(total);

        /*
        Sample works OK : 281
        I have tried 54780 and it's not right.
        ninesevensrzxkzpmgz8kcjxsbdftwoner -> 97srzxkzpmgz8kcjxsbdf2ner = 92 : "twone" at the end I go to 2 ...
        .. one of the samples had the same thing, works.

        Interesting: A manual search/replace turned into 54014 which also isn't right. But that means my algorithm
        and the manual approach are different.

        eight3fiveninefivemtxm9eightwot is another example : if you replace manually, the two at the end goes first,
        whereas if you do it from the left the eight goes first. Most of the differences were eightwo ...

        new approach gave 54780 :sadface

         */
    }

    private String fixLine(final String line) {
        String fixedLine = line;
        int position = 0;
        while (position < fixedLine.length()) {
            for (final Map.Entry<String, String> entry : NUMBER_MAP.entrySet()) {
                final String target = entry.getKey();
                final int targetLength = target.length();
                if (fixedLine.length() >= (position + targetLength)) {
                    final String source = fixedLine.substring(position, position + targetLength);
                    if (source.equalsIgnoreCase(target)) {
                        fixedLine = fixedLine.substring(0, position) +
                                entry.getValue() +
                                // this was the problem. I assumed that we should replace all the
                                // characters of the number so xxeightwoyy becomes xx8woyy,
                                // but they're just looking for the last one : not replacing at all
                                // so two is the last readable one
                                // fixedLine.substring(position + targetLength);
                                fixedLine.substring(position + 1);
                        break;
                    }
                }
            }
            position = position + 1;
        }
        return fixedLine;
    }

    private String fixLineOriginal(final String line) {
        String fixedLine = line;
        int position = 0;
        while (position < fixedLine.length()) {
            for (final Map.Entry<String, String> entry : NUMBER_MAP.entrySet()) {
                final String target = entry.getKey();
                final int targetLength = target.length();
                if (fixedLine.length() >= (position + targetLength)) {
                    final String source = fixedLine.substring(position, position + targetLength);
                    if (source.equalsIgnoreCase(target)) {
                        fixedLine = fixedLine.substring(0, position) +
                                entry.getValue() +
                                fixedLine.substring(position + targetLength);
                        break;
                    }
                }
            }
            position = position + 1;
        }
        return fixedLine;
    }

    private String fixLineHorrible(final String line) {
        String answer = "";
        int position = 0;
        int skip = 0;
        String adder = "";
        while (position < line.length()) {
            boolean found = false;
            for (final Map.Entry<String, String> entry : NUMBER_MAP.entrySet()) {
                if (found) {
                    break;
                }
                final String target = entry.getKey();
                final int targetLength = target.length();
                if (line.length() < (position + targetLength + 0)) {
                    continue;
                }
                final String source = line.substring(position, position + targetLength);
                if (source.equalsIgnoreCase(target)) {
                    adder = entry.getValue();
                    found = true;
                    skip = targetLength;
                    break;
                }
            }
            if (!found) {
                answer = answer + line.substring(position, position + 1);
                position = position + 1;
            } else {
                answer = answer + adder;
                position = position + skip;
            }
        }
        return answer;
    }
}
