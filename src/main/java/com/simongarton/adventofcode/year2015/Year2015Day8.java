package com.simongarton.adventofcode.year2015;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

public class Year2015Day8 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 8: Matchsticks";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2015, 8);
    }

    @Override
    public String part1(final String[] input) {

        // I think \\" is going to be a mess, also \\x1A

        int total = 0;
        for (final String line : input) {
            total += this.gobbleScore(line);
        }

        return String.valueOf(total);
    }

    private int gobbleScore(final String line) {

        int total = 0;
        // the strings I'm reading in INCLUDE the double quotes
        String workingLine = line.substring(1, line.length() - 1);
        while (!workingLine.isEmpty()) {

            final String first = workingLine.substring(0, 1);

            // if it's not a / then it has to be normal
            if (!first.equalsIgnoreCase("\\")) {
                total += 1;
                workingLine = workingLine.substring(1);
                continue;
            }
            if (workingLine.length() == 1) {
                break;
            }

            final String second = workingLine.substring(1, 2);
            if (second.equalsIgnoreCase("\"") || second.equalsIgnoreCase("\\")) {
                total += 1;
                workingLine = workingLine.substring(2);
                continue;
            }

            if (!second.equalsIgnoreCase("x")) {
                total += 1;
                workingLine = workingLine.substring(1);
                continue;
            }

            if (workingLine.length() < 4) {
                total += 1;
                workingLine = workingLine.substring(1);
                continue;
            }

            final String hex = workingLine.substring(2, 4);
            if (this.isHex(hex)) {
                total += 1;
                workingLine = workingLine.substring(4);
                continue;
            }
        }
        return line.length() - total;
    }

    private boolean isHex(final String hex) {

        try {
            Integer.parseInt(hex, 16);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String part2(final String[] input) {

        int total = 0;
        for (final String line : input) {
            total += this.reverseGobbleScore(line);
        }

        return String.valueOf(total);

    }

    private int reverseGobbleScore(final String line) {

        final StringBuilder result = new StringBuilder("\"");
        for (int i = 0; i < line.length(); i++) {

            final String current = line.substring(i, i + 1);
            if (current.equalsIgnoreCase("\"")) {
                result.append("\\");
                result.append(current);
                continue;
            }
            if (current.equalsIgnoreCase("\\")) {
                result.append("\\");
                result.append(current);
                continue;
            }
            result.append(current);
        }
        result.append("\"");

        return result.length() - line.length();
    }
}
