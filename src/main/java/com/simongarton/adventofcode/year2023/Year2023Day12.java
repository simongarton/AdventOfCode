package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Year2023Day12 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 12: Hot Springs";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 12);
    }

    @Override
    public String part1(final String[] input) {

        long combos = 0;
        for (final String line : input) {
            combos += this.arrangements(line);
        }

        return String.valueOf(combos);
    }

    private long arrangements(final String line) {
        final String[] parts = line.split(" ");
        final String row = parts[0];
        final List<Integer> groups = Arrays.stream(parts[1].split(",")).map(Integer::valueOf).collect(Collectors.toList());

        // I could do brute force
        return this.bruteForce(row, groups);
    }

    private long bruteForce(final String row, final List<Integer> groups) {
        final List<Integer> unknownLocations = new ArrayList<>();
        for (int i = 0; i < row.length(); i++) {
            if (row.substring(i, i + 1).equalsIgnoreCase("?")) {
                unknownLocations.add(i);
            }
        }
        final int combos = (int) Math.pow(2, unknownLocations.size());
        final int maxLength = Integer.toBinaryString(combos).length();

        int validCombos = 0;

        for (int combo = 0; combo < combos; combo++) {
            final String binary = this.leftPad("0", Integer.toBinaryString(combo), maxLength - 1);
            final String testRow = this.makeTestRow(row, binary, unknownLocations);
            final boolean testGroups = this.testGroups(testRow, groups);
            if (testGroups) {
                validCombos++;
            }
        }

        return validCombos;
    }

    private boolean testGroups(final String row, final List<Integer> groupsToTest) {
        int groups = 0;
        final List<Integer> foundGroups = new ArrayList<>();
        boolean inGroup = false;
        for (int i = 0; i < row.length(); i++) {
            if (row.substring(i, i + 1).equalsIgnoreCase(".")) {
                if (inGroup) {
                    foundGroups.add(groups);
                    groups = 0;
                }
                inGroup = false;
            } else {
                if (!inGroup) {
                    inGroup = true;
                }
                groups++;
            }
        }
        if (inGroup) {
            foundGroups.add(groups);
        }
        if (foundGroups.size() != groupsToTest.size()) {
            return false;
        }
        for (int i = 0; i < foundGroups.size(); i++) {
            if (!Objects.equals(foundGroups.get(i), groupsToTest.get(i))) {
                return false;
            }
        }
        return true;
    }

    private String makeTestRow(final String row, final String binary, final List<Integer> unknownLocations) {
        final String mappedBinary = binary.replace("0", ".").replace("1", "#");
        String testRow = row;
        for (int i = 0; i < unknownLocations.size(); i++) {
            testRow = this.replaceCharacter(testRow, unknownLocations.get(i), mappedBinary.substring(i, i + 1));
        }
        return testRow;
    }

    private String replaceCharacter(final String original, final Integer index, final String replacement) {
        return original.substring(0, index) + replacement + original.substring(index + 1, original.length());
    }

    private String leftPad(final String replace, final String binaryString, final int length) {
        final int rest = length - binaryString.length();
        return replace.repeat(rest) + binaryString;
    }

    @Override
    public String part2(final String[] input) {

        long combos = 0;
        for (final String line : input) {
            final String[] parts = line.split(" ");
            final String row = parts[0];
            final List<Integer> groups = Arrays.stream(parts[1].split(",")).map(Integer::valueOf).collect(Collectors.toList());
            final long result = this.countWithRecursion(row, groups);
            System.out.println(line + " : " + result);
            combos = combos + result;
        }

        return String.valueOf(combos);
    }

    private long countWithRecursion(final String line, final List<Integer> groups) {

        System.out.println(line + " : " + groups.stream().map(String::valueOf).collect(Collectors.joining(",")));

        long result = 0;

        if (line.isEmpty()) {
            result = (groups.isEmpty()) ? 1 : 0;
            System.out.println("  empty line = " + result);
            return result;
        }

        if (groups.isEmpty()) {
            result = line.contains("#") ? 0 : 1;
            System.out.println("  empty groups = " + result);
            return result;
        }

        final String firstSpring = line.substring(0, 1);
        if (".?".contains(firstSpring)) {
            result = result + this.countWithRecursion(line.substring(1), groups);
        }
        if ("#?".contains(firstSpring)) {
            final String startOfLine = line.substring(0, groups.get(0));
            String endOfLine = "";
            String endOfLineFirst = "";
            if (line.length() > (groups.get(0) + 1)) {
                endOfLine = line.substring(groups.get(0) + 1);
                if (!endOfLine.isEmpty()) {
                    endOfLineFirst = endOfLine.substring(0, 1);
                }

            }
            if (
                    groups.get(0) <= line.length() &&
                            !(startOfLine.contains(".")) &&
                            (
                                    groups.get(0) == line.length() ||
                                            !(endOfLineFirst.equalsIgnoreCase("#"))
                            )
            ) {
                result = result + this.countWithRecursion(endOfLine, this.restOfGroups(groups, 1));
            }
        }

        // https://www.youtube.com/watch?v=g3Ms5e7Jdqo&t=602s
        System.out.println("  " + result);
        return result;
    }

    private List<Integer> restOfGroups(final List<Integer> groups, final int start) {
        final List<Integer> restOfGroups = new ArrayList<>();
        for (int i = start; i < groups.size(); i++) {
            restOfGroups.add(groups.get(i));
        }
        return restOfGroups;
    }
}
