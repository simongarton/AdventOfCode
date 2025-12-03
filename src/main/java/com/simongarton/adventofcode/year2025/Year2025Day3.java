package com.simongarton.adventofcode.year2025;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

public class Year2025Day3 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 3: Lobby";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2025, 3);
    }

    @Override
    public String part1(final String[] input) {

        long joltage = 0;
        for (final String line : input) {
            final long thisJoltage = this.calculateJoltage(line);
            joltage = joltage + thisJoltage;
        }

        return String.valueOf(joltage);
    }

    private long calculateJoltage(final String line) {

        int firstValue = 0;
        int firstIndex = -1;

        for (int target = 9; target > 0; target--) {
            for (int index = 0; index < line.length() - 1; index++) {
                if (line.substring(index, index + 1).equalsIgnoreCase(String.valueOf(target))) {
                    firstIndex = index;
                    firstValue = target;
                    break;
                }
            }
            if (firstValue > 0) {
                break;
            }
        }

        for (int target = 9; target > 0; target--) {
            for (int index = firstIndex + 1; index < line.length(); index++) {
                if (line.substring(index, index + 1).equalsIgnoreCase(String.valueOf(target))) {
                    return Integer.parseInt(firstValue + "" + target);
                }
            }
        }

        throw new RuntimeException("ran out of numbers ...");
    }

    @Override
    public String part2(final String[] input) {

        long joltage = 0;
        for (final String line : input) {
            final long thisJoltage = this.calculateLongJoltage(line);
            joltage = joltage + thisJoltage;
        }

        return String.valueOf(joltage);

    }

    private long calculateLongJoltage(final String line) {

        /*

        New algorithm
        I need the biggest 12 digit number.
        First digit : largest number I can find that is equal to or more than 12 away from the end.
        Second digit :  largest number I can find that is equal to or more than 11 away from the end, starting from after the first
        ...

         */

        final StringBuilder number = new StringBuilder();
        int pointer = -1;
        final int length = line.length();
        for (int digit = 12; digit > 0; digit--) {

            int thisValue = 0;
            for (int position = pointer + 1; position < length + 1 - digit; position++) {
                final int positionValue = Integer.parseInt(line.substring(position, position + 1));
                if (positionValue > thisValue) {
                    thisValue = positionValue;
                    pointer = position;
                }
            }
            number.append(thisValue);
        }

        return Long.parseLong(number.toString());
    }
}
